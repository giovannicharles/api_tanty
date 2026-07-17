package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.service.StockLocationService;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockLocation;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stock/locations")
public class StockLocationController {

    private final StockLocationService stockLocationService;

    public StockLocationController(StockLocationService stockLocationService) {
        this.stockLocationService = stockLocationService;
    }

    @PostMapping
    public ResponseEntity<StockLocationId> createStockLocation(@RequestBody CreateLocationRequest request) {
        StockLocationId locationId = stockLocationService.createLocation(
            request.type, 
            request.name, 
            request.description != null ? request.description : ""
        );
        return ResponseEntity.ok(locationId);
    }

    public static class CreateLocationRequest {
        public StockLocationType type;
        public String name;
        public String description;
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLocationResponse> getStockLocation(@PathVariable UUID id) {
        StockLocationId locationId = new StockLocationId(id);
        return stockLocationService.getLocationById(locationId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLocationResponse>> getAllStockLocations() {
        List<StockLocation> centralLocations = stockLocationService.getLocationsByType(StockLocationType.STOCK_CENTRAL);
        List<StockLocation> bufferLocations = stockLocationService.getLocationsByType(StockLocationType.STOCK_BUFFER);
        List<StockLocation> mobileLocations = stockLocationService.getLocationsByType(StockLocationType.STOCK_MOBILE);
        List<StockLocation> magasins = stockLocationService.getAllMagasins();

        List<StockLocation> allLocations = new java.util.ArrayList<>();
        allLocations.addAll(centralLocations);
        allLocations.addAll(bufferLocations);
        allLocations.addAll(mobileLocations);
        allLocations.addAll(magasins);
        return ResponseEntity.ok(allLocations.stream().map(this::toResponse).toList());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLocationResponse>> getStockLocationsByType(@PathVariable StockLocationType type) {
        List<StockLocation> locations = stockLocationService.getLocationsByType(type);
        return ResponseEntity.ok(locations.stream().map(this::toResponse).toList());
    }

    private com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLocationResponse toResponse(StockLocation location) {
        int itemCount = 0;
        try {
            itemCount = stockLocationService.getMagasinItemCount(location.getId());
        } catch (Exception ignored) {}
        return com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLocationResponse.ofDetailed(
                location.getId().value().toString(),
                location.getType().name(),
                location.getName(),
                location.getDescription(),
                location.getManagerId(),
                location.getAddress(),
                location.getPhone(),
                location.getEmail(),
                location.getActive(),
                itemCount
        );
    }

    @GetMapping("/commercial/{commercialId}")
    public ResponseEntity<StockLocation> getMobileLocationByCommercial(@PathVariable UUID commercialId) {
        UserId userId = new UserId(commercialId.toString());
        return stockLocationService.getLocationByUser(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Void> assignCommercialToMobileLocation(@PathVariable UUID id, @RequestParam UUID commercialId) {
        StockLocationId locationId = new StockLocationId(id);
        UserId userId = new UserId(commercialId.toString());
        stockLocationService.assignLocationToUser(locationId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/initialize")
    public ResponseEntity<Void> initializeDefaultLocations() {
        stockLocationService.initializeDefaultLocations();
        return ResponseEntity.ok().build();
    }

    // ═══ MAGASIN ENDPOINTS ═════════════════════════════════════

    @GetMapping("/magasins")
    public ResponseEntity<List<com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLocationResponse>> getAllMagasins() {
        List<StockLocation> magasins = stockLocationService.getAllMagasins();
        return ResponseEntity.ok(magasins.stream().map(this::toResponse).toList());
    }

    @GetMapping("/magasins/active")
    public ResponseEntity<List<com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLocationResponse>> getActiveMagasins() {
        List<StockLocation> magasins = stockLocationService.getActiveMagasins();
        return ResponseEntity.ok(magasins.stream().map(this::toResponse).toList());
    }

    @GetMapping("/magasins/manager/{managerMatricule}")
    public ResponseEntity<List<com.NTFOODS.Api_tanty.modules.stock.application.dto.StockLocationResponse>> getMagasinsByManager(@PathVariable String managerMatricule) {
        List<StockLocation> magasins = stockLocationService.getMagasinsByManager(managerMatricule);
        return ResponseEntity.ok(magasins.stream().map(this::toResponse).toList());
    }

    @PostMapping("/magasins")
    public ResponseEntity<StockLocationId> createMagasin(@RequestBody CreateMagasinRequest request) {
        StockLocationId locationId = stockLocationService.createMagasin(
            request.name,
            request.description != null ? request.description : "",
            request.managerId,
            request.address,
            request.phone,
            request.email
        );
        return ResponseEntity.ok(locationId);
    }

    @PutMapping("/magasins/{id}")
    public ResponseEntity<Void> updateMagasin(@PathVariable UUID id, @RequestBody UpdateMagasinRequest request) {
        StockLocationId locationId = new StockLocationId(id);
        stockLocationService.updateMagasin(
            locationId,
            request.name,
            request.description,
            request.managerId,
            request.address,
            request.phone,
            request.email,
            request.active
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/magasins/{id}")
    public ResponseEntity<Void> deactivateMagasin(@PathVariable UUID id) {
        StockLocationId locationId = new StockLocationId(id);
        stockLocationService.deactivateMagasin(locationId);
        return ResponseEntity.ok().build();
    }

    public static class CreateMagasinRequest {
        public String name;
        public String description;
        public String managerId;
        public String address;
        public String phone;
        public String email;
    }

    public static class UpdateMagasinRequest {
        public String name;
        public String description;
        public String managerId;
        public String address;
        public String phone;
        public String email;
        public Boolean active;
    }
}
