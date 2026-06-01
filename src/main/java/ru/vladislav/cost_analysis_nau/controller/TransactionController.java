package ru.vladislav.cost_analysis_nau.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vladislav.cost_analysis_nau.dto.TransactionForm;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;
import ru.vladislav.cost_analysis_nau.service.AccountService;
import ru.vladislav.cost_analysis_nau.service.CategoryService;
import ru.vladislav.cost_analysis_nau.service.TransactionService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final UsersRepository usersRepository;

    private User currentUser(Authentication auth) {
        return usersRepository.findByLogin(auth.getName());
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isIncome,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Model model, Authentication auth) {

        User user = currentUser(auth);
        var transactions = (categoryId != null || isIncome != null || from != null || to != null)
                ? transactionService.getFiltered(user, categoryId, isIncome, from, to)
                : transactionService.getByUser(user);

        model.addAttribute("transactions", transactions);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("selectedType", isIncome);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "transactions/list";
    }

    @GetMapping("/new")
    public String newForm(Model model, Authentication auth) {
        User user = currentUser(auth);
        model.addAttribute("form", new TransactionForm());
        model.addAttribute("accounts", accountService.getAccountsByUser(user));
        model.addAttribute("categories", categoryService.getAll());
        return "transactions/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute TransactionForm form, Authentication auth, Model model) {
        try {
            transactionService.create(form, currentUser(auth));
            return "redirect:/transactions";
        } catch (Exception e) {
            log.error("Error creating transaction: {}", e.getMessage());
            User user = currentUser(auth);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("form", form);
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            model.addAttribute("categories", categoryService.getAll());
            return "transactions/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth) {
        transactionService.delete(id, currentUser(auth));
        return "redirect:/transactions";
    }
}
