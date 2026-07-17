package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement;
import com.NTFOODS.Api_tanty.modules.stock.domain.report.entity.Report;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ExportService - Service pour exporter des données dans différents formats (PDF, Excel, CSV)
 */
@Service
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private final PdfReportService pdfReportService;

    public ExportService(PdfReportService pdfReportService) {
        this.pdfReportService = pdfReportService;
    }
    
    /**
     * Exporte les items de stock en CSV
     */
    public byte[] exportStockItemsToCSV(List<StockItem> items, String locationName) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // En-tête CSV
        outputStream.write("SKU,Produit,Quantité,Quantité par Carton,Cartons,Poids Unitaire,Volume,Cartons par Assortiment\n".getBytes());
        
        // Données
        for (StockItem item : items) {
            String line = String.format("%s,%s,%.2f,%d,%.2f,%s,%s,%d\n",
                    item.getProductSku(),
                    "Produit", // Nom du produit (à récupérer depuis le service produit)
                    item.getQuantity(),
                    item.getQuantityPerCarton(),
                    item.calculateCartons(),
                    item.getUnitWeight(),
                    item.getVolume(),
                    item.getCartonsPerAssortiment()
            );
            outputStream.write(line.getBytes());
        }
        
        return outputStream.toByteArray();
    }
    
    /**
     * Exporte les mouvements de stock en CSV
     */
    public byte[] exportStockMovementsToCSV(List<StockMovement> movements) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // En-tête CSV
        outputStream.write("Référence,Type,De,À,SKU,Quantité,Date,Statut,Notes\n".getBytes());
        
        // Données
        for (StockMovement movement : movements) {
            String line = String.format("%s,%s,%s,%s,%s,%.2f,%s,%s,%s\n",
                    movement.getReferenceNumber(),
                    movement.getType(),
                    movement.getFromLocationId(),
                    movement.getToLocationId(),
                    movement.getProductSku(),
                    movement.getQuantity(),
                    movement.getRequestedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    movement.getStatus(),
                    movement.getNotes() != null ? movement.getNotes() : ""
            );
            outputStream.write(line.getBytes());
        }
        
        return outputStream.toByteArray();
    }
    
    /**
     * Exporte un rapport en CSV
     */
    public byte[] exportReportToCSV(Report report) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // En-tête CSV
        outputStream.write("Titre,Type,Date Génération,Période Début,Période Fin,Format,Statut\n".getBytes());
        
        // Données
        String line = String.format("%s,%s,%s,%s,%s,%s,%s\n",
                report.getTitle(),
                report.getType(),
                report.getGeneratedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                report.getPeriodStart() != null ? report.getPeriodStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
                report.getPeriodEnd() != null ? report.getPeriodEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
                report.getFormat(),
                report.getStatus()
        );
        outputStream.write(line.getBytes());
        
        return outputStream.toByteArray();
    }
    
    /**
     * Génère un nom de fichier pour l'export
     */
    public String generateFileName(String prefix, Report.ReportFormat format) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String extension = switch (format) {
            case PDF -> ".pdf";
            case EXCEL -> ".xlsx";
            case CSV -> ".csv";
            case JSON -> ".json";
        };
        return String.format("%s_%s%s", prefix, timestamp, extension);
    }
    
    /**
     * Exporte les items de stock en Excel (format CSV simplifié pour l'instant)
     * Note: Pour une vraie implémentation Excel, utiliser Apache POI
     */
    public byte[] exportStockItemsToExcel(List<StockItem> items, String locationName) throws IOException {
        // Pour simplifier, on utilise CSV comme format Excel-compatible
        // Dans une implémentation complète, on utiliserait Apache POI pour générer un vrai .xlsx
        return exportStockItemsToCSV(items, locationName);
    }
    
    /**
     * Exporte les mouvements de stock en Excel (format CSV simplifié pour l'instant)
     */
    public byte[] exportStockMovementsToExcel(List<StockMovement> movements) throws IOException {
        return exportStockMovementsToCSV(movements);
    }
    
    /**
     * Exporte un rapport en Excel (format CSV simplifié pour l'instant)
     */
    public byte[] exportReportToExcel(Report report) throws IOException {
        return exportReportToCSV(report);
    }
    
    /**
     * Exporte les items de stock en PDF (placeholder pour l'instant)
     * Note: Pour une vraie implémentation PDF, utiliser iText ou Apache PDFBox
     */
    public byte[] exportStockItemsToPDF(List<StockItem> items, String locationName) throws IOException {
        return pdfReportService.generateStockItemsPdf(items, locationName, "Inventaire du stock");
    }
    
    /**
     * Exporte les mouvements de stock en PDF (placeholder pour l'instant)
     */
    public byte[] exportStockMovementsToPDF(List<StockMovement> movements) throws IOException {
        return pdfReportService.generateStockMovementsPdf(movements, "Liste des mouvements");
    }
    
    /**
     * Exporte un rapport en PDF (placeholder pour l'instant)
     */
    public byte[] exportReportToPDF(Report report) throws IOException {
        return pdfReportService.generateStockItemsPdf(List.of(), report.getTitle(), report.getDescription());
    }
}
