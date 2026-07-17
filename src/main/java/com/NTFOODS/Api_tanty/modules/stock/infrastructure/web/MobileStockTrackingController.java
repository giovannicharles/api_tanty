package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.MobileStockTrackingService;
import com.NTFOODS.Api_tanty.modules.stock.domain.dotation.entity.DotationRequest;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock/mobile-tracking")
public class MobileStockTrackingController {

    private final MobileStockTrackingService mobileStockTrackingService;

    public MobileStockTrackingController(MobileStockTrackingService mobileStockTrackingService) {
        this.mobileStockTrackingService = mobileStockTrackingService;
    }

    @GetMapping("/commercial/{commercialMatricule}")
    public ResponseEntity<MobileStockTrackingService.MobileStockSummary> getCommercialMobileStock(
            @PathVariable String commercialMatricule,
            @RequestParam(required = false) UUID commercialId) {
        
        UserId userId = commercialId != null ? new UserId(commercialId.toString()) : null;
        MobileStockTrackingService.MobileStockSummary summary = 
                mobileStockTrackingService.getCommercialMobileStock(userId, commercialMatricule);
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MobileStockTrackingService.MobileStockSummary>> getAllCommercialsMobileStock() {
        List<MobileStockTrackingService.MobileStockSummary> summaries = 
                mobileStockTrackingService.getAllCommercialsMobileStock();
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/commercial/{commercialMatricule}/dotation-history")
    public ResponseEntity<List<DotationRequest>> getCommercialDotationHistory(@PathVariable String commercialMatricule) {
        List<DotationRequest> history = mobileStockTrackingService.getCommercialDotationHistory(commercialMatricule);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/commercial/{commercialMatricule}/product/{productSku}")
    public ResponseEntity<MobileStockTrackingService.ProductStockDetail> getCommercialProductStockDetail(
            @PathVariable String commercialMatricule,
            @PathVariable String productSku) {
        
        return mobileStockTrackingService.getCommercialProductStockDetail(commercialMatricule, productSku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/commercial/{commercialMatricule}/rotation")
    public ResponseEntity<MobileStockTrackingService.StockRotationMetrics> calculateCommercialStockRotation(
            @PathVariable String commercialMatricule,
            @RequestParam(defaultValue = "30") int days) {
        
        MobileStockTrackingService.StockRotationMetrics metrics = 
                mobileStockTrackingService.calculateCommercialStockRotation(commercialMatricule, days);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/slow-stock")
    public ResponseEntity<List<MobileStockTrackingService.SlowStockCommercial>> identifySlowStockCommercials(
            @RequestParam(defaultValue = "30") int daysThreshold,
            @RequestParam(defaultValue = "10") BigDecimal salesThreshold) {
        
        List<MobileStockTrackingService.SlowStockCommercial> slowStockCommercials = 
                mobileStockTrackingService.identifySlowStockCommercials(daysThreshold, salesThreshold);
        return ResponseEntity.ok(slowStockCommercials);
    }
}
