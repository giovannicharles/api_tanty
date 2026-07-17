package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.application.dto.ProductCatalogResponse;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * ProductCatalogService - Lecture du catalogue produit (Marque -> Gamme -> Variante -> Produit).
 * Alimente notamment le sélecteur de produits du formulaire de Réception, qui n'avait
 * jusqu'ici aucune source de données réelle : le seul contrôleur existant sur ce chemin
 * (/api/v1/stock/materiels) concerne en réalité les équipements/matériel, pas le
 * catalogue produit, et /api/v1/stock/products (ProductController) était une classe
 * vide sans aucun endpoint.
 */
@Service
@RequiredArgsConstructor
public class ProductCatalogService {

    private final ProductJpaRepository productRepository;
    private final ProductVariantJpaRepository variantRepository;
    private final ProductLineJpaRepository lineRepository;
    private final BrandJpaRepository brandRepository;

    @Cacheable(value = "productCatalog", key = "'all'")
    public List<ProductCatalogResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ProductCatalogResponse> getAll(String materialType) {
        if (materialType == null || materialType.isBlank()) return getAll();
        return productRepository.findAll().stream()
                .filter(p -> materialType.equalsIgnoreCase(effectiveMaterialType(p)))
                .map(this::toResponse)
                .toList();
    }

    /**
     * Les 40 produits historiques ont été créés avant l'ajout de la colonne
     * material_type (migration ddl-auto=update) : leur valeur est donc NULL en
     * base tant que le script SQL de rattrapage n'a pas été exécuté. On les
     * traite par défaut comme PRODUIT_FINI (seul type qui existait avant),
     * pour que le filtre reste utilisable même sans migration manuelle.
     */
    private String effectiveMaterialType(ProductJpaEntity p) {
        String mt = p.getMaterialType();
        return (mt == null || mt.isBlank()) ? "PRODUIT_FINI" : mt;
    }

    @Cacheable(value = "productCatalog", key = "#id")
    public Optional<ProductCatalogResponse> getById(Long id) {
        return productRepository.findById(id).map(this::toResponse);
    }

    /** Récupération en lot, indexée par id produit - utilisée pour enrichir les listes de stock items. */
    public java.util.Map<Long, ProductCatalogResponse> getByIds(java.util.Collection<Long> ids) {
        return productRepository.findAllById(ids).stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toMap(ProductCatalogResponse::getId, r -> r));
    }

    private ProductCatalogResponse toResponse(ProductJpaEntity p) {
        ProductCatalogResponse r = new ProductCatalogResponse();
        r.setId(p.getId());
        r.setSku(p.getSku());
        r.setBarcode(p.getBarcode());
        r.setCategory(p.getCategory());
        r.setUnit(p.getUnit());
        r.setActive(p.isActive());
        r.setPackagingType(p.getPackagingType());
        r.setQuantityPerCarton(p.getQuantityPerCarton());
        r.setVolume(p.getVolume());
        r.setMaterialType(effectiveMaterialType(p));

        String variantName = null, lineName = null, brandName = null;
        if (p.getVariantId() != null) {
            ProductVariantJpaEntity variant = variantRepository.findById(p.getVariantId()).orElse(null);
            if (variant != null) {
                variantName = variant.getName();
                ProductLineJpaEntity line = lineRepository.findById(variant.getProductLineId()).orElse(null);
                if (line != null) {
                    lineName = line.getName();
                    BrandJpaEntity brand = brandRepository.findById(line.getBrandId()).orElse(null);
                    if (brand != null) brandName = brand.getName();
                }
            }
        }
        r.setVariantName(variantName);
        r.setProductLineName(lineName);
        r.setBrandName(brandName);
        r.setDesignation(buildDesignation(brandName, lineName, variantName, p));
        return r;
    }

    private String buildDesignation(String brandName, String lineName, String variantName, ProductJpaEntity p) {
        StringBuilder sb = new StringBuilder();
        if (brandName != null) sb.append(brandName).append(' ');
        if (lineName != null) sb.append(lineName).append(' ');
        if (variantName != null) sb.append(variantName);
        String designation = sb.toString().trim();
        if (!designation.isEmpty()) return designation;
        // Matières premières / consommables : pas de hiérarchie Marque/Gamme/Variante,
        // `category` porte directement le nom lisible (ex: "Soja décortiqué").
        if (p.getCategory() != null && !p.getCategory().isBlank()) return p.getCategory();
        return p.getSku();
    }
}
