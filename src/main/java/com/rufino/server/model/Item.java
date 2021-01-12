package com.rufino.server.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "items")
@JsonInclude(Include.NON_NULL)
public class Item {

    @Id
    private UUID itemId;

    @NotBlank(message = "Value should not be empty")
    private String itemName;

    @NotNull(message = "Value should not be empty")
    private Double itemPrice;

    @NotNull(message = "Value should not be empty")
    private Integer itemQuantity;

    private String itemDescription;

    @NotNull(message = "Value should not be empty")
    private ZonedDateTime itemCreatedAt;

    private Integer itemCode;

    private Double itemDiscount;

    @NotNull(message = "Invalid order ID format")
    private UUID orderId;
    
    @NotNull(message = "Invalid product ID format")
    private UUID productId; 

    public Item() {
        setItemCreatedAt(ZonedDateTime.now(ZoneId.of("Z")));
        setItemId(UUID.randomUUID());
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public ZonedDateTime getItemCreatedAt() {
        return itemCreatedAt;
    }

    public void setItemCreatedAt(ZonedDateTime itemCreatedAt) {
        this.itemCreatedAt = itemCreatedAt;
    }

    public Integer getItemCode() {
        return itemCode;
    }

    public void setItemCode(Integer itemCode) {
        this.itemCode = itemCode;
    }

    public Double getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(Double itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

}
