package ru.vladislav.cost_analysis_nau.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vladislav.cost_analysis_nau.dto.TransferForm;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;
import ru.vladislav.cost_analysis_nau.service.AccountService;
import ru.vladislav.cost_analysis_nau.service.TransferService;

@Controller
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private static final Logger log = LoggerFactory.getLogger(TransferController.class);
    private final TransferService transferService;
    private final AccountService accountService;
    private final UsersRepository usersRepository;

    private User currentUser(Authentication auth) {
        return usersRepository.findByLogin(auth.getName());
    }

    @GetMapping
    public String list(Model model, Authentication auth) {
        User user = currentUser(auth);
        model.addAttribute("transfers", transferService.getTransfersByUser(user));
        model.addAttribute("accounts", accountService.getAccountsByUser(user));
        model.addAttribute("form", new TransferForm());
        return "transfers/list";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute TransferForm form, Authentication auth, Model model) {
        try {
            transferService.create(form, currentUser(auth));
            return "redirect:/transfers";
        } catch (Exception e) {
            log.error("Error creating transfer: {}", e.getMessage());
            User user = currentUser(auth);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("transfers", transferService.getTransfersByUser(user));
            model.addAttribute("accounts", accountService.getAccountsByUser(user));
            model.addAttribute("form", form);
            return "transfers/list";
        }
    }
}
