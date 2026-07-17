package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.domain.dotation.entity.DotationRequest;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.dotation.jpa.DotationItemJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.dotation.jpa.DotationRequestJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.dotation.repository.DotationRequestRepository;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DotationService - Service pour gérer le workflow de dotation
 * Commercial soumet → Secrétaire vérifie paiement → Comptable valide quantités → Gestionnaire stock approuve → Livraison
 */
@Service
@Transactional
public class DotationService {

  private static final Logger log = LoggerFactory.getLogger(DotationService.class);

  private final DotationRequestRepository dotationRequestRepository;
  private final StockLocationService stockLocationService;
  private final StockItemService stockItemService;
  private final StockMovementService stockMovementService;

  public DotationService(DotationRequestRepository dotationRequestRepository,
                         StockLocationService stockLocationService,
                         StockItemService stockItemService,
                         StockMovementService stockMovementService) {
    this.dotationRequestRepository = dotationRequestRepository;
    this.stockLocationService = stockLocationService;
    this.stockItemService = stockItemService;
    this.stockMovementService = stockMovementService;
  }

  /**
   * Crée une demande de dotation (par le commercial)
   * Si la demande est faite en soirée (après 18h), elle compte pour le lendemain
   */
  public DotationRequest createDotationRequest(UserId commercialId, String commercialMatricule,
                                               String commercialName, List<DotationRequest.DotationItem> items,
                                               String justification) {
    List<DotationRequestJpaEntity> existing = dotationRequestRepository.findByCommercialMatricule(commercialMatricule);
    boolean hasPending = existing.stream().anyMatch(e -> "PENDING".equals(e.getStatus()));
    if (hasPending) {
      throw new IllegalStateException("Ce commercial a une dotation PENDING. Le paiement du précédent doit être vérifié par la secrétaire avant nouvelle précommande.");
    }

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime scheduledDate = now;

    // Si demande en soirée (après 18h), compte pour le lendemain
    if (now.toLocalTime().isAfter(LocalTime.of(18, 0))) {
      scheduledDate = now.plusDays(1).toLocalDate().atStartOfDay();
      log.info("Demande soirée, programmée pour le lendemain: {}", scheduledDate);
    }

    DotationRequestJpaEntity entity = new DotationRequestJpaEntity(
      commercialId.getMatricule(),
      commercialMatricule,
      commercialName,
      justification,
      scheduledDate,
      generateReferenceNumber()
    );

    if (items != null) {
      for (DotationRequest.DotationItem item : items) {
        DotationItemJpaEntity itemEntity = new DotationItemJpaEntity(
          item.getProductId(),
          item.getProductSku(),
          item.getProductName(),
          item.getPackagingType(),
          item.getRequestedQuantity(),
          item.getQuantityPerCarton()
        );
        itemEntity.setNotes(item.getNotes());
        entity.addItem(itemEntity);
      }
    }

    DotationRequestJpaEntity saved = dotationRequestRepository.save(entity);
    log.info("Demande de dotation créée: {} pour commercial {}", saved.getReferenceNumber(), commercialMatricule);

    return mapToDomain(saved);
  }

  /**
   * La secrétaire vérifie que le commercial a déposé l'argent du précédent
   */
  public DotationRequest verifyPayment(Long requestId, UserId verifiedBy) {
    DotationRequestJpaEntity entity = dotationRequestRepository.findById(requestId)
      .orElseThrow(() -> new IllegalArgumentException("Demande de dotation non trouvée: " + requestId));

    if (!"PENDING".equals(entity.getStatus())) {
      throw new IllegalStateException("Seules les demandes PENDING peuvent être vérifiées pour le paiement");
    }

    entity.setPaymentVerifiedBy(verifiedBy.getMatricule());
    entity.setPaymentVerifiedAt(LocalDateTime.now());
    entity.setStatus("PAYMENT_VERIFIED");

    DotationRequestJpaEntity saved = dotationRequestRepository.save(entity);
    log.info("Paiement vérifié pour dotation {} par {}", requestId, verifiedBy.getMatricule());

    return mapToDomain(saved);
  }

