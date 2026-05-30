package ru.vladislav.cost_analysis_nau;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:postgresql://localhost:5432/analysis-nau",
                "spring.datasource.username=analysis-nau",
                "spring.datasource.password=12345",
                "spring.datasource.driver-class-name=org.postgresql.Driver",
                "spring.jpa.hibernate.ddl-auto=update",
                "app.name=cost_analysis_nau",
                "app.version=1.0"
        }
)
class CostAnalysisNauApplicationTests {
    @Test
    void contextLoads() {
    }
}

