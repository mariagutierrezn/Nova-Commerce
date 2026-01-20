package com.novacommerce.customer_service.domain.model;

public class Address {
    private String id;
    private String customerId;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    public Address() {}

    public Address(String id, String customerId, String street, String city, String state, String zipCode, String country) {
        this.id = id;
        this.customerId = customerId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
