package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vladislav.cost_analysis_nau.entity.Account;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

}
