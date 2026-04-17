package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.vladislav.cost_analysis_nau.entity.Account;

@Repository
@RepositoryRestResource(path = "account")
public interface AccountRepository extends CrudRepository<Account, Long> {
    boolean existsAccountById(@Param("accountId") Long accountId);
}
