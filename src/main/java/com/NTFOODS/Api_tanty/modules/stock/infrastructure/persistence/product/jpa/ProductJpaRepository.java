package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
    Optional<ProductJpaEntity> findBySku(String sku);
    List<ProductJpaEntity> findByVariantId(Long variantId);
    List<ProductJpaEntity> findByActiveTrue();
    List<ProductJpaEntity> findByMaterialType(String materialType);
}