package ru.vladislav.cost_analysis_nau.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vladislav.cost_analysis_nau.dto.AccountForm;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;
import ru.vladislav.cost_analysis_nau.service.AccountService;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;
    private final UsersRepository usersRepository;

    private User currentUser(Authentication auth) {
        return usersRepository.findByLogin(auth.getName());
    }

    @GetMapping
    public String list(Model model, Authentication auth) {
        User user = currentUser(auth);
        model.addAttribute("accounts", accountService.getAccountsByUser(user));
        model.addAttribute("totalBalance", accountService.getTotalBalance(user));
        return "accounts/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new AccountForm());
        return "accounts/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute AccountForm form, Authentication auth, Model model) {
        try {
            accountService.create(form, currentUser(auth));
            return "redirect:/accounts";
        } catch (Exception e) {
            log.error("Error creating account: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("form", form);
            return "accounts/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Authentication auth) {
        User user = currentUser(auth);
        var account = accountService.getAccountByIdAndUser(id, user);
        AccountForm form = new AccountForm();
        form.setName(account.getName());
        form.setBalance(account.getBalance());
        form.setDescription(account.getDescription());
        model.addAttribute("form", form);
        model.addAttribute("accountId", id);
        return "accounts/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute AccountForm form,
                         Authentication auth, Model model) {
        try {
            accountService.update(id, form, currentUser(auth));
            return "redirect:/accounts";
        } catch (Exception e) {
            log.error("Error updating account {}: {}", id, e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("form", form);
            model.addAttribute("accountId", id);
            return "accounts/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth) {
        accountService.delete(id, currentUser(auth));
        return "redirect:/accounts";
    }
}
