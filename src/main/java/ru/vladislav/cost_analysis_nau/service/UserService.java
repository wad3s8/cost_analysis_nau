package ru.vladislav.cost_analysis_nau.service;

import ru.vladislav.cost_analysis_nau.entity.UserApp;

import java.util.Optional;

public interface UserService
{
    void createUser(Long id, String login, String password);
    Optional<UserApp> findById(Long id);
    void deleteById(Long id);
    void update(Long id, String newLogin, String password);
}