package ru.vladislav.cost_analysis_nau.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;
import ru.vladislav.cost_analysis_nau.service.AnalyticsService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UsersRepository usersRepository;

    private User currentUser(Authentication auth) {
        return usersRepository.findByLogin(auth.getName());
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Model model, Authentication auth) {

        if (from == null) from = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        if (to == null) to = LocalDateTime.now();

        User user = currentUser(auth);

        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("totalIncome", analyticsService.getTotalIncome(user, from, to));
        model.addAttribute("totalExpense", analyticsService.getTotalExpense(user, from, to));
        model.addAttribute("balance", analyticsService.getTotalIncome(user, from, to)
                - analyticsService.getTotalExpense(user, from, to));
        model.addAttribute("incomeByCategory", analyticsService.getIncomeByCategory(user, from, to));
        model.addAttribute("expenseByCategory", analyticsService.getExpenseByCategory(user, from, to));

        return "analytics/index";
    }
}
