package ru.vladislav.cost_analysis_nau.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Сервис аналитики: агрегированные показатели по транзакциям за период. */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;

    /** Сумма доходов пользователя за указанный период. */
    public double getTotalIncome(User user, LocalDateTime from, LocalDateTime to) {
        Double result = transactionRepository.sumByUserAndTypeAndPeriod(user.getId(), true, from, to);
        return result != null ? result : 0.0;
    }

    /** Сумма расходов пользователя за указанный период. */
    public double getTotalExpense(User user, LocalDateTime from, LocalDateTime to) {
        Double result = transactionRepository.sumByUserAndTypeAndPeriod(user.getId(), false, from, to);
        return result != null ? result : 0.0;
    }

    /** Доходы пользователя за период, сгруппированные по категориям (категория → сумма). */
    public Map<String, Double> getIncomeByCategory(User user, LocalDateTime from, LocalDateTime to) {
        return toMap(transactionRepository.sumGroupedByCategoryAndPeriod(user.getId(), true, from, to));
    }

    /** Расходы пользователя за период, сгруппированные по категориям (категория → сумма). */
    public Map<String, Double> getExpenseByCategory(User user, LocalDateTime from, LocalDateTime to) {
        return toMap(transactionRepository.sumGroupedByCategoryAndPeriod(user.getId(), false, from, to));
    }

    private Map<String, Double> toMap(List<Object[]> rows) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (Double) row[1]);
        }
        return result;
    }
}
