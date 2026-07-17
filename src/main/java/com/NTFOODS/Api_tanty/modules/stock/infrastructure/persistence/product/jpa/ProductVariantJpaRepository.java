package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantJpaRepository extends JpaRepository<ProductVariantJpaEntity, Long> {
    List<ProductVariantJpaEntity> findByProductLineId(Long productLineId);
}
