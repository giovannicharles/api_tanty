package com.NTFOODS.Api_tanty.modules.stock.domain.report.entity;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Report - Entité représentant un rapport généré
 */
public class Report {
    
    private Long id;
    private final ReportType type;
    private final String title;
    private final String description;
    private final LocalDateTime generatedAt;
    private final LocalDateTime periodStart;
    private final LocalDateTime periodEnd;
    private final String generatedBy;
    private final ReportFormat format;
    private final Map<String, Object> data;
    private final String filePath;
    private final long fileSize;
    private String status; // GENERATING, COMPLETED, FAILED
    
    public Report(ReportType type, String title, String description, 
                 LocalDateTime periodStart, LocalDateTime periodEnd, 
                 String generatedBy, ReportFormat format, 
                 Map<String, Object> data, String filePath, long fileSize) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.generatedBy = generatedBy;
        this.format = format;
        this.data = data;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.generatedAt = LocalDateTime.now();
        this.status = "COMPLETED";
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ReportType getType() {
        return type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public LocalDateTime getPeriodStart() {
        return periodStart;
    }
    
    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }
    
    public String getGeneratedBy() {
        return generatedBy;
    }
    
    public ReportFormat getFormat() {
        return format;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * ReportType - Types de rapports disponibles
     */
    public enum ReportType {
        // Rapports de stock
        STOCK_CENTRAL_STATUS,
        STOCK_BUFFER_STATUS,
        STOCK_MOBILE_STATUS,
        STOCK_MOVEMENTS,
        INVENTORY_REPORT,
        STOCK_ROTATION,
        STOCK_VALUATION,
        
        // Rapports commerciaux
        COMMERCIAL_PERFORMANCE,
        DOTATIONS_VS_SALES,
        RETURNS_ANALYSIS,
        SALES_TRENDS,
        DEMAND_FORECAST,
        
        // Rapports de production
        PRODUCTION_VS_CONSUMPTION,
        PRODUCTION_YIELD,
        PRODUCTION_QUALITY,
        PRODUCTION_DEADLINES,
        
        //Rapports financiers
        STOCK_VALUATION_FINANCIAL,
        STOCK_COSTS,
        STOCK_MARGINS,
        STORAGE_COSTS
    }
    
    /**
     * ReportFormat - Formats de rapport disponibles
     */
    public enum ReportFormat {
        PDF,
        EXCEL,
        CSV,
        JSON
    }
}
