package ru.vladislav.cost_analysis_nau.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountForm {
    private String name;
    private Double balance;
    private String description;
}
