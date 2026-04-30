package ru.vladislav.cost_analysis_nau.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUser {
    private String login;
    private String password;
}
