package com.NTFOODS.Api_tanty.modules.stock.domain.common.entity;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId;

import java.time.LocalDateTime;

/**
 * StockLocation - Entité représentant une localisation de stock
 * Peut être: Stock Central, Tampon (Buffer), ou Stock Mobile
 */
public class StockLocation {
    
    private final StockLocationId id;
    private final StockLocationType type;
    private final String name;
    private final String description;
    private UserId assignedUserId; // Pour le stock mobile: ID du commercial assigné
    private String managerId;     // Pour les magasins: matricule du gestionnaire
    private String address;
    private String phone;
    private String email;
    private Boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public StockLocation(StockLocationId id, StockLocationType type, String name, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public StockLocationId getId() {
        return id;
    }
    
    public StockLocationType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public UserId getAssignedUserId() {
        return assignedUserId;
    }
    
    public void setAssignedUserId(UserId assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
