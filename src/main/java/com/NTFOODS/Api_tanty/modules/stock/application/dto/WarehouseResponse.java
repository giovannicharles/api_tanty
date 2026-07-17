package com.NTFOODS.Api_tanty.modules.stock.application.dto;

public class WarehouseResponse {
    private Long id;
    private String name;
    private String type;
    private boolean isBuffer;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isBuffer() { return isBuffer; }
    public void setBuffer(boolean buffer) { isBuffer = buffer; }
}
