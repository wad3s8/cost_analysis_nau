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
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;
import ru.vladislav.cost_analysis_nau.service.AccountService;
import ru.vladislav.cost_analysis_nau.service.TransactionService;

@RequiredArgsConstructor
@Controller
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);
    private final UsersRepository usersRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;

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
}
