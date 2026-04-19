package ru.vladislav.cost_analysis_nau.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vladislav.cost_analysis_nau.entity.Report;
import ru.vladislav.cost_analysis_nau.entity.ReportStatus;
import ru.vladislav.cost_analysis_nau.entity.Transaction;
import ru.vladislav.cost_analysis_nau.repository.ReportRepository;
import ru.vladislav.cost_analysis_nau.repository.TransactionRepository;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UsersRepository userRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Создаёт запись отчёта в БД со статусом CREATED и возвращает её id.
     */
    public Long createReport() {
        Report report = new Report(ReportStatus.CREATED);
        reportRepository.save(report);
        return report.getId();
    }

    /**
     * Возвращает содержимое отчёта по id.
     * Если отчёт не найден — возвращает сообщение об ошибке.
     */
    public String getReport(Long id) {
        return reportRepository.findById(id)
                .map(report -> switch (report.getStatus()) {
                    case CREATED -> "Отчёт ещё формируется, попробуйте позже.";
                    case ERROR -> "Формирование отчёта завершилось с ошибкой.";
                    case COMPLETED -> report.getContent();
                })
                .orElse("Отчёт с id=" + id + " не найден.");
    }

    /**
     * Асинхронно формирует отчёт.
     * Подсчёт пользователей и получение транзакций выполняются в отдельных потоках.
     */
    public CompletableFuture<Void> generateReport(Long reportId) {
        return CompletableFuture.runAsync(() -> {
            long totalStart = System.currentTimeMillis();

            // Результаты из потоков
            AtomicLong userCount = new AtomicLong();
            AtomicReference<List<Transaction>> transactions = new AtomicReference<>();
            AtomicLong userTime = new AtomicLong();
            AtomicLong transactionTime = new AtomicLong();

            // Флаг ошибки
            AtomicReference<String> errorMessage = new AtomicReference<>();

            // Поток 1: подсчёт пользователей
            Thread userThread = new Thread(() -> {
                try {
                    long start = System.currentTimeMillis();
                    userCount.set(userRepository.count());
                    userTime.set(System.currentTimeMillis() - start);
                } catch (Exception e) {
                    errorMessage.set("Ошибка при подсчёте пользователей: " + e.getMessage());
                }
            });

            // Поток 2: получение списка транзакций
            Thread transactionThread = new Thread(() -> {
                try {
                    long start = System.currentTimeMillis();
                    List<Transaction> list = new ArrayList<>((Collection<? extends Transaction>) transactionRepository.findAll());
                    transactions.set(list);
                    transactionTime.set(System.currentTimeMillis() - start);
                } catch (Exception e) {
                    errorMessage.set("Ошибка при получении транзакций: " + e.getMessage());
                }
            });

            userThread.start();
            transactionThread.start();

            try {
                userThread.join();
                transactionThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                saveError(reportId, "Поток был прерван: " + e.getMessage());
                return;
            }

            // Если в одном из потоков возникла ошибка
            if (errorMessage.get() != null) {
                saveError(reportId, errorMessage.get());
                return;
            }

            long totalTime = System.currentTimeMillis() - totalStart;

            // Формируем HTML-отчёт
            String html = buildHtml(
                    userCount.get(),
                    transactions.get(),
                    userTime.get(),
                    transactionTime.get(),
                    totalTime
            );

            // Сохраняем результат
            reportRepository.findById(reportId).ifPresent(report -> {
                report.setStatus(ReportStatus.COMPLETED);
                report.setContent(html);
                reportRepository.save(report);
            });
        });
    }

    private void saveError(Long reportId, String message) {
        reportRepository.findById(reportId).ifPresent(report -> {
            report.setStatus(ReportStatus.ERROR);
            report.setContent("<p>Ошибка: " + message + "</p>");
            reportRepository.save(report);
        });
    }

    private String buildHtml(long userCount,
                             List<Transaction> transactions,
                             long userTime,
                             long transactionTime,
                             long totalTime) {

        StringBuilder sb = new StringBuilder();
        sb.append("""
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <title>Отчёт</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 40px; }
                        h1 { color: #333; }
                        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
                        th, td { border: 1px solid #ccc; padding: 8px 12px; text-align: left; }
                        th { background-color: #f4f4f4; }
                        .stats { margin-top: 20px; background: #f9f9f9;
                                 padding: 16px; border-radius: 8px; }
                        .stats p { margin: 4px 0; }
                    </style>
                </head>
                <body>
                <h1>Статистика приложения</h1>
                """);

        // Блок: количество пользователей
        sb.append("<h2>Пользователи</h2>");
        sb.append("<p>Зарегистрированных пользователей: <strong>")
                .append(userCount)
                .append("</strong></p>");

        // Блок: таблица транзакций
        sb.append("<h2>Транзакции</h2>");
        if (transactions.isEmpty()) {
            sb.append("<p>Транзакции отсутствуют.</p>");
        } else {
            sb.append("""
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Счёт</th>
                                <th>Категория</th>
                                <th>Сумма</th>
                                <th>Тип</th>
                                <th>Дата</th>
                            </tr>
                        </thead>
                        <tbody>
                    """);
            for (Transaction t : transactions) {
                sb.append("<tr>");
                sb.append("<td>").append(t.getId()).append("</td>");
                sb.append("<td>").append(t.getAccount() != null ? t.getAccount().getName() : "—").append("</td>");
                sb.append("<td>").append(t.getCategory() != null ? t.getCategory().getName() : "—").append("</td>");
                sb.append("<td>").append(t.getAmount()).append("</td>");
                sb.append("<td>").append(t.isIncome() ? "Доход" : "Расход").append("</td>");
                sb.append("<td>").append(t.getCreatedAt()).append("</td>");
                sb.append("</tr>");
            }
            sb.append("</tbody></table>");
        }

        // Блок: время выполнения
        sb.append("<div class=\"stats\">");
        sb.append("<h2>Время формирования</h2>");
        sb.append("<p>Подсчёт пользователей: <strong>").append(userTime).append(" мс</strong></p>");
        sb.append("<p>Получение транзакций: <strong>").append(transactionTime).append(" мс</strong></p>");
        sb.append("<p>Общее время: <strong>").append(totalTime).append(" мс</strong></p>");
        sb.append("</div>");

        sb.append("</body></html>");
        return sb.toString();
    }
}