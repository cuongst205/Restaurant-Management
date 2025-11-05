package com.example.btljavafx.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int id;
    private LocalDateTime createdAt;
    private List<OrderItem> items;
    private double totalPrice;

    public Order(int id, List<OrderItem> items) {
        this.id = id;
        this.items = items;
        this.createdAt = LocalDateTime.now();
        this.totalPrice = items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) {
        this.items = items;
        this.totalPrice = items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }

    public double getTotalPrice() { return totalPrice; }
}
