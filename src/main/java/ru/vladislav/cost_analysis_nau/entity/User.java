package ru.vladislav.cost_analysis_nau.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class User {
    private final Long id;

    @Setter
    private String login;

    @Setter
    private String password;
}
