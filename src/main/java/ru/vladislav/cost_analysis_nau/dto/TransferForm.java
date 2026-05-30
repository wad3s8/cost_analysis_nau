package ru.vladislav.cost_analysis_nau.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferForm {
    private Long fromAccountId;
    private Long toAccountId;
    private Double amount;
    private String description;
}
