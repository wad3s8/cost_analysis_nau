package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladislav.cost_analysis_nau.entity.Account;

import java.util.List;

@RepositoryRestResource(path = "account")
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);
}
