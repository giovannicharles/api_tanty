package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.repository;

import com.NTFOODS.Api_tanty.modules.stock.domain.product.aggregate.ProductAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.repository.ProductRepository;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.valueobject.ProductSku;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.mapper.ProductMapper;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductMapper productMapper;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository, ProductMapper productMapper) {
        this.productJpaRepository = productJpaRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Optional<ProductAggregate> findBySku(ProductSku sku) {
        return productJpaRepository.findBySku(sku.getValue()).map(productMapper::toDomain);
    }

    @Override
    public Optional<ProductAggregate> findById(ProductId id) {
        return productJpaRepository.findById(id.getValue()).map(productMapper::toDomain);
    }

    @Override
    public void save(ProductAggregate product) {
        ProductJpaEntity productJpaEntity= productMapper.toJpa(product);
        productJpaRepository.save(productJpaEntity);
    }
}
