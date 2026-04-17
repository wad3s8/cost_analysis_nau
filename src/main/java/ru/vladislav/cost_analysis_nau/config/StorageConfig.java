package ru.vladislav.cost_analysis_nau.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vladislav.cost_analysis_nau.entity.UserApp;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class StorageConfig
{
    @Bean
    public List<UserApp> usersList()
    {
        return new ArrayList<>();
    }
}