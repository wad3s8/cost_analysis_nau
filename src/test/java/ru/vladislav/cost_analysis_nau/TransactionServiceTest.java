package ru.vladislav.cost_analysis_nau;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladislav.cost_analysis_nau.dto.TransactionForm;
import ru.vladislav.cost_analysis_nau.entity.*;
import ru.vladislav.cost_analysis_nau.repository.AccountRepository;
import ru.vladislav.cost_analysis_nau.repository.CategoryRepository;
import ru.vladislav.cost_analysis_nau.repository.TransactionRepository;
import ru.vladislav.cost_analysis_nau.service.TransactionService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Account account;
    private Category incomeCategory;
    private Category expenseCategory;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setLogin("testuser");

        account = new Account();
        account.setId(1L);
        account.setName("Test Account");
        account.setBalance(1000.0);
        account.setUser(user);

        incomeCategory = new Category();
        incomeCategory.setId(1L);
        incomeCategory.setName("Salary");
        incomeCategory.setIncome(true);

        expenseCategory = new Category();
        expenseCategory.setId(2L);
        expenseCategory.setName("Food");
        expenseCategory.setIncome(false);
    }

    private TransactionForm form(Long accountId, Long categoryId, double amount, boolean isIncome) {
        TransactionForm f = new TransactionForm();
        f.setAccountId(accountId);
        f.setCategoryId(categoryId);
        f.setAmount(amount);
        f.setIncome(isIncome);
        return f;
    }

    // ─── Позитивные сценарии ──────────────────────────────────────────────────

    @Test
    @DisplayName("Успешное создание дохода: баланс увеличивается")
    void create_Income_BalanceIncreased() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(incomeCategory));

        transactionService.create(form(1L, 1L, 500.0, true), user);

        assertEquals(1500.0, account.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    @DisplayName("Успешное создание расхода: баланс уменьшается")
    void create_Expense_BalanceDecreased() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(expenseCategory));

        transactionService.create(form(1L, 2L, 200.0, false), user);

        assertEquals(800.0, account.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    @DisplayName("Расход на всю сумму баланса: баланс становится 0")
    void create_Expense_ExactBalance_BalanceBecomesZero() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(expenseCategory));

        transactionService.create(form(1L, 2L, 1000.0, false), user);

        assertEquals(0.0, account.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // ─── Негативные сценарии ─────────────────────────────────────────────────

    @Test
    @DisplayName("Счёт не найден: выбрасывается RuntimeException")
    void create_AccountNotFound_ThrowsException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> transactionService.create(form(99L, 1L, 100.0, true), user));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Категория не найдена: выбрасывается RuntimeException")
    void create_CategoryNotFound_ThrowsException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> transactionService.create(form(1L, 99L, 100.0, true), user));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Несоответствие типа категории: выбрасывается RuntimeException")
    void create_CategoryTypeMismatch_ThrowsException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(incomeCategory));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transactionService.create(form(1L, 1L, 100.0, false), user));

        assertEquals("Category type does not match transaction type", ex.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Недостаточно средств на счёте: выбрасывается RuntimeException")
    void create_NotEnoughBalance_ThrowsException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(expenseCategory));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transactionService.create(form(1L, 2L, 9999.0, false), user));

        assertEquals("Insufficient balance", ex.getMessage());
        assertEquals(1000.0, account.getBalance());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Расход превышает баланс на 1 копейку: выбрасывается исключение")
    void create_BalanceExceededBySmallAmount_ThrowsException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(expenseCategory));

        assertThrows(RuntimeException.class,
                () -> transactionService.create(form(1L, 2L, 1000.01, false), user));
    }
}
