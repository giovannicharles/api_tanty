package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.repository;

import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductClassificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProductClassificationRepository - Repository pour ProductClassificationJpaEntity
 */
@Repository
public interface ProductClassificationRepository extends JpaRepository<ProductClassificationJpaEntity, Long> {
    
    Optional<ProductClassificationJpaEntity> findByClassificationCode(String classificationCode);
    
    List<ProductClassificationJpaEntity> findByBrand(String brand);
    
    List<ProductClassificationJpaEntity> findByRange(String range);
    
    List<ProductClassificationJpaEntity> findByVariety(String variety);
    
    List<ProductClassificationJpaEntity> findByPackaging(String packaging);
    
    @Query("SELECT pc FROM ProductClassificationJpaEntity pc WHERE pc.brand = :brand AND pc.range = :range")
    List<ProductClassificationJpaEntity> findByBrandAndRange(@Param("brand") String brand, @Param("range") String range);
    
    @Query("SELECT pc FROM ProductClassificationJpaEntity pc WHERE pc.brand = :brand AND pc.range = :range AND pc.variety = :variety")
    List<ProductClassificationJpaEntity> findByBrandRangeAndVariety(@Param("brand") String brand, 
                                                                   @Param("range") String range, 
                                                                   @Param("variety") String variety);
    
    @Query("SELECT DISTINCT pc.brand FROM ProductClassificationJpaEntity pc ORDER BY pc.brand")
    List<String> findAllBrands();
    
    @Query("SELECT DISTINCT pc.range FROM ProductClassificationJpaEntity pc WHERE pc.brand = :brand ORDER BY pc.range")
    List<String> findRangesByBrand(@Param("brand") String brand);
    
    @Query("SELECT DISTINCT pc.variety FROM ProductClassificationJpaEntity pc WHERE pc.brand = :brand AND pc.range = :range ORDER BY pc.variety")
    List<String> findVarietiesByBrandAndRange(@Param("brand") String brand, @Param("range") String range);
    
    @Query("SELECT DISTINCT pc.packaging FROM ProductClassificationJpaEntity pc ORDER BY pc.packaging")
    List<String> findAllPackagings();
}
