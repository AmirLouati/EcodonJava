package tn.esprit.entities;

public class Location {
    private int id;
    private String name;
    private String country;
    private double latitude;
    private double longitude;
    private double donationAmount;
    private String youtubeEmbedUrl;

    public Location() {}

    public Location(int id, String name, String country, double latitude, double longitude, double donationAmount, String youtubeEmbedUrl) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.donationAmount = donationAmount;
        this.youtubeEmbedUrl = youtubeEmbedUrl;
    }

    public Location(String name, String country, double latitude, double longitude, double donationAmount, String youtubeEmbedUrl) {
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.donationAmount = donationAmount;
        this.youtubeEmbedUrl = youtubeEmbedUrl;
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDonationAmount() {
        return donationAmount;
    }

    public void setDonationAmount(double donationAmount) {
        this.donationAmount = donationAmount;
    }

    public String getYoutubeEmbedUrl() {
        return youtubeEmbedUrl;
    }

    public void setYoutubeEmbedUrl(String youtubeEmbedUrl) {
        this.youtubeEmbedUrl = youtubeEmbedUrl;
    }

    @Override
    public String toString() {
        return name + " (" + country + ")";
    }
}
