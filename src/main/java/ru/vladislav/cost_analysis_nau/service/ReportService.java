package ru.vladislav.cost_analysis_nau.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
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
    private final TemplateEngine templateEngine;


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

    public CompletableFuture<Void> generateReport(Long reportId) {
        return CompletableFuture.runAsync(() -> {
            long totalStart = System.currentTimeMillis();

            AtomicLong userCount = new AtomicLong();
            AtomicReference<List<Transaction>> transactions = new AtomicReference<>();
            AtomicLong userTime = new AtomicLong();
            AtomicLong transactionTime = new AtomicLong();
            AtomicReference<String> errorMessage = new AtomicReference<>();

            Thread userThread = new Thread(() -> {
                try {
                    long start = System.currentTimeMillis();
                    userCount.set(userRepository.count());
                    userTime.set(System.currentTimeMillis() - start);
                } catch (Exception e) {
                    errorMessage.set("Ошибка при подсчёте пользователей: " + e.getMessage());
                }
            });

            Thread transactionThread = new Thread(() -> {
                try {
                    long start = System.currentTimeMillis();
                    transactions.set(new ArrayList<>((Collection) transactionRepository.findAll()));
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

            if (errorMessage.get() != null) {
                saveError(reportId, errorMessage.get());
                return;
            }

            long totalTime = System.currentTimeMillis() - totalStart;

            // Передаём переменные в шаблон
            Context context = new Context();
            context.setVariable("userCount", userCount.get());
            context.setVariable("transactions", transactions.get());
            context.setVariable("userTime", userTime.get());
            context.setVariable("transactionTime", transactionTime.get());
            context.setVariable("totalTime", totalTime);

            String html = templateEngine.process("report", context);  // ← рендерим шаблон

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
}
