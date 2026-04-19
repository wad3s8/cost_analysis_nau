package ru.vladislav.cost_analysis_nau.criteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import ru.vladislav.cost_analysis_nau.entity.Account;
import ru.vladislav.cost_analysis_nau.entity.Category;
import ru.vladislav.cost_analysis_nau.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Transaction> findTransactionsByAccountAndCategory(Account account, Category category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = cq.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();

        if (account != null) {
            predicates.add(cb.equal(transaction.get("account"), account));
        }

        if (category != null) {
            predicates.add(cb.equal(transaction.get("category"), category));
        }

        cq.select(transaction)
                .where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = cq.from(Transaction.class);

        cq.select(transaction)
                .where(cb.equal(transaction.get("account").get("id"), accountId))
                .orderBy(cb.desc(transaction.get("createdAt")));

        return entityManager.createQuery(cq).getResultList();
    }
}