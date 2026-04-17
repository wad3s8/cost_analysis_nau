package ru.vladislav.cost_analysis_nau.service;

import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.vladislav.cost_analysis_nau.repository.UsersRepository;
import ru.vladislav.cost_analysis_nau.entity.UserApp;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsersRepository usersRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserApp userApp   = usersRepository.findByLogin(username);
        if (userApp == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(userApp.getLogin(), userApp.getPassword(), Set.of(new SimpleGrantedAuthority("ROLE_"+ userApp.getRole())));
    }
}
