package ru.vladislav.cost_analysis_nau.criteria;


import ru.vladislav.cost_analysis_nau.entity.Account;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;

import java.util.List;

public interface TransactionRepositoryCustom {

    List<Transaction> findTransactionsByAccountAndCategory(Account account, Category category);

    List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId);
}
