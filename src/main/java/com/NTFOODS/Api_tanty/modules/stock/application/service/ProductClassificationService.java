package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.product.entity.ProductClassification;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductClassificationJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.repository.ProductClassificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ProductClassificationService - Service pour gérer les classifications de produits
 * Gère la marque, la gamme, la variété et le conditionnement détaillé
 */
@Service
@Transactional
public class ProductClassificationService {
    
    private static final Logger log = LoggerFactory.getLogger(ProductClassificationService.class);
    
    private final ProductClassificationRepository productClassificationRepository;
    
    public ProductClassificationService(ProductClassificationRepository productClassificationRepository) {
        this.productClassificationRepository = productClassificationRepository;
    }
    
    /**
     * Crée une nouvelle classification de produit
     */
    public ProductClassification createClassification(String brand, String range, String variety, 
                                                   String packaging, String packagingDetails,
                                                   Integer quantityPerCarton, String unitWeight,
                                                   String volume, Integer cartonsPerAssortiment) {
        ProductClassificationJpaEntity entity = new ProductClassificationJpaEntity(
                brand, range, variety, packaging, packagingDetails,
                quantityPerCarton, unitWeight, volume, cartonsPerAssortiment
        );
        
        ProductClassificationJpaEntity saved = productClassificationRepository.save(entity);
        log.info("Created product classification: {}", saved.getClassificationCode());
        
        return mapToDomain(saved);
    }
    
    /**
     * Récupère une classification par son code
     */
    public Optional<ProductClassification> getByClassificationCode(String classificationCode) {
        return productClassificationRepository.findByClassificationCode(classificationCode)
                .map(this::mapToDomain);
    }
    
    /**
     * Récupère toutes les classifications d'une marque
     */
    public List<ProductClassification> getByBrand(String brand) {
        return productClassificationRepository.findByBrand(brand).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère toutes les classifications d'une gamme
     */
    public List<ProductClassification> getByRange(String range) {
        return productClassificationRepository.findByRange(range).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère toutes les classifications d'une variété
     */
    public List<ProductClassification> getByVariety(String variety) {
        return productClassificationRepository.findByVariety(variety).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère toutes les classifications d'un type de conditionnement
     */
    public List<ProductClassification> getByPackaging(String packaging) {
        return productClassificationRepository.findByPackaging(packaging).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère toutes les marques disponibles
     */
    public List<String> getAllBrands() {
        return productClassificationRepository.findAllBrands();
    }
    
    /**
     * Récupère les gammes d'une marque
     */
    public List<String> getRangesByBrand(String brand) {
        return productClassificationRepository.findRangesByBrand(brand);
    }
    
    /**
     * Récupère les variétés d'une marque et gamme
     */
    public List<String> getVarietiesByBrandAndRange(String brand, String range) {
        return productClassificationRepository.findVarietiesByBrandAndRange(brand, range);
    }
    
    /**
     * Récupère tous les types de conditionnement
     */
    public List<String> getAllPackagings() {
        return productClassificationRepository.findAllPackagings();
    }
    
    /**
     * Récupère toutes les classifications
     */
    public List<ProductClassification> getAllClassifications() {
        return productClassificationRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * Supprime une classification
     */
    public void deleteClassification(Long classificationId) {
        productClassificationRepository.deleteById(classificationId);
        log.info("Deleted product classification: {}", classificationId);
    }
    
    private ProductClassification mapToDomain(ProductClassificationJpaEntity entity) {
        return new ProductClassification(
                entity.getBrand(),
                entity.getRange(),
                entity.getVariety(),
                entity.getPackaging(),
                entity.getPackagingDetails(),
                entity.getQuantityPerCarton(),
                entity.getUnitWeight(),
                entity.getVolume(),
                entity.getCartonsPerAssortiment()
        );
    }
}
