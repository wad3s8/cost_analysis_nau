package ru.vladislav.cost_analysis_nau.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.AccountRepository;
import ru.vladislav.cost_analysis_nau.repository.TransactionRepository;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;
import ru.vladislav.cost_analysis_nau.service.AccountService;
import ru.vladislav.cost_analysis_nau.service.TransactionService;

/** Контроллер дашборда и административных страниц: управление пользователями и системная статистика. */
@RequiredArgsConstructor
@Controller
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);
    private final UsersRepository usersRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping("/")
    public String dashboard(Model model, Authentication auth) {
        User user = usersRepository.findByLogin(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("totalBalance", accountService.getTotalBalance(user));
        model.addAttribute("accounts", accountService.getAccountsByUser(user));
        model.addAttribute("recentTransactions", transactionService.getByUser(user)
                .stream().limit(5).toList());
        return "dashboard";
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        model.addAttribute("users", usersRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/admin/users/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id) {
        usersRepository.deleteById(id);
        log.info("Admin deleted user {}", id);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public String systemStats(Model model) {
        model.addAttribute("userCount", usersRepository.count());
        model.addAttribute("accountCount", accountRepository.count());
        model.addAttribute("transactionCount", transactionRepository.count());
        model.addAttribute("totalIncome", transactionRepository.sumAllByType(true));
        model.addAttribute("totalExpense", transactionRepository.sumAllByType(false));
        model.addAttribute("incomeByUser", transactionRepository.sumGroupedByUser(true));
        model.addAttribute("expenseByUser", transactionRepository.sumGroupedByUser(false));
        return "admin/stats";
    }
}
