package com.NTFOODS.Api_tanty.modules.stock.application.dto;

public class StockLocationResponse {
    private String id;
    private String type;
    private String typeLabel;
    private String name;
    private String description;
    private String managerId;
    private String address;
    private String phone;
    private String email;
    private Boolean active;
    private int itemCount;

    public static StockLocationResponse of(String id, String type, String name, String description) {
        StockLocationResponse r = new StockLocationResponse();
        r.id = id;
        r.type = type;
        r.typeLabel = switch (type) {
            case "STOCK_CENTRAL" -> "Stock Central";
            case "STOCK_BUFFER" -> "Stock Tampon";
            case "STOCK_MOBILE" -> "Stock Mobile";
            case "MAGASIN" -> "Magasin";
            default -> type;
        };
        r.name = name;
        r.description = description;
        r.active = true;
        return r;
    }

    public static StockLocationResponse ofDetailed(String id, String type, String name, String description,
                                                     String managerId, String address, String phone,
                                                     String email, Boolean active, int itemCount) {
        StockLocationResponse r = of(id, type, name, description);
        r.managerId = managerId;
        r.address = address;
        r.phone = phone;
        r.email = email;
        r.active = active;
        r.itemCount = itemCount;
        return r;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTypeLabel() { return typeLabel; }
    public void setTypeLabel(String typeLabel) { this.typeLabel = typeLabel; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
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
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
}
