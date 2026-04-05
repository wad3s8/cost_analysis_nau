package ru.vladislav.cost_analysis_nau;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.vladislav.cost_analysis_nau.entity.Account;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;
import ru.vladislav.cost_analysis_nau.repository.AccountRepository;
import ru.vladislav.cost_analysis_nau.repository.CategoryRepository;
import ru.vladislav.cost_analysis_nau.repository.TransactionRepository;
import ru.vladislav.cost_analysis_nau.service.TransactionService;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class TransactionServiceTest {

    private final TransactionService transactionService;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    TransactionServiceTest(TransactionService transactionService,
                           AccountRepository accountRepository,
                           CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Test
    void testCreateTransactionSuccess() {
        Account account = new Account();
        account.setName("Account_" + UUID.randomUUID());
        account.setBalance(1000.0);
        account.setDescription("Test account");
        account = accountRepository.save(account);

        Category category = new Category();
        category.setName("Salary_" + UUID.randomUUID());
        category.setIncome(true);
        category = categoryRepository.save(category);

        transactionService.createTransaction(account.getId(), category.getId(), 500.0, true);

        Account updatedAccount = accountRepository.findById(account.getId()).orElseThrow();
        List<Transaction> transactions =
                transactionRepository.findByAccountIdOrderByTimestampDesc(account.getId());

        Assertions.assertEquals(1500.0, updatedAccount.getBalance());
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(500.0, transactions.get(0).getAmount());
        Assertions.assertTrue(transactions.get(0).isIncome());
    }

    /**
     * Негативный кейс: недостаточно средств, транзакция должна быть откатана
     */
    @Test
    void testCreateTransactionRollback() {
        Account account = new Account();
        account.setName("Account_" + UUID.randomUUID());
        account.setBalance(100.0);
        account.setDescription("Test account");
        account = accountRepository.save(account);

        Category category = new Category();
        category.setName("Food_" + UUID.randomUUID());
        category.setIncome(false);
        category = categoryRepository.save(category);

        Account finalAccount = account;
        Category finalCategory = category;
        Assertions.assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(finalAccount.getId(), finalCategory.getId(), 500.0, false);
        });

        Account unchangedAccount = accountRepository.findById(account.getId()).orElseThrow();
        List<Transaction> transactions =
                transactionRepository.findByAccountIdOrderByTimestampDesc(account.getId());

        Assertions.assertEquals(100.0, unchangedAccount.getBalance());
        Assertions.assertTrue(transactions.isEmpty());
    }
}