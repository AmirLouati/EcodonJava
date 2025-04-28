package tn.esprit.entities;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Association {
    private int id;
    private User user;
    private String Name;
    private String Logo;
    private String Description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) { // Fixed: Parameter name matches usage
        this.Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getLogo() { // Renamed from "getLog" for clarity
        return Logo;
    }

    public void setLogo(String logo) {
        this.Logo = logo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Association() {}

    public Association(String Name, User user, String Description, String Logo) {
        this.Name = Name;
        this.user = user;
        this.Description = Description;
        this.Logo = Logo;
    }

    public Association(int id, User user, String name, String logo, String description) {
        this.id = id;
        this.user = user;
        Name = name;
        Logo = logo;
        Description = description;
    }
    private IntegerProperty eventCount = new SimpleIntegerProperty();
    private IntegerProperty memberCount = new SimpleIntegerProperty();

    public int getEventCount() {
        return eventCount.get();
    }

    public IntegerProperty eventCountProperty() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount.set(eventCount);
    }

    public int getMemberCount() {
        return memberCount.get();
    }

    public IntegerProperty memberCountProperty() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount.set(memberCount);
    }
    @Override
    public String toString() {
        return "Association{" +
                "id=" + id +
                ", user=" + user +
                ", Name='" + Name + '\'' +
                ", Logo='" + Logo + '\'' +
                ", Description='" + Description + '\'' +
                '}';
    }
}