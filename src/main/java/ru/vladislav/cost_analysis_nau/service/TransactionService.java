package ru.vladislav.cost_analysis_nau.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladislav.cost_analysis_nau.entity.Account;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;
import ru.vladislav.cost_analysis_nau.repository.AccountRepository;
import ru.vladislav.cost_analysis_nau.repository.CategoryRepository;
import ru.vladislav.cost_analysis_nau.repository.TransactionRepository;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void createTransaction(Long accountId,
                                  Long categoryId,
                                  Double amount,
                                  boolean isIncome) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));


        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));


        if (category.isIncome() != isIncome) {
            throw new RuntimeException("Category type mismatch");
        }


        if (isIncome) {
            account.setBalance(account.getBalance() + amount);
        } else {
            if (account.getBalance() < amount) {
                throw new RuntimeException("Not enough balance");
            }
            account.setBalance(account.getBalance() - amount);
        }


        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setIncome(isIncome);
        transaction.setCreatedAt(Timestamp.from(Instant.now()));

        transactionRepository.save(transaction);

        accountRepository.save(account);
    }
}
