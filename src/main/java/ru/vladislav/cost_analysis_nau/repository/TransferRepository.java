package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladislav.cost_analysis_nau.entity.Transfer;

import java.util.List;

@RepositoryRestResource(path = "transfer")
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("select t from Transfer t where t.from.user.id = :userId or t.to.user.id = :userId order by t.createdAt desc")
    List<Transfer> findByUserId(@Param("userId") Long userId);
}
