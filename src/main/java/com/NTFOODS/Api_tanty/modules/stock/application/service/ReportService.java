package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.domain.report.entity.Report;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.report.jpa.ReportJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.report.repository.ReportRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ReportService - Service pour generer des rapports
 * Gere la generation de rapports de stock, commerciaux, de production et financiers
 */
@Service
@Transactional
public class ReportService {

  private static final Logger log = LoggerFactory.getLogger(ReportService.class);

  private final ReportRepository reportRepository;
  private final StockLocationService stockLocationService;
  private final StockItemService stockItemService;
  private final StockMovementService stockMovementService;
  private final MobileStockTrackingService mobileStockTrackingService;
  private final ObjectMapper objectMapper;

  public ReportService(ReportRepository reportRepository,
                       StockLocationService stockLocationService,
                       StockItemService stockItemService,
                       StockMovementService stockMovementService,
                       MobileStockTrackingService mobileStockTrackingService,
                       ObjectMapper objectMapper) {
    this.reportRepository = reportRepository;
    this.stockLocationService = stockLocationService;
    this.stockItemService = stockItemService;
    this.stockMovementService = stockMovementService;
    this.mobileStockTrackingService = mobileStockTrackingService;
    this.objectMapper = objectMapper;
  }

  public Report generateStockCentralStatusReport(LocalDateTime periodStart, LocalDateTime periodEnd,
                                                 String generatedBy, Report.ReportFormat format) {
    StockLocationId centralLocation = stockLocationService.getLocationsByType(StockLocationType.STOCK_CENTRAL)
      .stream()
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Central stock location not found"))
      .getId();

    List<com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem> stockItems =
      stockItemService.getStockItemsByLocation(centralLocation);

    Map<String, Object> data = new HashMap<>();
    data.put("location", "Stock Central");
    data.put("totalProducts", stockItems.size());
    data.put("totalQuantity", stockItems.stream()
      .mapToDouble(item -> item.getQuantity().doubleValue())
      .sum());
    data.put("items", stockItems);

    return saveReport(Report.ReportType.STOCK_CENTRAL_STATUS,
      "Rapport Stock Central",
      "Statut du stock central sur la periode",
      periodStart, periodEnd, generatedBy, format, data);
  }

  public Report generateStockBufferStatusReport(LocalDateTime periodStart, LocalDateTime periodEnd,
                                                String generatedBy, Report.ReportFormat format) {
    StockLocationId bufferLocation = stockLocationService.getLocationsByType(StockLocationType.STOCK_BUFFER)
      .stream()
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Buffer stock location not found"))
      .getId();

    List<com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem> stockItems =
      stockItemService.getStockItemsByLocation(bufferLocation);

    Map<String, Object> data = new HashMap<>();
    data.put("location", "Tampon (Buffer)");
    data.put("totalProducts", stockItems.size());
    data.put("totalQuantity", stockItems.stream()
      .mapToDouble(item -> item.getQuantity().doubleValue())
      .sum());
    data.put("items", stockItems);

    return saveReport(Report.ReportType.STOCK_BUFFER_STATUS,
      "Rapport Tampon",
      "Statut du tampon sur la periode",
      periodStart, periodEnd, generatedBy, format, data);
  }

  public Report generateStockMovementsReport(LocalDateTime periodStart, LocalDateTime periodEnd,
                                             String generatedBy, Report.ReportFormat format) {
    List<com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement> movements =
      stockMovementService.getMovementsByPeriod(periodStart, periodEnd);

    Map<String, Object> data = new HashMap<>();
    data.put("periodStart", periodStart);
    data.put("periodEnd", periodEnd);
    data.put("totalMovements", movements.size());
    data.put("movementsByType", movements.stream()
      .collect(Collectors.groupingBy(m -> m.getType().toString(), Collectors.counting())));
    data.put("movements", movements);

    return saveReport(Report.ReportType.STOCK_MOVEMENTS,
      "Rapport Mouvements de Stock",
      "Mouvements de stock sur la periode",
      periodStart, periodEnd, generatedBy, format, data);
  }

  public Report generateCommercialPerformanceReport(LocalDateTime periodStart, LocalDateTime periodEnd,
                                                    String generatedBy, Report.ReportFormat format) {
    List<MobileStockTrackingService.MobileStockSummary> allMobileStock =
      mobileStockTrackingService.getAllCommercialsMobileStock();

    Map<String, Object> data = new HashMap<>();
    data.put("periodStart", periodStart);
    data.put("periodEnd", periodEnd);
    data.put("totalCommercials", allMobileStock.size());
    data.put("commercials", allMobileStock);

    return saveReport(Report.ReportType.COMMERCIAL_PERFORMANCE,
      "Rapport Performance Commerciaux",
      "Performance des commerciaux sur la periode",
      periodStart, periodEnd, generatedBy, format, data);
  }

