package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductLineJpaRepository extends JpaRepository<ProductLineJpaEntity, Long> {
    List<ProductLineJpaEntity> findByBrandId(Long brandId);
    List<ProductLineJpaEntity> findByActiveTrue();
}
