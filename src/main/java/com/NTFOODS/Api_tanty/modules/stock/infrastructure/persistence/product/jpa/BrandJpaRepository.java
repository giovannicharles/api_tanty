package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandJpaRepository  extends JpaRepository<BrandJpaEntity, Long> {
    List<BrandJpaEntity> findByActiveTrue();
}
