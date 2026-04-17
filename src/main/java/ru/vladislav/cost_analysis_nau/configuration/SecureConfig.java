package ru.vladislav.cost_analysis_nau.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.vladislav.cost_analysis_nau.entity.Role;

@Configuration
@EnableWebSecurity
public class SecureConfig {
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/registration").permitAll()
                .anyRequest().authenticated())
                .formLogin((form) -> form
                        .loginPage("/login") // Указываем свой URL для страницы входа
                        .permitAll()
                );
        return httpSecurity.build();
    }
}
