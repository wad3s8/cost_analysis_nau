package ru.vladislav.cost_analysis_nau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladislav.cost_analysis_nau.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
