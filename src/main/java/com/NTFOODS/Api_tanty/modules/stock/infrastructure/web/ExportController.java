package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.ExportService;
import com.NTFOODS.Api_tanty.modules.stock.application.service.ExcelReportService;
import com.NTFOODS.Api_tanty.modules.stock.application.service.PdfReportService;
import com.NTFOODS.Api_tanty.modules.stock.application.service.ProductCatalogService;
import com.NTFOODS.Api_tanty.modules.stock.application.service.StockItemService;
import com.NTFOODS.Api_tanty.modules.stock.application.service.StockMovementService;
import com.NTFOODS.Api_tanty.modules.stock.application.service.StockLocationService;
import com.NTFOODS.Api_tanty.modules.stock.application.dto.ProductCatalogResponse;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.domain.report.entity.Report;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock/export")
public class ExportController {

    private final ExportService exportService;
    private final StockItemService stockItemService;
    private final StockMovementService stockMovementService;
    private final StockLocationService stockLocationService;
    private final PdfReportService pdfReportService;
    private final ProductCatalogService productCatalogService;
    private final ExcelReportService excelReportService;

    public ExportController(ExportService exportService,
                           StockItemService stockItemService,
                           StockMovementService stockMovementService,
                           StockLocationService stockLocationService,
                           PdfReportService pdfReportService,
                           ProductCatalogService productCatalogService,
                           ExcelReportService excelReportService) {
        this.exportService = exportService;
        this.stockItemService = stockItemService;
        this.stockMovementService = stockMovementService;
        this.stockLocationService = stockLocationService;
        this.pdfReportService = pdfReportService;
        this.productCatalogService = productCatalogService;
        this.excelReportService = excelReportService;
    }

