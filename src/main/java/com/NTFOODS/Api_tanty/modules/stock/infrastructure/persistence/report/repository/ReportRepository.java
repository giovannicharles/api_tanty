package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.report.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.report.entity.Report;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.report.jpa.ReportJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ReportRepository - Repository pour ReportJpaEntity
 */
@Repository
public interface ReportRepository extends JpaRepository<ReportJpaEntity, Long> {
    
    List<ReportJpaEntity> findByType(Report.ReportType type);
    
    List<ReportJpaEntity> findByGeneratedBy(String generatedBy);
    
    List<ReportJpaEntity> findByFormat(Report.ReportFormat format);
    
    List<ReportJpaEntity> findByStatus(String status);
    
    @Query("SELECT r FROM ReportJpaEntity r WHERE r.generatedAt BETWEEN :startDate AND :endDate ORDER BY r.generatedAt DESC")
    List<ReportJpaEntity> findByGeneratedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM ReportJpaEntity r WHERE r.type = :type AND r.generatedAt BETWEEN :startDate AND :endDate")
    List<ReportJpaEntity> findByTypeAndPeriod(@Param("type") Report.ReportType type,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(r) FROM ReportJpaEntity r WHERE r.generatedAt BETWEEN :startDate AND :endDate")
    long countReportsInPeriod(@Param("startDate") LocalDateTime startDate, 
                             @Param("endDate") LocalDateTime endDate);
}
