package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.vladislav.cost_analysis_nau.entity.Transfer;

@RepositoryRestResource(path = "transfer")
public interface TransferRepository extends CrudRepository<Transfer, Long> {
}
