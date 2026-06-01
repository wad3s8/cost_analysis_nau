package ru.vladislav.cost_analysis_nau.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladislav.cost_analysis_nau.dto.TransactionForm;
import ru.vladislav.cost_analysis_nau.entity.Account;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.AccountRepository;
import ru.vladislav.cost_analysis_nau.repository.CategoryRepository;
import ru.vladislav.cost_analysis_nau.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public List<Transaction> getByUser(User user) {
        return transactionRepository.findByUserId(user.getId());
    }

    public List<Transaction> getFiltered(User user, Long categoryId, Boolean isIncome,
                                         LocalDateTime from, LocalDateTime to) {
        return transactionRepository.findFiltered(user.getId(), categoryId, isIncome, from, to);
    }

    public Transaction getById(Long id, User user) {
        return transactionRepository.findById(id)
                .filter(t -> t.getAccount().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Transactional
    public Transaction create(TransactionForm form, User user) {
        Account account = accountRepository.findById(form.getAccountId())
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.isIncome() != form.isIncome()) {
            throw new RuntimeException("Category type does not match transaction type");
        }

        if (form.isIncome()) {
            account.setBalance(account.getBalance() + form.getAmount());
        } else {
            if (account.getBalance() < form.getAmount()) {
                throw new RuntimeException("Insufficient balance");
            }
            account.setBalance(account.getBalance() - form.getAmount());
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setAmount(form.getAmount());
        transaction.setIncome(form.isIncome());
        transaction.setDescription(form.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());

        accountRepository.save(account);
        log.info("Transaction created: {} {} on account {} by user '{}'",
                form.isIncome() ? "income" : "expense", form.getAmount(), account.getId(), user.getLogin());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction update(Long id, TransactionForm form, User user) {
        Transaction transaction = getById(id, user);

        Account oldAccount = transaction.getAccount();
        if (transaction.isIncome()) {
            oldAccount.setBalance(oldAccount.getBalance() - transaction.getAmount());
        } else {
            oldAccount.setBalance(oldAccount.getBalance() + transaction.getAmount());
        }

        Account newAccount = accountRepository.findById(form.getAccountId())
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.isIncome() != form.isIncome()) {
            throw new RuntimeException("Category type does not match transaction type");
        }

        if (form.isIncome()) {
            newAccount.setBalance(newAccount.getBalance() + form.getAmount());
        } else {
            if (newAccount.getBalance() < form.getAmount()) {
                throw new RuntimeException("Insufficient balance");
            }
            newAccount.setBalance(newAccount.getBalance() - form.getAmount());
        }

        if (!oldAccount.getId().equals(newAccount.getId())) {
            accountRepository.save(oldAccount);
        }
        accountRepository.save(newAccount);

        transaction.setAccount(newAccount);
        transaction.setCategory(category);
        transaction.setAmount(form.getAmount());
        transaction.setIncome(form.isIncome());
        transaction.setDescription(form.getDescription());

        log.info("Transaction {} updated by user '{}'", id, user.getLogin());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void delete(Long id, User user) {
        Transaction transaction = getById(id, user);

        Account account = transaction.getAccount();
        // Reverse the effect on balance
        if (transaction.isIncome()) {
            account.setBalance(account.getBalance() - transaction.getAmount());
        } else {
            account.setBalance(account.getBalance() + transaction.getAmount());
        }
        accountRepository.save(account);
        transactionRepository.delete(transaction);
        log.info("Transaction {} deleted by user '{}'", id, user.getLogin());
    }
}
