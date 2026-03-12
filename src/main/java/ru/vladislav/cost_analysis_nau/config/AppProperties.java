package ru.vladislav.cost_analysis_nau.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties
{
    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    public String getAppName()
    {
        return appName;
    }

    public String getAppVersion()
    {
        return appVersion;
    }
}