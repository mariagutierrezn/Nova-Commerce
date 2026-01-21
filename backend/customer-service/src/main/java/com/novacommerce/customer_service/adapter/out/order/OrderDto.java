package com.novacommerce.customer_service.adapter.out.order;

import java.time.LocalDateTime;

public class OrderDto {
    private String id;
    private Double totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private String customerId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
}
