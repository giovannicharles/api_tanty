package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DocumentManagementService - Service pour gérer l'import/export de documents
 * Gère l'import de fichiers CSV/Excel pour la mise à jour du stock
 */
@Service
@Transactional
public class DocumentManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentManagementService.class);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    private final StockItemService stockItemService;
    private final StockLocationService stockLocationService;
    private final ExportService exportService;
    
    public DocumentManagementService(StockItemService stockItemService,
                                    StockLocationService stockLocationService,
                                    ExportService exportService) {
        this.stockItemService = stockItemService;
        this.stockLocationService = stockLocationService;
        this.exportService = exportService;
    }
    
    /**
     * Importe des items de stock depuis un fichier CSV
     * Format attendu: SKU,Produit,Quantité,Quantité par Carton,Poids Unitaire,Volume,Cartons par Assortiment
     */
    public DocumentImportResult importStockItemsFromCSV(MultipartFile file, UserId importedBy, 
                                                      String locationName) throws IOException {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        
        String content = new String(file.getBytes());
        String[] lines = content.split("\n");
        
        // Skip header line
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            
            try {
                String[] parts = line.split(",");
                if (parts.length < 7) {
                    errors.add("Ligne " + (i + 1) + ": Format invalide - colonnes manquantes");
                    failureCount++;
                    continue;
                }
                
                String sku = parts[0].trim();
                // String productName = parts[1].trim(); // Non utilisé pour l'instant
                double quantity = Double.parseDouble(parts[2].trim());
                int quantityPerCarton = Integer.parseInt(parts[3].trim());
                String unitWeight = parts[4].trim();
                String volume = parts[5].trim();
                int cartonsPerAssortiment = Integer.parseInt(parts[6].trim());
                
                // Récupérer la localisation (simplifié - utilise la première localisation du type spécifié)
                var locations = stockLocationService.getLocationsByType(
                        com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_CENTRAL);
                
                if (locations.isEmpty()) {
                    errors.add("Ligne " + (i + 1) + ": Aucune localisation trouvée");
                    failureCount++;
                    continue;
                }
                
                var locationId = locations.get(0).getId();
                
                stockItemService.createOrUpdateStockItem(
                        locationId,
                        null, // productId
                        sku,
                        "UNIT", // packagingType
                        java.math.BigDecimal.valueOf(quantity),
                        java.math.BigDecimal.valueOf(quantityPerCarton),
                        java.math.BigDecimal.valueOf(Double.parseDouble(unitWeight.replace("kg", "").trim())),
                        volume,
                        cartonsPerAssortiment,
                        importedBy
                );
                
                successCount++;
                log.info("Imported stock item: {}", sku);
                
            } catch (Exception e) {
                errors.add("Ligne " + (i + 1) + ": " + e.getMessage());
                failureCount++;
                log.error("Error importing line {}: {}", i + 1, e.getMessage());
            }
        }
        
        return new DocumentImportResult(successCount, failureCount, errors);
    }
    
    /**
     * Exporte les items de stock vers un fichier CSV
     */
    public byte[] exportStockItemsToCSV(String locationName) throws IOException {
        var locations = stockLocationService.getLocationsByType(
                com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType.STOCK_CENTRAL);
        
        if (locations.isEmpty()) {
            throw new IllegalStateException("No stock location found");
        }
        
        var locationId = locations.get(0).getId();
        var stockItems = stockItemService.getStockItemsByLocation(locationId);
        
        return exportService.exportStockItemsToCSV(stockItems, locationName);
    }
    
    /**
     * Exporte les mouvements de stock vers un fichier CSV
     */
    public byte[] exportStockMovementsToCSV(java.time.LocalDateTime startDate, 
                                           java.time.LocalDateTime endDate) throws IOException {
        // Placeholder: Pour une implémentation complète, utiliser StockMovementService
        // var movements = stockMovementService.getMovementsByPeriod(startDate, endDate);
        // return exportService.exportStockMovementsToCSV(movements);
        return "Export des mouvements de stock - Fonctionnalité à compléter".getBytes();
    }
    
    /**
     * Génère un nom de fichier unique
     */
    public String generateUniqueFileName(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        return String.format("%s_%s.%s", prefix, timestamp, extension);
    }
    
    /**
     * DocumentImportResult - Résultat d'un import de document
     */
    public static class DocumentImportResult {
        private final int successCount;
        private final int failureCount;
        private final List<String> errors;
        
        public DocumentImportResult(int successCount, int failureCount, List<String> errors) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.errors = errors;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public int getFailureCount() {
            return failureCount;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public int getTotalCount() {
            return successCount + failureCount;
        }
    }
}
