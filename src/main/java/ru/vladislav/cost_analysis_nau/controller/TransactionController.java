package ru.vladislav.cost_analysis_nau.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;
import ru.vladislav.cost_analysis_nau.repository.AccountRepository;
import ru.vladislav.cost_analysis_nau.repository.CategoryRepository;
import ru.vladislav.cost_analysis_nau.repository.TransactionRepository;

import java.util.List;

@RequiredArgsConstructor
@RestController("/transaction-custom")
public class TransactionController {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/{accountId}/byCategory")
    public List<Transaction> getTransactionsByAccountAndCategory(@PathVariable Long accountId,
                                                                 @RequestParam String nameCategory) {
        if (!categoryRepository.existsCategoryByName(nameCategory)) {
            throw new ResourceNotFoundException("Category " + nameCategory + " not found");
        }
        if (!accountRepository.existsAccountById(accountId)) {
            throw new ResourceNotFoundException("Account " + accountId + " not found");
        }
        Category category = categoryRepository.findCategoryByName(nameCategory);
        return transactionRepository.findTransactionsByAccountIdAndCategory(accountId, category);
    }

    @GetMapping("/{accountId}/byAccount")
    List<Transaction> findTransactionsByAccount(@PathVariable Long accountId){
        if (!accountRepository.existsAccountById(accountId)) {
            throw new ResourceNotFoundException("Account " + accountId + " not found");
        }
        return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId);
    }
}
