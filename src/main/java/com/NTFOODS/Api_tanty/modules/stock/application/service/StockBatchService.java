package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.batch.entity.StockBatch;
import com.NTFOODS.Api_tanty.modules.stock.domain.batch.repository.StockBatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * StockBatchService - Gestion des lots de stock (traçabilité FIFO/FEFO).
 */
@Service
@Transactional
public class StockBatchService {

    private static final Logger log = LoggerFactory.getLogger(StockBatchService.class);

    private final StockBatchRepository stockBatchRepository;

    public StockBatchService(StockBatchRepository stockBatchRepository) {
        this.stockBatchRepository = stockBatchRepository;
    }

    public StockBatch createBatch(String batchNumber, Long productId, String productSku, String productName,
                                   String supplierName, LocalDate manufactureDate, LocalDate expiryDate,
                                   BigDecimal initialQuantity, String locationId, String notes) {
        if (stockBatchRepository.findByBatchNumber(batchNumber).isPresent()) {
            throw new IllegalArgumentException("Batch number already exists: " + batchNumber);
        }
        StockBatch batch = new StockBatch(
                batchNumber,
                productId,
                productSku,
                productName,
                supplierName,
                manufactureDate,
                expiryDate,
                initialQuantity,
                initialQuantity,
                locationId,
                determineInitialStatus(expiryDate),
                notes
        );
        StockBatch saved = stockBatchRepository.save(batch);
        log.info("Created batch {} for product {}", batchNumber, productSku);
        return saved;
    }

    public List<StockBatch> getAllBatches() {
        return stockBatchRepository.findAll();
    }

    public Optional<StockBatch> getBatch(Long id) {
        return stockBatchRepository.findById(id);
    }

    public List<StockBatch> getBatchesByProduct(Long productId) {
        return stockBatchRepository.findByProductId(productId);
    }

    public List<StockBatch> getBatchesBySku(String productSku) {
        return stockBatchRepository.findByProductSku(productSku);
    }

    public List<StockBatch> getBatchesByLocation(String locationId) {
        return stockBatchRepository.findByLocationId(locationId);
    }

    /**
     * Retourne les lots disponibles pour un produit/emplacement, triés par date de péremption (FEFO),
     * puis par date de création (FIFO).
     */
    public List<StockBatch> getAvailableBatchesForConsumption(String productSku, String locationId) {
        return stockBatchRepository.findActiveByProductSkuAndLocation(productSku, locationId);
    }

    /**
     * Consomme une quantité selon la méthode FEFO/FIFO.
     */
    public void consumeQuantity(String productSku, String locationId, BigDecimal quantity) {
        List<StockBatch> batches = getAvailableBatchesForConsumption(productSku, locationId);
        BigDecimal remainingToConsume = quantity;

        for (StockBatch batch : batches) {
            if (remainingToConsume.compareTo(BigDecimal.ZERO) <= 0) break;
            if (batch.getRemainingQuantity().compareTo(remainingToConsume) >= 0) {
                batch.consume(remainingToConsume);
                remainingToConsume = BigDecimal.ZERO;
            } else {
                BigDecimal available = batch.getRemainingQuantity();
                batch.consume(available);
                remainingToConsume = remainingToConsume.subtract(available);
            }
            stockBatchRepository.save(batch);
        }

        if (remainingToConsume.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Insufficient batch quantity for " + productSku + " in " + locationId);
        }
    }

    /**
     * Lots proches de la péremption (dans les 30 jours).
     */
    public List<StockBatch> getExpiringSoonBatches(int days) {
        return stockBatchRepository.findExpiringBefore(LocalDate.now().plusDays(days));
    }

    /**
     * Lots expirés non encore marqués EXPIRED.
     */
    public List<StockBatch> getExpiredBatches() {
        return stockBatchRepository.findExpired();
    }

    public StockBatch updateBatchStatus(Long id, String status) {
        StockBatch batch = stockBatchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + id));
        batch.setStatus(status);
        return stockBatchRepository.save(batch);
    }

    public void deleteBatch(Long id) {
        stockBatchRepository.findById(id).ifPresent(b -> {
            if (b.getRemainingQuantity() != null && b.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalStateException("Cannot delete batch with remaining quantity");
            }
        });
        // Domain repository does not expose delete; set status to EMPTY instead.
        StockBatch batch = stockBatchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + id));
        batch.setStatus("DELETED");
        stockBatchRepository.save(batch);
    }

    private String determineInitialStatus(LocalDate expiryDate) {
        if (expiryDate != null && LocalDate.now().isAfter(expiryDate)) {
            return "EXPIRED";
        }
        return "AVAILABLE";
    }
}
