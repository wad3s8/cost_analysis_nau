package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vladislav.cost_analysis_nau.entity.Transfer;

@Repository
public interface TransferRepository extends CrudRepository<Transfer, Long> {
}
