package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.DashboardStatsResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLevelResponse;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockItemJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockLocationJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockMovementJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockItemRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockLocationRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockMovementRepository;
import com.NTFOODS.Api_tanty.shared.infrastructure.dto.PageRequest;
import com.NTFOODS.Api_tanty.shared.infrastructure.dto.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

  private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

  private final StockItemRepository stockItemRepository;
  private final StockLocationRepository stockLocationRepository;
  private final StockMovementRepository stockMovementRepository;
  private final ProductJpaRepository productJpaRepository;

  public DashboardService(StockItemRepository stockItemRepository,
                          StockLocationRepository stockLocationRepository,
                          StockMovementRepository stockMovementRepository,
                          ProductJpaRepository productJpaRepository) {
    this.stockItemRepository = stockItemRepository;
    this.stockLocationRepository = stockLocationRepository;
    this.stockMovementRepository = stockMovementRepository;
    this.productJpaRepository = productJpaRepository;
  }

  @Cacheable(value = "dashboardStats", key = "'stats'")
  public DashboardStatsResponse getDashboardStats() {
    // Total des articles en stock (tous emplacements confondus)
    long totalStockLevels = stockItemRepository.count();

    // Mouvements en attente
    List<StockMovementJpaEntity> pendingMovements = stockMovementRepository.findPendingMovements();
    int pendingReceipts = (int) pendingMovements.stream()
      .filter(m -> m.getType().name().contains("RECEPTION") || m.getType().name().equals("ENTRY"))
      .count();

    // Total des emplacements
    int totalWarehouses = (int) stockLocationRepository.count();

    // Alertes critiques = stock items avec quantite <= 0 ou tres basse
    // On considere critique si quantite < 10 cartons
    List<StockItemJpaEntity> allItems = stockItemRepository.findAll();
    int criticalAlerts = (int) allItems.stream()
      .filter(item -> item.getQuantity() != null && item.getQuantity().compareTo(new BigDecimal("10")) < 0)
      .count();

    // Valeur totale du stock
    BigDecimal totalStockValue = BigDecimal.ZERO;
    for (StockItemJpaEntity item : allItems) {
      Optional<ProductJpaEntity> productOpt = productJpaRepository.findById(item.getProductId());
      if (productOpt.isPresent()) {
        BigDecimal price = productOpt.get().getUnitPriceAmount() != null ? productOpt.get().getUnitPriceAmount() : BigDecimal.ZERO;
        totalStockValue = totalStockValue.add(item.getQuantity().multiply(price));
      }
    }
    totalStockValue = totalStockValue.setScale(2, RoundingMode.HALF_UP);

    // Lots de production en attente (mouvements de type PRODUCTION avec statut PENDING)
    int pendingBatches = (int) pendingMovements.stream()
      .filter(m -> m.getType().name().equals("PRODUCTION"))
      .count();

    // Commandes internes actives = dotations PENDING + APPROVED
    int activeInternalOrders = (int) pendingMovements.stream()
      .filter(m -> m.getType().name().equals("DOTATION"))
      .count();

    return new DashboardStatsResponse(
      (int) totalStockLevels,
      pendingReceipts,
      pendingBatches,
      activeInternalOrders,
      criticalAlerts,
      totalStockValue,
      totalWarehouses
    );
  }

  @Cacheable(value = "stockAlerts", key = "'alerts-' + #pageRequest.page + '-' + #pageRequest.size")
  public PageResponse<StockLevelResponse> getStockAlerts(PageRequest pageRequest) {
    List<StockItemJpaEntity> allItems = stockItemRepository.findAll();

    // Recuperer la map des produits pour avoir les noms et prix
    Map<Long, ProductJpaEntity> productMap = productJpaRepository.findAll().stream()
      .collect(Collectors.toMap(ProductJpaEntity::getId, p -> p));

    // Recuperer la map des emplacements
    Map<java.util.UUID, StockLocationJpaEntity> locationMap = stockLocationRepository.findAll().stream()
      .collect(Collectors.toMap(StockLocationJpaEntity::getLocationId, loc -> loc));

    List<StockLevelResponse> alerts = new ArrayList<>();
    for (StockItemJpaEntity item : allItems) {
      if (item.getQuantity() == null) continue;

      // Seuil par defaut : 10 cartons
      BigDecimal reorderPoint = new BigDecimal("10");
      String alertLevel = "NORMAL";

      if (item.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
        alertLevel = "CRITIQUE";
      } else if (item.getQuantity().compareTo(reorderPoint) < 0) {
        alertLevel = "CRITIQUE";
      } else if (item.getQuantity().compareTo(new BigDecimal("25")) < 0) {
        alertLevel = "FAIBLE";
      }

      if (!"NORMAL".equals(alertLevel)) {
        ProductJpaEntity product = productMap.get(item.getProductId());
        StockLocationJpaEntity location = locationMap.get(item.getLocationId());

        String productName = product != null ? product.getSku() : item.getProductSku();
        String warehouseName = location != null ? location.getName() : "Inconnu";
        BigDecimal price = product != null && product.getUnitPriceAmount() != null ? product.getUnitPriceAmount() : BigDecimal.ZERO;
        BigDecimal stockValue = item.getQuantity().multiply(price).setScale(2, RoundingMode.HALF_UP);
        Long warehouseId = location != null ? location.getId() : null;

        alerts.add(new StockLevelResponse(
          item.getId(),
          productName,
          item.getProductSku(),
          item.getQuantity().intValue(),
          reorderPoint.intValue(),
          stockValue,
          warehouseId != null ? warehouseId.intValue() : 0,
          warehouseName,
          alertLevel
        ));
      }
    }

    // Trier par niveau d'alerte (CRITIQUE en premier)
    alerts.sort((a, b) -> {
      if ("CRITIQUE".equals(a.getAlertLevel()) && !"CRITIQUE".equals(b.getAlertLevel())) return -1;
      if (!"CRITIQUE".equals(a.getAlertLevel()) && "CRITIQUE".equals(b.getAlertLevel())) return 1;
      return a.getQuantity().compareTo(b.getQuantity());
    });

    return paginate(alerts, pageRequest);
  }

  @Cacheable(value = "stockLevels", key = "'levels-' + #pageRequest.page + '-' + #pageRequest.size")
  public PageResponse<StockLevelResponse> getStockLevels(PageRequest pageRequest) {
    List<StockItemJpaEntity> allItems = stockItemRepository.findAll();

    Map<Long, ProductJpaEntity> productMap = productJpaRepository.findAll().stream()
      .collect(Collectors.toMap(ProductJpaEntity::getId, p -> p));

    Map<java.util.UUID, StockLocationJpaEntity> locationMap = stockLocationRepository.findAll().stream()
      .collect(Collectors.toMap(StockLocationJpaEntity::getLocationId, loc -> loc));

    List<StockLevelResponse> levels = new ArrayList<>();
    for (StockItemJpaEntity item : allItems) {
      ProductJpaEntity product = productMap.get(item.getProductId());
      StockLocationJpaEntity location = locationMap.get(item.getLocationId());

      String productName = product != null ? product.getSku() : item.getProductSku();
      String warehouseName = location != null ? location.getName() : "Inconnu";
      BigDecimal reorderPoint = new BigDecimal("10");
      BigDecimal price = product != null && product.getUnitPriceAmount() != null ? product.getUnitPriceAmount() : BigDecimal.ZERO;
      BigDecimal stockValue = item.getQuantity().multiply(price).setScale(2, RoundingMode.HALF_UP);
      Long warehouseId = location != null ? location.getId() : null;

      String alertLevel = "NORMAL";
      if (item.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
        alertLevel = "CRITIQUE";
      } else if (item.getQuantity().compareTo(reorderPoint) < 0) {
        alertLevel = "CRITIQUE";
      } else if (item.getQuantity().compareTo(new BigDecimal("25")) < 0) {
        alertLevel = "FAIBLE";
      } else if (item.getQuantity().compareTo(new BigDecimal("100")) > 0) {
        alertLevel = "SURPLUS";
      }

      levels.add(new StockLevelResponse(
        item.getId(),
        productName,
        item.getProductSku(),
        item.getQuantity().intValue(),
        reorderPoint.intValue(),
        stockValue,
        warehouseId != null ? warehouseId.intValue() : 0,
        warehouseName,
        alertLevel
      ));
    }

    return paginate(levels, pageRequest);
  }

  private PageResponse<StockLevelResponse> paginate(List<StockLevelResponse> allItems, PageRequest pageRequest) {
    int start = pageRequest.getOffset();
    int end = Math.min(start + pageRequest.getSize(), allItems.size());

    if (start >= allItems.size()) {
      return new PageResponse<>(new ArrayList<>(), pageRequest.getPage(), pageRequest.getSize(), allItems.size());
    }

    List<StockLevelResponse> paged = allItems.subList(start, end);
    return new PageResponse<>(paged, pageRequest.getPage(), pageRequest.getSize(), allItems.size());
  }
}
