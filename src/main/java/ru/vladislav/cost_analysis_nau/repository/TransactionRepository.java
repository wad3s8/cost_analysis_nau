package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "transaction")
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdAndCategory(Long accountId, Category category);

    @Query("select t from Transaction t where t.account.user.id = :userId order by t.createdAt desc")
    List<Transaction> findByUserId(@Param("userId") Long userId);

    @Query("""
        select t from Transaction t
        where t.account.user.id = :userId
          and (:categoryId is null or t.category.id = :categoryId)
          and (:isIncome is null or t.isIncome = :isIncome)
          and (:from is null or t.createdAt >= :from)
          and (:to is null or t.createdAt <= :to)
        order by t.createdAt desc
    """)
    List<Transaction> findFiltered(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("isIncome") Boolean isIncome,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("select coalesce(sum(t.amount), 0) from Transaction t where t.account.user.id = :userId and t.isIncome = :isIncome")
    Double sumByUserAndType(@Param("userId") Long userId, @Param("isIncome") boolean isIncome);

    @Query("""
        select coalesce(sum(t.amount), 0) from Transaction t
        where t.account.user.id = :userId
          and t.isIncome = :isIncome
          and t.createdAt >= :from
          and t.createdAt <= :to
    """)
    Double sumByUserAndTypeAndPeriod(
            @Param("userId") Long userId,
            @Param("isIncome") boolean isIncome,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        select t.category.name, coalesce(sum(t.amount), 0)
        from Transaction t
        where t.account.user.id = :userId
          and t.isIncome = :isIncome
          and t.createdAt >= :from
          and t.createdAt <= :to
        group by t.category.name
        order by sum(t.amount) desc
    """)
    List<Object[]> sumGroupedByCategoryAndPeriod(
            @Param("userId") Long userId,
            @Param("isIncome") boolean isIncome,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