  /**
   * Le comptable arbitre et valide les quantités (peut modifier les items)
   */
  public DotationRequest validateQuantities(Long requestId, UserId validatedBy,
                                             String comments, List<DotationRequest.DotationItem> modifiedItems) {
    DotationRequestJpaEntity entity = dotationRequestRepository.findById(requestId)
      .orElseThrow(() -> new IllegalArgumentException("Demande de dotation non trouvée: " + requestId));

    if (!"PAYMENT_VERIFIED".equals(entity.getStatus())) {
      throw new IllegalStateException("Seules les demandes PAYMENT_VERIFIED peuvent être validées par le comptable");
    }

    entity.setQuantityValidatedBy(validatedBy.getMatricule());
    entity.setQuantityValidatedAt(LocalDateTime.now());
    entity.setQuantityValidationComments(comments);
    entity.setStatus("QUANTITY_VALIDATED");

    if (modifiedItems != null && !modifiedItems.isEmpty()) {
      entity.getItems().clear();
      for (DotationRequest.DotationItem item : modifiedItems) {
        DotationItemJpaEntity itemEntity = new DotationItemJpaEntity(
          item.getProductId(),
          item.getProductSku(),
          item.getProductName(),
          item.getPackagingType(),
          item.getRequestedQuantity(),
          item.getQuantityPerCarton()
        );
        itemEntity.setApprovedQuantity(item.getApprovedQuantity());
        itemEntity.setNotes(item.getNotes());
        entity.addItem(itemEntity);
      }
    }

    DotationRequestJpaEntity saved = dotationRequestRepository.save(entity);
    log.info("Quantités validées pour dotation {} par {}", requestId, validatedBy.getMatricule());

    return mapToDomain(saved);
  }

  /**
   * Le gestionnaire de stock révise la demande (peut modifier les quantités)
   */
  public DotationRequest reviewDotationRequest(Long requestId, UserId reviewerId,
                                               String reviewComments, List<DotationRequest.DotationItem> modifiedItems) {
    DotationRequestJpaEntity entity = dotationRequestRepository.findById(requestId)
      .orElseThrow(() -> new IllegalArgumentException("Demande de dotation non trouvée: " + requestId));

    if (!"PENDING".equals(entity.getStatus()) && !"PAYMENT_VERIFIED".equals(entity.getStatus()) && !"QUANTITY_VALIDATED".equals(entity.getStatus())) {
      throw new IllegalStateException("Seules les demandes PENDING, PAYMENT_VERIFIED ou QUANTITY_VALIDATED peuvent être révisées");
    }

    entity.setReviewedBy(reviewerId.getMatricule());
    entity.setReviewedAt(LocalDateTime.now());
    entity.setReviewComments(reviewComments);
    entity.setStatus("REVIEWED");

    if (modifiedItems != null && !modifiedItems.isEmpty()) {
      entity.getItems().clear();
      for (DotationRequest.DotationItem item : modifiedItems) {
        DotationItemJpaEntity itemEntity = new DotationItemJpaEntity(
          item.getProductId(),
          item.getProductSku(),
          item.getProductName(),
          item.getPackagingType(),
          item.getRequestedQuantity(),
          item.getQuantityPerCarton()
        );
        itemEntity.setApprovedQuantity(item.getApprovedQuantity());
        itemEntity.setNotes(item.getNotes());
        entity.addItem(itemEntity);
      }
    }

    DotationRequestJpaEntity saved = dotationRequestRepository.save(entity);
    log.info("Demande de dotation révisée: {} par {}", requestId, reviewerId.getMatricule());

    return mapToDomain(saved);
  }

