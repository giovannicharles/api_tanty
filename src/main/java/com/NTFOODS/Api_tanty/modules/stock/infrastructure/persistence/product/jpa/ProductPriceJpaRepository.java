package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductPriceJpaRepository extends JpaRepository<ProductPriceJpaEntity, Long> {
    List<ProductPriceJpaEntity> findByProductId(Long productId);
    List<ProductPriceJpaEntity> findByProductIdAndActiveTrue(Long productId);
    Optional<ProductPriceJpaEntity> findByProductIdAndPriceTypeAndActiveTrue(Long productId, String priceType);
}
