package ru.vladislav.cost_analysis_nau;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.vladislav.cost_analysis_nau.controller.TransactionController;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;
import ru.vladislav.cost_analysis_nau.repository.AccountRepository;
import ru.vladislav.cost_analysis_nau.repository.CategoryRepository;
import ru.vladislav.cost_analysis_nau.repository.TransactionRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    private Category foodCategory;
    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(transactionController)
                .build();

        foodCategory = new Category();
        foodCategory.setId(1L);
        foodCategory.setName("Food");
        foodCategory.setIncome(false);

        sampleTransaction = new Transaction();
        sampleTransaction.setId(1L);
        sampleTransaction.setAmount(250.0);
        sampleTransaction.setIncome(false);
        sampleTransaction.setCreatedAt(Timestamp.from(Instant.now()));
        sampleTransaction.setCategory(foodCategory);
    }

    // ─── GET /{accountId}/byCategory ─────────────────────────────────────────

    @Test
    @DisplayName("byCategory: категория и счёт существуют — возвращает 200 и список транзакций")
    void getByCategory_ValidParams_Returns200() throws Exception {
        when(categoryRepository.existsCategoryByName("Food")).thenReturn(true);
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findCategoryByName("Food")).thenReturn(foodCategory);
        when(transactionRepository.findTransactionsByAccountIdAndCategory(1L, foodCategory))
                .thenReturn(List.of(sampleTransaction));

        mockMvc.perform(get("/transaction-custom/1/byCategory")
                        .param("nameCategory", "Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].amount").value(250.0));
    }

    @Test
    @DisplayName("byCategory: категория не существует — возвращает 404")
    void getByCategory_CategoryNotFound_Returns404() throws Exception {
        when(categoryRepository.existsCategoryByName("Unknown")).thenReturn(false);

        mockMvc.perform(get("/transaction-custom/1/byCategory")
                        .param("nameCategory", "Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("byCategory: счёт не существует — возвращает 404")
    void getByCategory_AccountNotFound_Returns404() throws Exception {
        when(categoryRepository.existsCategoryByName("Food")).thenReturn(true);
        when(accountRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(get("/transaction-custom/99/byCategory")
                        .param("nameCategory", "Food"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("byCategory: нет транзакций по фильтру — возвращает пустой список")
    void getByCategory_NoTransactions_ReturnsEmptyList() throws Exception {
        when(categoryRepository.existsCategoryByName("Food")).thenReturn(true);
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findCategoryByName("Food")).thenReturn(foodCategory);
        when(transactionRepository.findTransactionsByAccountIdAndCategory(1L, foodCategory))
                .thenReturn(List.of());

        mockMvc.perform(get("/transaction-custom/1/byCategory")
                        .param("nameCategory", "Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ─── GET /{accountId}/byAccount ──────────────────────────────────────────

    @Test
    @DisplayName("byAccount: счёт существует — возвращает 200 и список транзакций")
    void getByAccount_ValidAccount_Returns200() throws Exception {
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findByAccountIdOrderByTimestampDesc(1L))
                .thenReturn(List.of(sampleTransaction));

        mockMvc.perform(get("/transaction-custom/1/byAccount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("byAccount: счёт не существует — возвращает 404")
    void getByAccount_AccountNotFound_Returns404() throws Exception {
        when(accountRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(get("/transaction-custom/99/byAccount"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("byAccount: нет транзакций — возвращает пустой список")
    void getByAccount_NoTransactions_ReturnsEmptyList() throws Exception {
        when(accountRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findByAccountIdOrderByTimestampDesc(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/transaction-custom/1/byAccount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}