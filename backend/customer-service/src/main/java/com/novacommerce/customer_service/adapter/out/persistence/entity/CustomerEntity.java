package com.novacommerce.customer_service.adapter.out.persistence.entity;

import com.novacommerce.customer_service.domain.model.enums.CustomerStatus;
import com.novacommerce.customer_service.domain.model.enums.LoyaltyLevel;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document(collection = "customers")
public class CustomerEntity {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private CustomerStatus status;
    private LoyaltyLevel loyaltyLevel;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public CustomerStatus getStatus() { return status; }
    public void setStatus(CustomerStatus status) { this.status = status; }
    public LoyaltyLevel getLoyaltyLevel() { return loyaltyLevel; }
    public void setLoyaltyLevel(LoyaltyLevel loyaltyLevel) { this.loyaltyLevel = loyaltyLevel; }
}
