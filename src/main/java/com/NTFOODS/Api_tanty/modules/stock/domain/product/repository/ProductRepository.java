package com.NTFOODS.Api_tanty.modules.stock.domain.product.repository;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.aggregate.ProductAggregate;
import com.NTFOODS.Api_tanty.modules.stock.domain.product.valueobject.ProductSku;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.ProductId;

import java.util.Optional;
public interface ProductRepository {
    Optional<ProductAggregate> findBySku(ProductSku sku);
    Optional<ProductAggregate> findById(ProductId id);
    void save(ProductAggregate product);
}