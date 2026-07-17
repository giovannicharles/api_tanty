package com.NTFOODS.Api_tanty.modules.stock.infrastructure.web;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.ProductCatalogResponse;
import com.NTFOODS.Api_tanty.modules.stock.application.service.ProductCatalogService;
import com.NTFOODS.Api_tanty.shared.kernel.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController - Catalogue produit en lecture (Marque/Gamme/Variante/Produit).
 * Remplace un stub vide (aucun endpoint) qui empêchait le frontend de charger la
 * moindre liste de produits, bloquant de fait la création de toute réception.
 */
@RestController
@RequestMapping("/api/v1/stock/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductCatalogService catalogService;

    @GetMapping
    public ResponseEntity<List<ProductCatalogResponse>> getAll(
            @RequestParam(required = false) String materialType) {
        return ResponseEntity.ok(catalogService.getAll(materialType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCatalogResponse> getById(@PathVariable Long id) {
        return catalogService.getById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable : " + id));
    }
}
