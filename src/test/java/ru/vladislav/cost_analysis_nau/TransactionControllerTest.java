package ru.vladislav.cost_analysis_nau;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.vladislav.cost_analysis_nau.controller.TransactionController;
import ru.vladislav.cost_analysis_nau.entity.Role;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;
import ru.vladislav.cost_analysis_nau.service.AccountService;
import ru.vladislav.cost_analysis_nau.service.CategoryService;
import ru.vladislav.cost_analysis_nau.service.TransactionService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionControllerTest {

    @Mock private TransactionService transactionService;
    @Mock private AccountService accountService;
    @Mock private CategoryService categoryService;
    @Mock private UsersRepository usersRepository;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setRole(Role.USER);

        when(usersRepository.findByLogin("testuser")).thenReturn(testUser);
        when(transactionService.getByUser(testUser)).thenReturn(List.of());
        when(categoryService.getAll()).thenReturn(List.of());
        when(accountService.getAccountsByUser(testUser)).thenReturn(List.of());
    }

    private Authentication mockAuth() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        return auth;
    }

    @Test
    @DisplayName("GET /transactions — возвращает страницу со списком транзакций")
    void getTransactions_Returns200AndListView() throws Exception {
        mockMvc.perform(get("/transactions")
                        .principal(mockAuth()))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/list"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @DisplayName("GET /transactions/new — возвращает форму добавления транзакции")
    void getNewTransactionForm_Returns200AndFormView() throws Exception {
        mockMvc.perform(get("/transactions/new")
                        .principal(mockAuth()))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/form"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("accounts"))
                .andExpect(model().attributeExists("categories"));
    }
}