    @GetMapping("/items/{locationId}/{format}")
    public ResponseEntity<byte[]> exportStockItems(@PathVariable UUID locationId, @PathVariable String format) throws IOException {
        StockLocationId stockLocationId = new StockLocationId(locationId);
        List<StockItem> items = stockItemService.getStockItemsByLocation(stockLocationId);

        byte[] data;
        String contentType;
        String filename;

        switch (format.toLowerCase()) {
            case "csv":
                data = exportService.exportStockItemsToCSV(items, "Stock");
                contentType = "text/csv";
                filename = exportService.generateFileName("stock_items", Report.ReportFormat.CSV);
                break;
            case "excel":
                data = exportService.exportStockItemsToExcel(items, "Stock");
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                filename = exportService.generateFileName("stock_items", Report.ReportFormat.EXCEL);
                break;
            case "pdf":
                data = exportService.exportStockItemsToPDF(items, "Stock");
                contentType = "application/pdf";
                filename = exportService.generateFileName("stock_items", Report.ReportFormat.PDF);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @GetMapping("/movements/{format}")
    public ResponseEntity<byte[]> exportStockMovements(@PathVariable String format) throws IOException {
        List<StockMovement> movements = stockMovementService.getPendingMovements();

        byte[] data;
        String contentType;
        String filename;

        switch (format.toLowerCase()) {
            case "csv":
                data = exportService.exportStockMovementsToCSV(movements);
                contentType = "text/csv";
                filename = exportService.generateFileName("stock_movements", Report.ReportFormat.CSV);
                break;
            case "excel":
                data = exportService.exportStockMovementsToExcel(movements);
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                filename = exportService.generateFileName("stock_movements", Report.ReportFormat.EXCEL);
                break;
            case "pdf":
                data = exportService.exportStockMovementsToPDF(movements);
                contentType = "application/pdf";
                filename = exportService.generateFileName("stock_movements", Report.ReportFormat.PDF);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    // ── FICHES DE SYNTHÈSE ─────────────────────────────────────

    @GetMapping("/fiche-synthese/stock/{locationType}")
    public ResponseEntity<byte[]> ficheSyntheseStock(@PathVariable String locationType,
                                                     @RequestParam(defaultValue = "Inventaire du stock") String motif) throws IOException {
        StockLocationType type = StockLocationType.valueOf(locationType.toUpperCase());
        StockLocation location = stockLocationService.getLocationsByType(type).stream()
            .findFirst().orElseThrow(() -> new IllegalStateException("Location non trouvée: " + locationType));
        List<StockItem> items = stockItemService.getStockItemsByLocation(location.getId());
        List<StockMovement> movements = stockMovementService.getAllMovements();
        byte[] pdf = pdfReportService.generateFicheSyntheseStock(items, movements, location.getName(), motif);
        return pdfResponse(pdf, "fiche_synthese_stock_" + locationType.toLowerCase() + ".pdf");
    }

    @GetMapping("/fiche-synthese/sorties")
    public ResponseEntity<byte[]> ficheSyntheseSorties(@RequestParam(defaultValue = "Sorties de stock") String motif,
                                                       @RequestParam LocalDateTime periodStart,
                                                       @RequestParam LocalDateTime periodEnd) throws IOException {
        List<StockMovement> movements = stockMovementService.getMovementsByPeriod(periodStart, periodEnd);
        byte[] pdf = pdfReportService.generateFicheSyntheseSorties(movements, motif, periodStart, periodEnd);
        return pdfResponse(pdf, "fiche_synthese_sorties.pdf");
    }

    @GetMapping("/fiche-synthese/entrees")
    public ResponseEntity<byte[]> ficheSyntheseEntrees(@RequestParam(defaultValue = "Entrées de stock") String motif,
                                                       @RequestParam LocalDateTime periodStart,
                                                       @RequestParam LocalDateTime periodEnd) throws IOException {
        List<StockMovement> movements = stockMovementService.getMovementsByPeriod(periodStart, periodEnd);
        byte[] pdf = pdfReportService.generateFicheSyntheseEntrees(movements, motif, periodStart, periodEnd);
        return pdfResponse(pdf, "fiche_synthese_entrees.pdf");
    }

    @GetMapping("/fiche-synthese/globale")
    public ResponseEntity<byte[]> ficheSyntheseGlobale(@RequestParam(defaultValue = "Synthèse globale du stock") String motif,
                                                       @RequestParam LocalDateTime periodStart,
                                                       @RequestParam LocalDateTime periodEnd) throws IOException {
        List<StockItem> centralItems = stockLocationService.getLocationsByType(StockLocationType.STOCK_CENTRAL).stream()
            .findFirst().map(loc -> stockItemService.getStockItemsByLocation(loc.getId()))
            .orElse(List.of());
        List<StockMovement> movements = stockMovementService.getMovementsByPeriod(periodStart, periodEnd);
        byte[] pdf = pdfReportService.generateFicheSyntheseGlobale(centralItems, movements, motif, periodStart, periodEnd);
        return pdfResponse(pdf, "fiche_synthese_globale.pdf");
    }

    @GetMapping("/fiche-synthese/ntfoods")
    public ResponseEntity<byte[]> ficheSyntheseNTFoods(
            @RequestParam(defaultValue = "Production") String motif,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) Integer nombreColis) throws IOException {
        List<String> codes = productCatalogService.getAll().stream()
            .map(ProductCatalogResponse::getSku)
            .filter(sku -> sku != null && !sku.isEmpty())
            .distinct()
            .collect(Collectors.toList());
        byte[] pdf = pdfReportService.generateFicheSyntheseNTFoods(codes, motif, nom, ville, zone, nombreColis);
        return pdfResponse(pdf, "fiche_synthese_tanty.pdf");
    }

    // ── NOUVEAUX RAPPORTS (valorisation, alertes, inventaire, rotation) ──────

    @GetMapping("/valorisation/{locationType}")
    public ResponseEntity<byte[]> rapportValorisation(@PathVariable String locationType,
                                                      @RequestParam(defaultValue = "Valorisation du stock") String motif) throws IOException {
        StockLocationType type = StockLocationType.valueOf(locationType.toUpperCase());
        StockLocation location = stockLocationService.getLocationsByType(type).stream()
            .findFirst().orElseThrow(() -> new IllegalStateException("Location non trouvée: " + locationType));
        List<StockItem> items = stockItemService.getStockItemsByLocation(location.getId());
        Map<String, String> designations = buildDesignations(items);
        byte[] pdf = pdfReportService.generateRapportValorisation(items, designations, location.getName(), motif);
        return pdfResponse(pdf, "rapport_valorisation_" + locationType.toLowerCase() + ".pdf");
    }

    @GetMapping("/alertes/{locationType}")
    public ResponseEntity<byte[]> rapportAlertes(@PathVariable String locationType) throws IOException {
        StockLocationType type = StockLocationType.valueOf(locationType.toUpperCase());
        StockLocation location = stockLocationService.getLocationsByType(type).stream()
            .findFirst().orElseThrow(() -> new IllegalStateException("Location non trouvée: " + locationType));
        List<StockItem> items = stockItemService.getStockItemsByLocation(location.getId());
        Map<String, String> designations = buildDesignations(items);
        byte[] pdf = pdfReportService.generateRapportAlertes(items, designations, location.getName());
        return pdfResponse(pdf, "rapport_alertes_" + locationType.toLowerCase() + ".pdf");
    }

    @GetMapping("/inventaire/{locationType}")
    public ResponseEntity<byte[]> inventaireComplet(@PathVariable String locationType,
                                                     @RequestParam(defaultValue = "Inventaire complet") String motif) throws IOException {
        StockLocationType type = StockLocationType.valueOf(locationType.toUpperCase());
        StockLocation location = stockLocationService.getLocationsByType(type).stream()
            .findFirst().orElseThrow(() -> new IllegalStateException("Location non trouvée: " + locationType));
        List<StockItem> items = stockItemService.getStockItemsByLocation(location.getId());
        Map<String, String> designations = buildDesignations(items);
        byte[] pdf = pdfReportService.generateInventaireComplet(items, designations, location.getName(), motif);
        return pdfResponse(pdf, "inventaire_complet_" + locationType.toLowerCase() + ".pdf");
    }

    @GetMapping("/rotation/{locationType}")
    public ResponseEntity<byte[]> rapportRotation(@PathVariable String locationType,
                                                   @RequestParam(defaultValue = "Rotation du stock") String motif,
                                                   @RequestParam LocalDateTime periodStart,
                                                   @RequestParam LocalDateTime periodEnd) throws IOException {
        StockLocationType type = StockLocationType.valueOf(locationType.toUpperCase());
        StockLocation location = stockLocationService.getLocationsByType(type).stream()
            .findFirst().orElseThrow(() -> new IllegalStateException("Location non trouvée: " + locationType));
        List<StockItem> items = stockItemService.getStockItemsByLocation(location.getId());
        List<StockMovement> movements = stockMovementService.getMovementsByPeriod(periodStart, periodEnd);
        Map<String, String> designations = buildDesignations(items);
        byte[] pdf = pdfReportService.generateRapportRotation(items, movements, designations, location.getName(), motif, periodStart, periodEnd);
        return pdfResponse(pdf, "rapport_rotation_" + locationType.toLowerCase() + ".pdf");
    }

    private Map<String, String> buildDesignations(List<StockItem> items) {
        Map<Long, ProductCatalogResponse> products = productCatalogService.getByIds(
            items.stream().map(StockItem::getProductId).filter(java.util.Objects::nonNull).toList());
        Map<String, String> designations = new java.util.HashMap<>();
        for (StockItem item : items) {
            ProductCatalogResponse p = products.get(item.getProductId());
            String designation = p != null ? p.getDesignation() : "—";
            designations.put(item.getProductSku(), designation);
        }
        return designations;
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    private ResponseEntity<byte[]> excelResponse(byte[] data, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(data);
    }

    // ── EXCEL WITH CHARTS ──────────────────────────────────────

    @GetMapping("/excel/items/{locationType}")
    public ResponseEntity<byte[]> exportStockItemsExcelWithCharts(@PathVariable String locationType) throws IOException {
        StockLocationType type = StockLocationType.valueOf(locationType.toUpperCase());
        StockLocation location = stockLocationService.getLocationsByType(type).stream()
            .findFirst().orElseThrow(() -> new IllegalStateException("Location non trouvée: " + locationType));
        List<StockItem> items = stockItemService.getStockItemsByLocation(location.getId());
        Map<String, String> designations = buildDesignations(items);
        byte[] data = excelReportService.generateStockItemsExcel(items, location.getName(), designations);
        return excelResponse(data, "stock_items_" + locationType.toLowerCase() + ".xlsx");
    }

    @GetMapping("/excel/movements")
    public ResponseEntity<byte[]> exportMovementsExcelWithCharts() throws IOException {
        List<StockMovement> movements = stockMovementService.getAllMovements();
        byte[] data = excelReportService.generateStockMovementsExcel(movements);
        return excelResponse(data, "stock_movements.xlsx");
    }

    @GetMapping("/excel/valorisation/{locationType}")
    public ResponseEntity<byte[]> exportValorisationExcelWithCharts(@PathVariable String locationType) throws IOException {
        StockLocationType type = StockLocationType.valueOf(locationType.toUpperCase());
        StockLocation location = stockLocationService.getLocationsByType(type).stream()
            .findFirst().orElseThrow(() -> new IllegalStateException("Location non trouvée: " + locationType));
        List<StockItem> items = stockItemService.getStockItemsByLocation(location.getId());
        Map<String, String> designations = buildDesignations(items);
        byte[] data = excelReportService.generateValorisationExcel(items, designations, location.getName());
        return excelResponse(data, "valorisation_" + locationType.toLowerCase() + ".xlsx");
    }

    @GetMapping("/excel/global/{locationType}")
    public ResponseEntity<byte[]> exportGlobalReportExcelWithCharts(
            @PathVariable String locationType,
            @RequestParam LocalDateTime periodStart,
            @RequestParam LocalDateTime periodEnd) throws IOException {
        StockLocationType type = StockLocationType.valueOf(locationType.toUpperCase());
        StockLocation location = stockLocationService.getLocationsByType(type).stream()
            .findFirst().orElseThrow(() -> new IllegalStateException("Location non trouvée: " + locationType));
        List<StockItem> items = stockItemService.getStockItemsByLocation(location.getId());
        List<StockMovement> movements = stockMovementService.getMovementsByPeriod(periodStart, periodEnd);
        Map<String, String> designations = buildDesignations(items);
        byte[] data = excelReportService.generateGlobalReportExcel(items, movements, designations, location.getName(), periodStart, periodEnd);
        return excelResponse(data, "rapport_global_" + locationType.toLowerCase() + ".xlsx");
    }
}
