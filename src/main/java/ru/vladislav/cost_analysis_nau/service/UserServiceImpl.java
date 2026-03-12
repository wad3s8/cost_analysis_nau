package ru.vladislav.cost_analysis_nau.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vladislav.cost_analysis_nau.config.AppProperties;
import ru.vladislav.cost_analysis_nau.config.Config;
import ru.vladislav.cost_analysis_nau.entity.User;
import ru.vladislav.cost_analysis_nau.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final AppProperties appProperties;
    private final UserRepository userRepository;

    @Override
    public void createUser(Long id, String login, String password) {
        userRepository.create(new User(id, login, password));
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.read(id);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.delete(id);
    }

    @Override
    public void update(Long id, String newLogin, String password) {
        userRepository.update(new User(id, newLogin, password));
    }

    @PostConstruct
    public void init(){
        System.out.println(appProperties.getAppName());
        System.out.println(appProperties.getAppVersion());
    }
}
