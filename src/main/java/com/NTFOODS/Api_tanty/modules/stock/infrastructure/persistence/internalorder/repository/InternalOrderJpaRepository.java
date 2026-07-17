package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.internalorder.repository;

import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.internalorder.jpa.InternalOrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternalOrderJpaRepository extends JpaRepository<InternalOrderJpaEntity, Long> {

    List<InternalOrderJpaEntity> findAllByOrderByCreatedAtDesc();

    List<InternalOrderJpaEntity> findByStatusOrderByCreatedAtDesc(String status);

    List<InternalOrderJpaEntity> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
}
