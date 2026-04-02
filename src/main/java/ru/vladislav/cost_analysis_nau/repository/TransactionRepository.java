package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.vladislav.cost_analysis_nau.entity.Account;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findTransactionsByAccountAndCategory(Account account, Category category);

    @Query("""
    select t
    from Transaction t
    where t.account.id = :accountId
    order by t.timestamp desc
""")
    List<Transaction> findByAccountIdOrderByTimestampDesc(@Param("accountId") Long accountId);
}
