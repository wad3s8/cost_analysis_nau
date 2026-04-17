package ru.vladislav.cost_analysis_nau.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vladislav.cost_analysis_nau.entity.UserApp;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;


@RequiredArgsConstructor
@Controller
public class AuthController {
    private final UsersRepository usersRepository;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(UserApp user, Model model) {
        try {
            usersRepository.save(user);
            return "redirect:/login";
        } catch (Exception ex) {
            model.addAttribute("message", "User exists");
            return "registration";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
