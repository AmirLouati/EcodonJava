package tn.esprit.entities;

import java.time.LocalDateTime;

public class Donation {
    private int id;
    private int eventId;
    private int userId;

    private String firstName;
    private String lastName;
    private String country;
    private String address;
    private String apartment;
    private String town;
    private String state;
    private String postalCode;
    private String phone;
    private String email;
    private String orderNotes;
    private double donationAmount;
    private String donationType;
    private String paymentMethod;
    private LocalDateTime createdAt;

    // Constructors
    public Donation() {}

    public Donation(int eventId, int userId, String firstName, String lastName, String country, String address,
                    String apartment, String town, String state, String postalCode, String phone, String email,
                    String orderNotes, double donationAmount, String donationType, String paymentMethod, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.address = address;
        this.apartment = apartment;
        this.town = town;
        this.state = state;
        this.postalCode = postalCode;
        this.phone = phone;
        this.email = email;
        this.orderNotes = orderNotes;
        this.donationAmount = donationAmount;
        this.donationType = donationType;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrderNotes() {
        return orderNotes;
    }

    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }

    public double getDonationAmount() {
        return donationAmount;
    }

    public void setDonationAmount(double donationAmount) {
        this.donationAmount = donationAmount;
    }

    public String getDonationType() {
        return donationType;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