  /**
   * Le gestionnaire de stock approuve la demande
   */
  public DotationRequest approveDotationRequest(Long requestId, UserId approverId) {
    DotationRequestJpaEntity entity = dotationRequestRepository.findById(requestId)
      .orElseThrow(() -> new IllegalArgumentException("Demande de dotation non trouvée: " + requestId));

    if (!"QUANTITY_VALIDATED".equals(entity.getStatus())) {
      throw new IllegalStateException("Seules les demandes QUANTITY_VALIDATED peuvent être approuvées");
    }

    // Vérifier la disponibilité dans le tampon
    StockLocationId bufferLocation = stockLocationService.getLocationsByType(StockLocationType.STOCK_BUFFER)
      .stream()
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Localisation tampon non trouvée"))
      .getId();

    for (DotationItemJpaEntity item : entity.getItems()) {
      BigDecimal approvedQuantity = item.getApprovedQuantity() != null ? item.getApprovedQuantity() : item.getRequestedQuantity();
      Optional<com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem> stockItem =
        stockItemService.getStockItem(bufferLocation, item.getProductSku());

      if (stockItem.isEmpty() || stockItem.get().getQuantity().compareTo(approvedQuantity) < 0) {
        BigDecimal available = stockItem.map(si -> si.getQuantity()).orElse(BigDecimal.ZERO);
        throw new IllegalStateException(String.format(
          "Stock tampon insuffisant pour %s (demandé: %s, disponible: %s)",
          item.getProductSku(), approvedQuantity.stripTrailingZeros().toPlainString(), available.stripTrailingZeros().toPlainString()));
      }
    }

    entity.setApprovedBy(approverId.getMatricule());
    entity.setApprovedAt(LocalDateTime.now());
    entity.setStatus("APPROVED");

    DotationRequestJpaEntity saved = dotationRequestRepository.save(entity);
    log.info("Demande de dotation approuvée: {} par {}", requestId, approverId.getMatricule());

    return mapToDomain(saved);
  }

  /**
   * Le gestionnaire de stock révise les quantités et approuve en une seule action
   */
  public DotationRequest reviewAndApproveDotationRequest(Long requestId, UserId managerId,
                                                          String reviewComments,
                                                          List<DotationRequest.DotationItem> modifiedItems) {
    DotationRequestJpaEntity entity = dotationRequestRepository.findById(requestId)
      .orElseThrow(() -> new IllegalArgumentException("Demande de dotation non trouvée: " + requestId));

    if (!"QUANTITY_VALIDATED".equals(entity.getStatus()) && !"REVIEWED".equals(entity.getStatus())) {
      throw new IllegalStateException("Seules les demandes QUANTITY_VALIDATED ou REVIEWED peuvent être révisées et approuvées");
    }

    entity.setReviewedBy(managerId.getMatricule());
    entity.setReviewedAt(LocalDateTime.now());
    entity.setReviewComments(reviewComments);

    if (modifiedItems != null && !modifiedItems.isEmpty()) {
      entity.getItems().clear();
      for (DotationRequest.DotationItem item : modifiedItems) {
        DotationItemJpaEntity itemEntity = new DotationItemJpaEntity(
          item.getProductId(), item.getProductSku(), item.getProductName(), item.getPackagingType(),
          item.getRequestedQuantity(), item.getQuantityPerCarton()
        );
        itemEntity.setApprovedQuantity(item.getApprovedQuantity());
        itemEntity.setNotes(item.getNotes());
        entity.addItem(itemEntity);
      }
    }

    StockLocationId bufferLocation = stockLocationService.getLocationsByType(StockLocationType.STOCK_BUFFER)
      .stream().findFirst()
      .orElseThrow(() -> new IllegalStateException("Localisation tampon non trouvée"))
      .getId();

    for (DotationItemJpaEntity item : entity.getItems()) {
      BigDecimal approvedQuantity = item.getApprovedQuantity() != null ? item.getApprovedQuantity() : item.getRequestedQuantity();
      Optional<com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem> stockItem =
        stockItemService.getStockItem(bufferLocation, item.getProductSku());
      if (stockItem.isEmpty() || stockItem.get().getQuantity().compareTo(approvedQuantity) < 0) {
        BigDecimal available = stockItem.map(si -> si.getQuantity()).orElse(BigDecimal.ZERO);
        throw new IllegalStateException(String.format(
          "Stock tampon insuffisant pour %s (demandé: %s, disponible: %s)",
          item.getProductSku(), approvedQuantity.stripTrailingZeros().toPlainString(), available.stripTrailingZeros().toPlainString()));
      }
    }

    entity.setApprovedBy(managerId.getMatricule());
    entity.setApprovedAt(LocalDateTime.now());
    entity.setStatus("APPROVED");

    DotationRequestJpaEntity saved = dotationRequestRepository.save(entity);
    log.info("Demande de dotation révisée et approuvée: {} par {}", requestId, managerId.getMatricule());
    return mapToDomain(saved);
  }

