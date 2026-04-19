package ru.vladislav.cost_analysis_nau.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladislav.cost_analysis_nau.service.ReportService;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Создаёт отчёт и сразу запускает его асинхронное формирование.
     * Возвращает id отчёта, НЕ дожидаясь окончания формирования.
     * POST /reports
     */
    @PostMapping
    public ResponseEntity<Long> createReport() {
        Long reportId = reportService.createReport();
        reportService.generateReport(reportId); // запускаем асинхронно, не ждём
        return ResponseEntity.ok(reportId);
    }

    /**
     * Возвращает содержимое отчёта по его id.
     * Если отчёт ещё не готов или завершился с ошибкой — возвращает соответствующее сообщение.
     * GET /reports/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<String> getReport(@PathVariable Long id) {
        String content = reportService.getReport(id);
        return ResponseEntity.ok(content);
    }
}