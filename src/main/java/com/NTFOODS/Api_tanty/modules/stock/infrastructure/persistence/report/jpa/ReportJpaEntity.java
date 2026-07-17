package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.report.jpa;

import com.NTFOODS.Api_tanty.modules.stock.domain.report.entity.Report;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

/**
 * ReportJpaEntity - Entité JPA pour Report
 */
@Entity
@Table(name = "reports")
public class ReportJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Report.ReportType type;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    
    @Column(name = "period_start")
    private LocalDateTime periodStart;
    
    @Column(name = "period_end")
    private LocalDateTime periodEnd;
    
    @Column(name = "generated_by", nullable = false)
    private String generatedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private Report.ReportFormat format;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "json")
    private String data;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    public ReportJpaEntity() {}
    
    public ReportJpaEntity(Report.ReportType type, String title, String description,
                         LocalDateTime periodStart, LocalDateTime periodEnd,
                         String generatedBy, Report.ReportFormat format,
                         String data, String filePath, Long fileSize) {
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
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Report.ReportType getType() { return type; }
    public void setType(Report.ReportType type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }
    
    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }
    
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    
    public Report.ReportFormat getFormat() { return format; }
    public void setFormat(Report.ReportFormat format) { this.format = format; }
    
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
