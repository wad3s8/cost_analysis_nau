package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladislav.cost_analysis_nau.entity.User;

@RepositoryRestResource(path = "users")
public interface UsersRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
    boolean existsByLogin(String login);
}
