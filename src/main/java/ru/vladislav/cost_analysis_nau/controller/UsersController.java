package ru.vladislav.cost_analysis_nau.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;

@RequiredArgsConstructor
@Controller
public class  UsersController {
    private final UsersRepository usersRepository;

    @GetMapping("/users/view/list")
    public String getListUsers(Model model){
        model.addAttribute("users", usersRepository.findAll());
        return "users";
    }
}
