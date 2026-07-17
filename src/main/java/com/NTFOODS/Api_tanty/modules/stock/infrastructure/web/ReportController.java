package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.ReportService;
import com.NTFOODS.Api_tanty.modules.stock.domain.report.entity.Report;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stock/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Report> generateReport(@RequestBody GenerateReportRequest request) {
        Report report;
        switch (request.type) {
            case "STOCK_CENTRAL_STATUS":
                report = reportService.generateStockCentralStatusReport(
                    request.periodStart, request.periodEnd, request.generatedBy, request.format);
                break;
            case "STOCK_BUFFER_STATUS":
                report = reportService.generateStockBufferStatusReport(
                    request.periodStart, request.periodEnd, request.generatedBy, request.format);
                break;
            case "STOCK_MOVEMENTS":
                report = reportService.generateStockMovementsReport(
                    request.periodStart, request.periodEnd, request.generatedBy, request.format);
                break;
            case "COMMERCIAL_PERFORMANCE":
                report = reportService.generateCommercialPerformanceReport(
                    request.periodStart, request.periodEnd, request.generatedBy, request.format);
                break;
            case "DOTATIONS_VS_SALES":
                report = reportService.generateDotationsVsSalesReport(
                    request.periodStart, request.periodEnd, request.generatedBy, request.format);
                break;
            case "INVENTORY":
                report = reportService.generateInventoryReport(
                    request.periodStart, request.periodEnd, request.generatedBy, request.format);
                break;
            default:
                throw new IllegalArgumentException("Unknown report type: " + request.type);
        }
        return ResponseEntity.ok(report);
    }

    public static class GenerateReportRequest {
        public String type;
        public LocalDateTime periodStart;
        public LocalDateTime periodEnd;
        public String generatedBy;
        public Report.ReportFormat format;
    }

    @PostMapping("/stock-central")
    public ResponseEntity<Report> generateStockCentralStatusReport(
            @RequestParam LocalDateTime periodStart,
            @RequestParam LocalDateTime periodEnd,
            @RequestParam String generatedBy,
            @RequestParam Report.ReportFormat format) {

        Report report = reportService.generateStockCentralStatusReport(
                periodStart, periodEnd, generatedBy, format);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/stock-buffer")
    public ResponseEntity<Report> generateStockBufferStatusReport(
            @RequestParam LocalDateTime periodStart,
            @RequestParam LocalDateTime periodEnd,
            @RequestParam String generatedBy,
            @RequestParam Report.ReportFormat format) {

        Report report = reportService.generateStockBufferStatusReport(
                periodStart, periodEnd, generatedBy, format);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/movements")
    public ResponseEntity<Report> generateStockMovementsReport(
            @RequestParam LocalDateTime periodStart,
            @RequestParam LocalDateTime periodEnd,
            @RequestParam String generatedBy,
            @RequestParam Report.ReportFormat format) {

        Report report = reportService.generateStockMovementsReport(
                periodStart, periodEnd, generatedBy, format);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/commercial-performance")
    public ResponseEntity<Report> generateCommercialPerformanceReport(
            @RequestParam LocalDateTime periodStart,
            @RequestParam LocalDateTime periodEnd,
            @RequestParam String generatedBy,
            @RequestParam Report.ReportFormat format) {

        Report report = reportService.generateCommercialPerformanceReport(
                periodStart, periodEnd, generatedBy, format);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/dotations-vs-sales")
    public ResponseEntity<Report> generateDotationsVsSalesReport(
            @RequestParam LocalDateTime periodStart,
            @RequestParam LocalDateTime periodEnd,
            @RequestParam String generatedBy,
            @RequestParam Report.ReportFormat format) {

        Report report = reportService.generateDotationsVsSalesReport(
                periodStart, periodEnd, generatedBy, format);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/inventory")
    public ResponseEntity<Report> generateInventoryReport(
            @RequestParam LocalDateTime periodStart,
            @RequestParam LocalDateTime periodEnd,
            @RequestParam String generatedBy,
            @RequestParam Report.ReportFormat format) {

        Report report = reportService.generateInventoryReport(
                periodStart, periodEnd, generatedBy, format);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Report>> getReportsByType(@PathVariable Report.ReportType type) {
        List<Report> reports = reportService.getReportsByType(type);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/user/{generatedBy}")
    public ResponseEntity<List<Report>> getReportsByUser(@PathVariable String generatedBy) {
        List<Report> reports = reportService.getReportsByUser(generatedBy);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/period")
    public ResponseEntity<List<Report>> getReportsByPeriod(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        
        List<Report> reports = reportService.getReportsByPeriod(startDate, endDate);
        return ResponseEntity.ok(reports);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
