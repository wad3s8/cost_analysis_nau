package ru.vladislav.cost_analysis_nau.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vladislav.cost_analysis_nau.dto.CreateUser;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;

import java.util.Locale;

@RequiredArgsConstructor
@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(CreateUser createUser, Model model) {
        try {
            if (usersRepository.findByLogin(createUser.getLogin().toLowerCase(Locale.ROOT)) != null) {
                log.warn("Registration failed: login '{}' already exists", createUser.getLogin());
                model.addAttribute("message", "User with this login already exists");
                return "registration";
            }

            User user = new User();
            user.setLogin(createUser.getLogin());
            user.setPassword(passwordEncoder.encode(createUser.getPassword()));
            usersRepository.save(user);

            log.info("New user registered: '{}'", createUser.getLogin());
            return "redirect:/login";
        } catch (Exception ex) {
            log.error("Registration error for login '{}': {}", createUser.getLogin(), ex.getMessage());
            model.addAttribute("message", "An error occurred during registration");
            return "registration";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}