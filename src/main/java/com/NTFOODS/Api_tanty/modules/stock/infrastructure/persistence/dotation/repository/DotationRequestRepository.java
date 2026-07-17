package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.dotation.repository;

import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.dotation.jpa.DotationRequestJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DotationRequestRepository - Repository pour DotationRequestJpaEntity
 */
@Repository
public interface DotationRequestRepository extends JpaRepository<DotationRequestJpaEntity, Long> {
    
    Optional<DotationRequestJpaEntity> findByReferenceNumber(String referenceNumber);
    
    List<DotationRequestJpaEntity> findByCommercialMatricule(String commercialMatricule);
    
    List<DotationRequestJpaEntity> findByStatus(String status);
    
    @Query("SELECT dr FROM DotationRequestJpaEntity dr WHERE dr.status = 'PENDING' ORDER BY dr.requestedAt")
    List<DotationRequestJpaEntity> findPendingRequests();
    
    @Query("SELECT dr FROM DotationRequestJpaEntity dr WHERE dr.status = 'REVIEWED' ORDER BY dr.reviewedAt")
    List<DotationRequestJpaEntity> findReviewedRequests();
    
    @Query("SELECT dr FROM DotationRequestJpaEntity dr WHERE dr.status = 'PAYMENT_VERIFIED' ORDER BY dr.paymentVerifiedAt")
    List<DotationRequestJpaEntity> findPaymentVerifiedRequests();
    
    @Query("SELECT dr FROM DotationRequestJpaEntity dr WHERE dr.status = 'QUANTITY_VALIDATED' ORDER BY dr.quantityValidatedAt")
    List<DotationRequestJpaEntity> findQuantityValidatedRequests();
    
    @Query("SELECT dr FROM DotationRequestJpaEntity dr WHERE dr.status = 'APPROVED' AND dr.scheduledDate <= :date ORDER BY dr.scheduledDate")
    List<DotationRequestJpaEntity> findApprovedRequestsScheduledBefore(@Param("date") LocalDateTime date);
    
    @Query("SELECT dr FROM DotationRequestJpaEntity dr WHERE dr.requestedAt BETWEEN :startDate AND :endDate")
    List<DotationRequestJpaEntity> findByRequestedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT dr FROM DotationRequestJpaEntity dr WHERE dr.scheduledDate BETWEEN :startDate AND :endDate")
    List<DotationRequestJpaEntity> findByScheduledDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                             @Param("endDate") LocalDateTime endDate);
}
