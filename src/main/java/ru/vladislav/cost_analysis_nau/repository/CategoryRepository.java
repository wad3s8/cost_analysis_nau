package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladislav.cost_analysis_nau.entity.Category;

import java.util.List;

@RepositoryRestResource(path = "category")
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
    boolean existsByName(String name);
    List<Category> findByIsIncome(boolean isIncome);
}
