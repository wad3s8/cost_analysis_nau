package ru.vladislav.cost_analysis_nau.controller;

import lombok.RequiredArgsConstructor;
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
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;  // Добавляем BCrypt для хеширования пароля

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(CreateUser createUser, Model model) {
        try {
            // Проверка на существующего пользователя с таким логином
            if (usersRepository.findByLogin(createUser.getLogin().toLowerCase(Locale.ROOT)) != null) {
                model.addAttribute("message", "User with this login already exists");
                return "registration";
            }

            // Создание нового пользователя
            User user = new User();
            user.setLogin(createUser.getLogin());
            user.setPassword(passwordEncoder.encode(createUser.getPassword()));  // Хеширование пароля

            // Сохранение нового пользователя в базе
            usersRepository.save(user);

            return "redirect:/login";
        } catch (Exception ex) {
            model.addAttribute("message", "An error occurred during registration");
            return "registration";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}