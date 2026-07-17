package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.DotationService;
import com.NTFOODS.Api_tanty.modules.stock.application.service.PdfReportService;
import com.NTFOODS.Api_tanty.modules.stock.domain.dotation.entity.DotationRequest;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * DotationController - Contrôleur REST pour la gestion des dotations
 * Workflow: Commercial soumet → Secrétaire vérifie paiement → Comptable valide quantités → Gestionnaire stock approuve → Livraison
 */
@RestController
@RequestMapping("/api/stock/dotations")
public class DotationController {

  private static final Logger log = LoggerFactory.getLogger(DotationController.class);

  private final DotationService dotationService;
  private final PdfReportService pdfReportService;

  public DotationController(DotationService dotationService, PdfReportService pdfReportService) {
    this.dotationService = dotationService;
    this.pdfReportService = pdfReportService;
  }

  @PostMapping
  public ResponseEntity<DotationRequest> createDotationRequest(@RequestBody Map<String, Object> body) {
    try {
      String commercialId = (String) body.get("commercialId");
      String commercialMatricule = (String) body.get("commercialMatricule");
      String commercialName = (String) body.get("commercialName");
      String justification = (String) body.getOrDefault("justification", "");

      List<DotationRequest.DotationItem> items = parseDotationItems(body.get("items"));

      DotationRequest created = dotationService.createDotationRequest(
        new UserId(commercialId != null ? commercialId : commercialMatricule),
        commercialMatricule,
        commercialName,
        items,
        justification
      );
      return ResponseEntity.ok(created);
    } catch (Exception e) {
      log.error("Erreur création dotation: {}", e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<DotationRequest> getDotationRequest(@PathVariable Long id) {
    try {
      DotationRequest request = dotationService.getRequestById(id)
        .orElseThrow(() -> new IllegalArgumentException("Dotation non trouvée: " + id));
      return ResponseEntity.ok(request);
    } catch (Exception e) {
      log.error("Erreur récupération dotation {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping
  public ResponseEntity<List<DotationRequest>> getAllDotationRequests() {
    return ResponseEntity.ok(dotationService.getAllDotationRequests());
  }

  @GetMapping("/pending")
  public ResponseEntity<List<DotationRequest>> getPendingDotationRequests() {
    return ResponseEntity.ok(dotationService.getPendingRequests());
  }

  @GetMapping("/payment-verified")
  public ResponseEntity<List<DotationRequest>> getPaymentVerifiedDotationRequests() {
    return ResponseEntity.ok(dotationService.getPaymentVerifiedRequests());
  }

  @GetMapping("/quantity-validated")
  public ResponseEntity<List<DotationRequest>> getQuantityValidatedDotationRequests() {
    return ResponseEntity.ok(dotationService.getQuantityValidatedRequests());
  }

  @GetMapping("/reviewed")
  public ResponseEntity<List<DotationRequest>> getReviewedDotationRequests() {
    return ResponseEntity.ok(dotationService.getReviewedRequests());
  }

  @GetMapping("/commercial/{matricule}")
  public ResponseEntity<List<DotationRequest>> getDotationRequestsByCommercial(@PathVariable String matricule) {
    return ResponseEntity.ok(dotationService.getRequestsByCommercial(matricule));
  }

  @GetMapping("/reference/{reference}")
  public ResponseEntity<DotationRequest> getDotationRequestByReference(@PathVariable String reference) {
    return dotationService.getRequestByReference(reference)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}/verify-payment")
  public ResponseEntity<DotationRequest> verifyPayment(
    @PathVariable Long id,
    @RequestParam String verifierId) {
    try {
      DotationRequest verified = dotationService.verifyPayment(id, new UserId(verifierId));
      return ResponseEntity.ok(verified);
    } catch (Exception e) {
      log.error("Erreur vérification paiement dotation {}: {}", id, e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}/validate-quantities")
  public ResponseEntity<DotationRequest> validateQuantities(
    @PathVariable Long id,
    @RequestBody Map<String, Object> body) {
    try {
      String validatorId = (String) body.get("validatorId");
      String comments = (String) body.getOrDefault("comments", "");

      List<DotationRequest.DotationItem> modifiedItems = parseDotationItems(body.get("items"));

      DotationRequest validated = dotationService.validateQuantities(
        id, new UserId(validatorId), comments, modifiedItems
      );
      return ResponseEntity.ok(validated);
    } catch (Exception e) {
      log.error("Erreur validation quantités dotation {}: {}", id, e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}/review")
  public ResponseEntity<DotationRequest> reviewDotationRequest(
    @PathVariable Long id,
    @RequestBody Map<String, Object> body) {
    try {
      String reviewerId = (String) body.get("reviewerId");
      String reviewComments = (String) body.getOrDefault("reviewComments", "");

      List<DotationRequest.DotationItem> modifiedItems = parseDotationItems(body.get("items"));

      DotationRequest reviewed = dotationService.reviewDotationRequest(
        id, new UserId(reviewerId), reviewComments, modifiedItems
      );
      return ResponseEntity.ok(reviewed);
    } catch (Exception e) {
      log.error("Erreur révision dotation {}: {}", id, e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}/approve")
  public ResponseEntity<DotationRequest> approveDotationRequest(
    @PathVariable Long id,
    @RequestParam String approverId,
    @RequestParam(required = false) String notes) {
    try {
      DotationRequest approved = dotationService.approveDotationRequest(id, new UserId(approverId));
      return ResponseEntity.ok(approved);
    } catch (Exception e) {
      log.error("Erreur approbation dotation {}: {}", id, e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}/review-and-approve")
  public ResponseEntity<DotationRequest> reviewAndApproveDotationRequest(
    @PathVariable Long id,
    @RequestBody Map<String, Object> body) {
    try {
      String managerId = (String) body.get("managerId");
      String reviewComments = (String) body.getOrDefault("reviewComments", "");
      List<DotationRequest.DotationItem> modifiedItems = parseDotationItems(body.get("items"));
      DotationRequest reviewed = dotationService.reviewAndApproveDotationRequest(
        id, new UserId(managerId), reviewComments, modifiedItems);
      return ResponseEntity.ok(reviewed);
    } catch (Exception e) {
      log.error("Erreur révision/approbation dotation {}: {}", id, e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}/reject")
  public ResponseEntity<DotationRequest> rejectDotationRequest(
    @PathVariable Long id,
    @RequestParam String rejecterId,
    @RequestParam String reason) {
    try {
      DotationRequest rejected = dotationService.rejectDotationRequest(id, new UserId(rejecterId), reason);
      return ResponseEntity.ok(rejected);
    } catch (Exception e) {
      log.error("Erreur rejet dotation {}: {}", id, e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}/execute")
  public ResponseEntity<DotationRequest> executeDotationRequest(
    @PathVariable Long id,
    @RequestParam String executorId) {
    try {
      dotationService.executeDotation(id, new UserId(executorId));
      DotationRequest executed = dotationService.getRequestById(id)
        .orElseThrow(() -> new IllegalArgumentException("Dotation non trouvée: " + id));
      return ResponseEntity.ok(executed);
    } catch (Exception e) {
      log.error("Erreur exécution dotation {}: {}", id, e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{id}/fiche-synthese")
  public ResponseEntity<byte[]> downloadFicheSynthese(@PathVariable Long id) {
    try {
      DotationRequest dotation = dotationService.getRequestById(id)
        .orElseThrow(() -> new IllegalArgumentException("Dotation non trouvée: " + id));
      List<String> codes = dotation.getItems().stream()
        .map(item -> item.getProductSku() != null ? item.getProductSku() : "—")
        .distinct()
        .toList();
      byte[] pdf = pdfReportService.generateFicheSyntheseNTFoods(
        codes, "Dotation", dotation.getCommercialName(), null, null, null);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDispositionFormData("attachment", "fiche_synthese_dotation_" + id + ".pdf");
      return ResponseEntity.ok().headers(headers).body(pdf);
    } catch (IOException e) {
      log.error("Erreur génération fiche synthèse dotation {}: {}", id, e.getMessage());
      return ResponseEntity.internalServerError().build();
    } catch (Exception e) {
      log.error("Erreur fiche synthèse dotation {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDotationRequest(@PathVariable Long id) {
    try {
      dotationService.deleteDotationRequest(id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      log.error("Erreur suppression dotation {}: {}", id, e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Convertit une liste de LinkedHashMap (désérialisation Jackson) en List<DotationItem>
   */
  @SuppressWarnings("unchecked")
  private List<DotationRequest.DotationItem> parseDotationItems(Object rawItems) {
    if (rawItems == null) {
      return new ArrayList<>();
    }
    List<DotationRequest.DotationItem> result = new ArrayList<>();
    List<?> itemList = (List<?>) rawItems;
    for (Object item : itemList) {
      if (item instanceof DotationRequest.DotationItem) {
        result.add((DotationRequest.DotationItem) item);
      } else if (item instanceof LinkedHashMap) {
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) item;
        Long productId = map.get("productId") != null
          ? toLong(map.get("productId"))
          : null;
        String productSku = (String) map.getOrDefault("productSku", "");
        String productName = (String) map.getOrDefault("productName", "");
        String packagingType = (String) map.getOrDefault("packagingType", "UNITE");
        BigDecimal requestedQuantity = toBigDecimal(map.get("requestedQuantity"));
        BigDecimal quantityPerCarton = toBigDecimal(map.get("quantityPerCarton"));

        DotationRequest.DotationItem dotationItem = new DotationRequest.DotationItem(
          productId, productSku, productName, packagingType,
          requestedQuantity, quantityPerCarton
        );
        if (map.get("approvedQuantity") != null) {
          dotationItem.setApprovedQuantity(toBigDecimal(map.get("approvedQuantity")));
        }
        if (map.get("notes") != null) {
          dotationItem.setNotes((String) map.get("notes"));
        }
        result.add(dotationItem);
      }
    }
    return result;
  }

  private BigDecimal toBigDecimal(Object value) {
    if (value == null) return BigDecimal.ZERO;
    if (value instanceof BigDecimal) return (BigDecimal) value;
    if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
    if (value instanceof String) return new BigDecimal((String) value);
    return BigDecimal.ZERO;
  }

  private Long toLong(Object value) {
    if (value == null) return null;
    if (value instanceof Number) return ((Number) value).longValue();
    if (value instanceof String) return Long.parseLong((String) value);
    return null;
  }
}
