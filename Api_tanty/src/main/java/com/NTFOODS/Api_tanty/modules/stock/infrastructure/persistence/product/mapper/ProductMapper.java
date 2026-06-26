package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.mapper;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.Money;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.aggregate.ProductAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.entity.Brand;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.entity.ProductLine;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.entity.ProductVariant;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.enums.Category;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.enums.UnitType;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.repository.ProductRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.valueobject.ProductSku;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.*;

import org.springframework.stereotype.Component;

/**
 * ProductMapper - Mapper pour convertir entre ProductJpaEntity et ProductAggregate
 * Gère la conversion entre l'entité JPA et l'agrégat de domaine
 */
@Component
public class ProductMapper {
private final BrandJpaRepository brandJpaRepository;
private final ProductLineJpaRepository productLineJpaRepository;
private final ProductVariantJpaRepository productVariantJpaRepository;

    /**
     * Constructeur avec injection des repositories JPA
     * @param brandJpaRepository Repository pour les marques
     * @param productLineJpaRepository Repository pour les lignes de produits
     * @param productVariantJpaRepository Repository pour les variantes de produits
     */
    public ProductMapper(BrandJpaRepository brandJpaRepository, ProductLineJpaRepository productLineJpaRepository, ProductVariantJpaRepository productVariantJpaRepository) {
        this.brandJpaRepository = brandJpaRepository;
        this.productLineJpaRepository = productLineJpaRepository;
        this.productVariantJpaRepository = productVariantJpaRepository;
    }

    /**
     * Convertit ProductAggregate en ProductJpaEntity
     * @param productAggregate Agrégat de domaine
     * @return Entité JPA
     */
    public ProductJpaEntity toJpa(ProductAggregate productAggregate){
        ProductJpaEntity productJpaEntity= new ProductJpaEntity();
        productJpaEntity.setSku(productAggregate.getSku().getValue());
        productJpaEntity.setVariantId(productAggregate.getVariant().getId());
        productJpaEntity.setBarcode("");

        return productJpaEntity;

    }

    /**
     * Convertit ProductJpaEntity en ProductAggregate
     * @param productJpaEntity Entité JPA
     * @return Agrégat de domaine
     */
    public ProductAggregate toDomain(ProductJpaEntity productJpaEntity){
        ProductVariantJpaEntity productVariantJpaEntity = productVariantJpaRepository.findById(productJpaEntity.getId()).orElseThrow();
        ProductLineJpaEntity productLineJpaEntity= productLineJpaRepository.findById(productVariantJpaEntity.getProductLineId()).orElseThrow();
        BrandJpaEntity brandJpaEntity= brandJpaRepository.findById(productLineJpaEntity.getId()).orElseThrow();

        Brand brand= new Brand(brandJpaEntity.getId(), brandJpaEntity.getName(), brandJpaEntity.getCode());
        ProductLine productLine= new ProductLine(productLineJpaEntity.getId(), brand, productLineJpaEntity.getName(),productLineJpaEntity.getCode());
        ProductVariant productVariant= new ProductVariant(productLine.getId(),productLine,productVariantJpaEntity.getName(),productVariantJpaEntity.getCode());


        return new ProductAggregate(
                new ProductSku(productJpaEntity.getSku()), productVariant, productJpaEntity.getBarcode(), Category.valueOf(productJpaEntity.getCategory()), UnitType.valueOf(productJpaEntity.getUnit()), new Money(productJpaEntity.getUnitPriceAmount()),productJpaEntity.getLeadTimeDays(), productJpaEntity.getSafetyStockDays())
        ;
    }
}
