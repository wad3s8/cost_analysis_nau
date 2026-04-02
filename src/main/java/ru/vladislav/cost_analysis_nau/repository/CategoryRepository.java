package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vladislav.cost_analysis_nau.entity.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
}
