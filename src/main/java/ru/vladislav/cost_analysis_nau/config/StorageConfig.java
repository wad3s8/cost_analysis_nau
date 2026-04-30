package ru.vladislav.cost_analysis_nau.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vladislav.cost_analysis_nau.entity.User;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class StorageConfig
{
    @Bean
    public List<User> usersList()
    {
        return new ArrayList<>();
    }
}