  /**
   * Le gestionnaire de stock rejette la demande
   */
  public DotationRequest rejectDotationRequest(Long requestId, UserId rejecterId, String reason) {
    DotationRequestJpaEntity entity = dotationRequestRepository.findById(requestId)
      .orElseThrow(() -> new IllegalArgumentException("Demande de dotation non trouvée: " + requestId));

    if ("COMPLETED".equals(entity.getStatus()) || "REJECTED".equals(entity.getStatus())) {
      throw new IllegalStateException("Cette demande ne peut plus être rejetée");
    }

    entity.setReviewedBy(rejecterId.getMatricule());
    entity.setReviewedAt(LocalDateTime.now());
    entity.setReviewComments(reason);
    entity.setStatus("REJECTED");

    DotationRequestJpaEntity saved = dotationRequestRepository.save(entity);
    log.info("Demande de dotation rejetée: {} par {} - Raison: {}", requestId, rejecterId.getMatricule(), reason);

    return mapToDomain(saved);
  }

  /**
   * Exécute la dotation (transfert du tampon vers le stock mobile du commercial)
   */
  public void executeDotation(Long requestId, UserId executorId) {
    DotationRequestJpaEntity entity = dotationRequestRepository.findById(requestId)
      .orElseThrow(() -> new IllegalArgumentException("Demande de dotation non trouvée: " + requestId));

    if (!"APPROVED".equals(entity.getStatus())) {
      throw new IllegalStateException("Seules les demandes APPROVED peuvent être exécutées");
    }

    StockLocationId bufferLocation = stockLocationService.getLocationsByType(StockLocationType.STOCK_BUFFER)
      .stream()
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Localisation tampon non trouvée"))
      .getId();

    StockLocationId mobileLocation = stockLocationService.getLocationByUser(new UserId(entity.getCommercialMatricule()))
      .map(com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation::getId)
      .orElseGet(() -> {
        StockLocationId newLocationId = stockLocationService.createLocation(
          StockLocationType.STOCK_MOBILE,
          "Stock Mobile - " + entity.getCommercialName(),
          "Stock mobile du commercial " + entity.getCommercialMatricule()
        );
        stockLocationService.assignLocationToUser(newLocationId, new UserId(entity.getCommercialMatricule()));
        return newLocationId;
      });

    for (DotationItemJpaEntity item : entity.getItems()) {
      BigDecimal approvedQuantity = item.getApprovedQuantity() != null ? item.getApprovedQuantity() : item.getRequestedQuantity();

      stockMovementService.createMovement(
        StockMovementType.TRANSFER_BUFFER_TO_MOBILE,
        bufferLocation,
        mobileLocation,
        item.getProductId(),
        item.getProductSku(),
        item.getPackagingType(),
        approvedQuantity,
        item.getQuantityPerCarton(),
        executorId,
        entity.getReferenceNumber(),
        "Dotation pour " + entity.getCommercialName()
      );
    }

    entity.setDeliveredBy(executorId.getMatricule());
    entity.setCompletedAt(LocalDateTime.now());
    entity.setStatus("COMPLETED");

    dotationRequestRepository.save(entity);
    log.info("Demande de dotation exécutée: {} par {}", requestId, executorId.getMatricule());
  }

