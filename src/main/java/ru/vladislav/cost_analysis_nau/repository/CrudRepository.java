package ru.vladislav.cost_analysis_nau.repository;

import java.util.Optional;

public interface CrudRepository<T, ID>
{
    void create(T entity);

    Optional<T> read(ID id);

    void update(T entity);

    void delete(ID id);
}