  public Report generateDotationsVsSalesReport(LocalDateTime periodStart, LocalDateTime periodEnd,
                                               String generatedBy, Report.ReportFormat format) {
    List<Report> dotations = getReportsByTypeAndPeriod(
      Report.ReportType.DOTATIONS_VS_SALES, periodStart, periodEnd);

    Map<String, Object> data = new HashMap<>();
    data.put("periodStart", periodStart);
    data.put("periodEnd", periodEnd);
    data.put("totalDotations", dotations.size());

    return saveReport(Report.ReportType.DOTATIONS_VS_SALES,
      "Rapport Dotations vs Ventes",
      "Comparaison des dotations et ventes sur la periode",
      periodStart, periodEnd, generatedBy, format, data);
  }

  public Report generateInventoryReport(LocalDateTime periodStart, LocalDateTime periodEnd,
                                        String generatedBy, Report.ReportFormat format) {
    var centralStock = stockItemService.getStockItemsByLocation(
      stockLocationService.getLocationsByType(StockLocationType.STOCK_CENTRAL).get(0).getId());
    var bufferStock = stockItemService.getStockItemsByLocation(
      stockLocationService.getLocationsByType(StockLocationType.STOCK_BUFFER).get(0).getId());

    Map<String, Object> data = new HashMap<>();
    data.put("centralStock", centralStock);
    data.put("bufferStock", bufferStock);
    data.put("totalCentralItems", centralStock.size());
    data.put("totalBufferItems", bufferStock.size());

    return saveReport(Report.ReportType.INVENTORY_REPORT,
      "Rapport d'Inventaire",
      "Inventaire detaille sur la periode",
      periodStart, periodEnd, generatedBy, format, data);
  }

  public Report getReportById(Long reportId) {
    ReportJpaEntity entity = reportRepository.findById(reportId)
      .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));
    return mapToDomain(entity);
  }

  public List<Report> getReportsByType(Report.ReportType type) {
    return reportRepository.findByType(type).stream()
      .map(this::mapToDomain)
      .collect(Collectors.toList());
  }

  public List<Report> getReportsByUser(String generatedBy) {
    return reportRepository.findByGeneratedBy(generatedBy).stream()
      .map(this::mapToDomain)
      .collect(Collectors.toList());
  }

  public List<Report> getReportsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
    return reportRepository.findByGeneratedAtBetween(startDate, endDate).stream()
      .map(this::mapToDomain)
      .collect(Collectors.toList());
  }

  public void deleteReport(Long reportId) {
    reportRepository.deleteById(reportId);
    log.info("Deleted report: {}", reportId);
  }

  private Report saveReport(Report.ReportType type, String title, String description,
                            LocalDateTime periodStart, LocalDateTime periodEnd,
                            String generatedBy, Report.ReportFormat format,
                            Map<String, Object> data) {
    try {
      String jsonData = objectMapper.writeValueAsString(data);

      ReportJpaEntity entity = new ReportJpaEntity(
        type, title, description, periodStart, periodEnd,
        generatedBy, format, jsonData, null, null);

      ReportJpaEntity saved = reportRepository.save(entity);
      log.info("Generated report: {} - {}", type, title);

      return mapToDomain(saved);
    } catch (Exception e) {
      log.error("Error generating report: {}", title, e);
      throw new RuntimeException("Failed to generate report", e);
    }
  }

  private List<Report> getReportsByTypeAndPeriod(Report.ReportType type,
                                                 LocalDateTime startDate, LocalDateTime endDate) {
    return reportRepository.findByTypeAndPeriod(type, startDate, endDate).stream()
      .map(this::mapToDomain)
      .collect(Collectors.toList());
  }

  private Report mapToDomain(ReportJpaEntity entity) {
    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> data = objectMapper.readValue(entity.getData(), Map.class);

      return new Report(
        entity.getType(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getPeriodStart(),
        entity.getPeriodEnd(),
        entity.getGeneratedBy(),
        entity.getFormat(),
        data,
        entity.getFilePath(),
        entity.getFileSize() != null ? entity.getFileSize() : 0L
      );
    } catch (Exception e) {
      log.error("Error mapping report to domain: {}", entity.getId(), e);
      throw new RuntimeException("Failed to map report", e);
    }
  }
}
