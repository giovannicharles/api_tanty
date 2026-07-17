package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.ProductClassificationService;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.entity.ProductClassification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock/product-classifications")
public class ProductClassificationController {

    private final ProductClassificationService productClassificationService;

    public ProductClassificationController(ProductClassificationService productClassificationService) {
        this.productClassificationService = productClassificationService;
    }

    @PostMapping
    public ResponseEntity<ProductClassification> createClassification(@RequestBody CreateClassificationRequest request) {
        ProductClassification classification = productClassificationService.createClassification(
                request.brand, request.range, request.variety, request.packaging, request.packagingDetails,
                request.quantityPerCarton, request.unitWeight, request.volume, request.cartonsPerAssortiment);

        return ResponseEntity.ok(classification);
    }

    public static class CreateClassificationRequest {
        public String brand;
        public String range;
        public String variety;
        public String packaging;
        public String packagingDetails;
        public Integer quantityPerCarton;
        public String unitWeight;
        public String volume;
        public Integer cartonsPerAssortiment;
    }

    @GetMapping("/code/{classificationCode}")
    public ResponseEntity<ProductClassification> getByClassificationCode(@PathVariable String classificationCode) {
        return productClassificationService.getByClassificationCode(classificationCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductClassification>> getByBrand(@PathVariable String brand) {
        List<ProductClassification> classifications = productClassificationService.getByBrand(brand);
        return ResponseEntity.ok(classifications);
    }

    @GetMapping("/range/{range}")
    public ResponseEntity<List<ProductClassification>> getByRange(@PathVariable String range) {
        List<ProductClassification> classifications = productClassificationService.getByRange(range);
        return ResponseEntity.ok(classifications);
    }

    @GetMapping("/variety/{variety}")
    public ResponseEntity<List<ProductClassification>> getByVariety(@PathVariable String variety) {
        List<ProductClassification> classifications = productClassificationService.getByVariety(variety);
        return ResponseEntity.ok(classifications);
    }

    @GetMapping("/packaging/{packaging}")
    public ResponseEntity<List<ProductClassification>> getByPackaging(@PathVariable String packaging) {
        List<ProductClassification> classifications = productClassificationService.getByPackaging(packaging);
        return ResponseEntity.ok(classifications);
    }

    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrands() {
        List<String> brands = productClassificationService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/brand/{brand}/ranges")
    public ResponseEntity<List<String>> getRangesByBrand(@PathVariable String brand) {
        List<String> ranges = productClassificationService.getRangesByBrand(brand);
        return ResponseEntity.ok(ranges);
    }

    @GetMapping("/brand/{brand}/range/{range}/varieties")
    public ResponseEntity<List<String>> getVarietiesByBrandAndRange(
            @PathVariable String brand,
            @PathVariable String range) {
        
        List<String> varieties = productClassificationService.getVarietiesByBrandAndRange(brand, range);
        return ResponseEntity.ok(varieties);
    }

    @GetMapping("/packagings")
    public ResponseEntity<List<String>> getAllPackagings() {
        List<String> packagings = productClassificationService.getAllPackagings();
        return ResponseEntity.ok(packagings);
    }

    @GetMapping
    public ResponseEntity<List<ProductClassification>> getAllClassifications() {
        List<ProductClassification> classifications = productClassificationService.getAllClassifications();
        return ResponseEntity.ok(classifications);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassification(@PathVariable Long id) {
        productClassificationService.deleteClassification(id);
        return ResponseEntity.noContent().build();
    }
}
