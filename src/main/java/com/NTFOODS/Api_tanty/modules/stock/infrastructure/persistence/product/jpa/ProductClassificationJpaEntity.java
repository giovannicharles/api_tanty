package com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa;

import jakarta.persistence.*;

/**
 * ProductClassificationJpaEntity - Entité JPA pour ProductClassification
 */
@Entity
@Table(name = "product_classifications")
public class ProductClassificationJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "brand", nullable = false)
    private String brand;
    
    @Column(name = "range", nullable = false)
    private String range;
    
    @Column(name = "variety", nullable = false)
    private String variety;
    
    @Column(name = "packaging", nullable = false)
    private String packaging;
    
    @Column(name = "packaging_details", nullable = false)
    private String packagingDetails;
    
    @Column(name = "quantity_per_carton")
    private Integer quantityPerCarton;
    
    @Column(name = "unit_weight")
    private String unitWeight;
    
    @Column(name = "volume")
    private String volume;
    
    @Column(name = "cartons_per_assortiment")
    private Integer cartonsPerAssortiment;
    
    @Column(name = "classification_code", unique = true, nullable = false)
    private String classificationCode;
    
    public ProductClassificationJpaEntity() {}
    
    public ProductClassificationJpaEntity(String brand, String range, String variety, String packaging,
                                       String packagingDetails, Integer quantityPerCarton,
                                       String unitWeight, String volume, Integer cartonsPerAssortiment) {
        this.brand = brand;
        this.range = range;
        this.variety = variety;
        this.packaging = packaging;
        this.packagingDetails = packagingDetails;
        this.quantityPerCarton = quantityPerCarton;
        this.unitWeight = unitWeight;
        this.volume = volume;
        this.cartonsPerAssortiment = cartonsPerAssortiment;
        this.classificationCode = generateClassificationCode();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }
    
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    
    public String getPackaging() { return packaging; }
    public void setPackaging(String packaging) { this.packaging = packaging; }
    
    public String getPackagingDetails() { return packagingDetails; }
    public void setPackagingDetails(String packagingDetails) { this.packagingDetails = packagingDetails; }
    
    public Integer getQuantityPerCarton() { return quantityPerCarton; }
    public void setQuantityPerCarton(Integer quantityPerCarton) { this.quantityPerCarton = quantityPerCarton; }
    
    public String getUnitWeight() { return unitWeight; }
    public void setUnitWeight(String unitWeight) { this.unitWeight = unitWeight; }
    
    public String getVolume() { return volume; }
    public void setVolume(String volume) { this.volume = volume; }
    
    public Integer getCartonsPerAssortiment() { return cartonsPerAssortiment; }
    public void setCartonsPerAssortiment(Integer cartonsPerAssortiment) { this.cartonsPerAssortiment = cartonsPerAssortiment; }
    
    public String getClassificationCode() { return classificationCode; }
    public void setClassificationCode(String classificationCode) { this.classificationCode = classificationCode; }
    
    private String generateClassificationCode() {
        return String.format("%s-%s-%s-%s", 
                brand.toUpperCase(), 
                range.toUpperCase(), 
                variety.toUpperCase(), 
                packaging.toUpperCase());
    }
}
