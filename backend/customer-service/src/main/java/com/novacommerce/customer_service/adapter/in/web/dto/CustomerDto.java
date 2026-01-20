package com.novacommerce.customer_service.adapter.in.web.dto;

import com.novacommerce.customer_service.domain.model.enums.CustomerStatus;
import com.novacommerce.customer_service.domain.model.enums.LoyaltyLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerDto {
    private String id;
    @NotBlank
    @Size(max = 100)
    private String firstName;
    @NotBlank
    @Size(max = 100)
    private String lastName;
    @Email
    @NotBlank
    private String email;
    @Size(max = 20)
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
