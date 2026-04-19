package ru.vladislav.cost_analysis_nau;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.vladislav.cost_analysis_nau.entity.Account;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;
import ru.vladislav.cost_analysis_nau.repository.AccountRepository;
import ru.vladislav.cost_analysis_nau.repository.CategoryRepository;
import ru.vladislav.cost_analysis_nau.repository.TransactionRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
class TransactionRepositoryTest {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    TransactionRepositoryTest(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Test
    void testFindByAccountIdOrderByTimestampDesc() {
        Account account = new Account();
        account.setName("Account_" + UUID.randomUUID());
        account.setBalance(1000.0);
        account.setDescription("Test account");
        account = accountRepository.save(account);

        Category category = new Category();
        category.setName("Category_" + UUID.randomUUID());
        category.setIncome(false);
        category = categoryRepository.save(category);

        Transaction transaction1 = new Transaction();
        transaction1.setAccount(account);
        transaction1.setCategory(category);
        transaction1.setAmount(100.0);
        transaction1.setIncome(false);
        transaction1.setCreatedAt(Timestamp.from(Instant.now().minusSeconds(60)));
        transactionRepository.save(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setAccount(account);
        transaction2.setCategory(category);
        transaction2.setAmount(200.0);
        transaction2.setIncome(false);
        transaction2.setCreatedAt(Timestamp.from(Instant.now()));
        transactionRepository.save(transaction2);

        List<Transaction> foundTransactions =
                transactionRepository.findByAccountIdOrderByTimestampDesc(account.getId());

        Assertions.assertNotNull(foundTransactions);
        Assertions.assertEquals(2, foundTransactions.size());
        Assertions.assertEquals(transaction2.getId(), foundTransactions.get(0).getId());
        Assertions.assertEquals(transaction1.getId(), foundTransactions.get(1).getId());
    }

    @Test
    void testFindTransactionsByAccountAndCategory() {
        Account account = new Account();
        account.setName("Account_" + UUID.randomUUID());
        account.setBalance(500.0);
        account.setDescription("Test account");
        account = accountRepository.save(account);

        Category category = new Category();
        category.setName("Food_" + UUID.randomUUID());
        category.setIncome(false);
        category = categoryRepository.save(category);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setAmount(150.0);
        transaction.setIncome(false);
        transaction.setCreatedAt(Timestamp.from(Instant.now()));
        transaction = transactionRepository.save(transaction);

        List<Transaction> foundTransactions =
                transactionRepository.findTransactionsByAccountIdAndCategory(account.getId(), category);

        Assertions.assertNotNull(foundTransactions);
        Assertions.assertEquals(1, foundTransactions.size());
        Assertions.assertEquals(transaction.getId(), foundTransactions.get(0).getId());
        Assertions.assertEquals(account.getId(), foundTransactions.get(0).getAccount().getId());
        Assertions.assertEquals(category.getId(), foundTransactions.get(0).getCategory().getId());
    }

    @Test
    void testFindTransactionsByAccountAndCategoryEmpty() {
        Account account = new Account();
        account.setName("Account_" + UUID.randomUUID());
        account.setBalance(700.0);
        account.setDescription("Test account");
        account = accountRepository.save(account);

        Category category = new Category();
        category.setName("Transport_" + UUID.randomUUID());
        category.setIncome(false);
        category = categoryRepository.save(category);

        List<Transaction> foundTransactions =
                transactionRepository.findTransactionsByAccountIdAndCategory(account.getId(), category);

        Assertions.assertNotNull(foundTransactions);
        Assertions.assertTrue(foundTransactions.isEmpty());
    }
}