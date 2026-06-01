package ru.vladislav.cost_analysis_nau.configuration;

import jakarta.servlet.DispatcherType;
import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;
import java.util.Map;

/** Регистрирует JavaMelody вручную, минуя Spring Boot autoconfigure (несовместим с Boot 4). */
@Configuration
public class JavaMelodyConfig {

    @Bean
    public FilterRegistrationBean<MonitoringFilter> monitoringFilter() {
        FilterRegistrationBean<MonitoringFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new MonitoringFilter());
        bean.addUrlPatterns("/*");
        bean.setName("javamelody");
        bean.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));
        bean.setInitParameters(Map.of("log", "true"));
        bean.setOrder(-99); // после Spring Security (-100), до DispatcherServlet
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean<SessionListener> sessionListener() {
        return new ServletListenerRegistrationBean<>(new SessionListener());
    }
}
