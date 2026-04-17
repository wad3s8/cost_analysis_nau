package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.vladislav.cost_analysis_nau.entity.Category;

@Repository
@RepositoryRestResource(path = "category")
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findCategoryByName(@Param("name") String name);
    boolean existsCategoryByName(@Param("name") String name);
}
