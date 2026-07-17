package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.productionbatch.repository;

import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.productionbatch.jpa.ProductionBatchJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionBatchRepository extends JpaRepository<ProductionBatchJpaEntity, Long> {

    List<ProductionBatchJpaEntity> findByStatusOrderByCreatedAtDesc(String status);

    List<ProductionBatchJpaEntity> findAllByOrderByCreatedAtDesc();
}
