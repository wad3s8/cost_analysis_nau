package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.vladislav.cost_analysis_nau.entity.Account;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;
import java.util.List;

@Repository
@RepositoryRestResource(path = "transaction")
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findTransactionsByAccountIdAndCategory(Long accountId, Category category);

    @Query("""
    select t
    from Transaction t
    where t.account.id = :accountId
    order by t.createdAt desc
""")
    List<Transaction> findByAccountIdOrderByTimestampDesc(@Param("accountId") Long accountId);
}