  /**
   * Récupère toutes les demandes de dotation
   */
  public List<DotationRequest> getAllDotationRequests() {
    return dotationRequestRepository.findAll().stream()
      .map(this::mapToDomain)
      .collect(Collectors.toList());
  }

  /**
   * Récupère une demande par ID
   */
  public Optional<DotationRequest> getRequestById(Long id) {
    return dotationRequestRepository.findById(id)
      .map(this::mapToDomain);
  }

  /**
   * Supprime une demande de dotation
   */
  public void deleteDotationRequest(Long id) {
    dotationRequestRepository.deleteById(id);
    log.info("Demande de dotation supprimée: {}", id);
  }

  /**
   * Récupère les demandes en attente de vérification de paiement (secrétaire)
   */
  public List<DotationRequest> getPendingRequests() {
    return dotationRequestRepository.findPendingRequests().stream()
      .map(this::mapToDomain)
      .collect(Collectors.toList());
  }

  /**
   * Récupère les demandes dont le paiement a été vérifié (en attente validation comptable)
   */
  public List<DotationRequest> getPaymentVerifiedRequests() {
    return dotationRequestRepository.findPaymentVerifiedRequests().stream()
      .map(this::mapToDomain)
      .collect(Collectors.toList());
  }

  /**
   * Récupère les demandes dont les quantités ont été validées (en attente approbation gestionnaire)
   */
  public List<DotationRequest> getQuantityValidatedRequests() {
    return dotationRequestRepository.findQuantityValidatedRequests().stream()
      .map(this::mapToDomain)
      .collect(Collectors.toList());
  }

  /**
   * Récupère les demandes révisées en attente d'approbation
   */
  public List<DotationRequest> getReviewedRequests() {
    return dotationRequestRepository.findByStatus("QUANTITY_VALIDATED").stream()
      .map(this::mapToDomain)
      .collect(Collectors.toList());
  }

  /**
   * Récupère les demandes d'un commercial
   */
  public List<DotationRequest> getRequestsByCommercial(String commercialMatricule) {
    return dotationRequestRepository.findByCommercialMatricule(commercialMatricule).stream()
      .map(this::mapToDomain)
      .collect(Collectors.toList());
  }

  /**
   * Récupère une demande par son numéro de référence
   */
  public Optional<DotationRequest> getRequestByReference(String referenceNumber) {
    return dotationRequestRepository.findByReferenceNumber(referenceNumber)
      .map(this::mapToDomain);
  }

  private String generateReferenceNumber() {
    return "DOT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
  }

  private DotationRequest mapToDomain(DotationRequestJpaEntity entity) {
    DotationRequest request = new DotationRequest(
      new UserId(entity.getCommercialId()),
      entity.getCommercialMatricule(),
      entity.getCommercialName(),
      entity.getJustification(),
      entity.getScheduledDate()
    );
    request.setId(entity.getId());
    request.setStatus(entity.getStatus());
    request.setReferenceNumber(entity.getReferenceNumber());
    if (entity.getDeliveredBy() != null) {
      request.setDeliveredBy(new UserId(entity.getDeliveredBy()));
    }

    List<DotationRequest.DotationItem> items = entity.getItems().stream()
      .map(itemEntity -> {
        DotationRequest.DotationItem item = new DotationRequest.DotationItem(
          itemEntity.getProductId(),
          itemEntity.getProductSku(),
          itemEntity.getProductName(),
          itemEntity.getPackagingType(),
          itemEntity.getRequestedQuantity(),
          itemEntity.getQuantityPerCarton()
        );
        item.setId(itemEntity.getId());
        item.setApprovedQuantity(itemEntity.getApprovedQuantity());
        item.setNotes(itemEntity.getNotes());
        return item;
      })
      .collect(Collectors.toList());

    request.setItems(items);

    return request;
  }
}
