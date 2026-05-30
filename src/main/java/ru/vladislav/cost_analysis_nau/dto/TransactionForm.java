package ru.vladislav.cost_analysis_nau.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionForm {
    private Long accountId;
    private Long categoryId;
    private Double amount;
    private boolean isIncome;
    private String description;
}
