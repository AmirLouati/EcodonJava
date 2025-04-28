package tn.esprit.entities;

import java.time.LocalDate;

public class Event {
    private int id;
    private Association association;
    private Location location;
    private String name;
    private LocalDate eventDate;
    private double price;
    private String type;
    private String imageFilename;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public Event() {}

    public Event(int id, Association association, Location location, String name, LocalDate eventDate, double price, String type, String imageFilename, LocalDate createdAt, LocalDate updatedAt) {
        this.id = id;
        this.association = association;
        this.location = location;
        this.name = name;
        this.eventDate = eventDate;
        this.price = price;
        this.type = type;
        this.imageFilename = imageFilename;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Event(Association association, Location location, String name, LocalDate eventDate, double price, String type, String imageFilename) {
        this.association = association;
        this.location = location;
        this.name = name;
        this.eventDate = eventDate;
        this.price = price;
        this.type = type;
        this.imageFilename = imageFilename;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
