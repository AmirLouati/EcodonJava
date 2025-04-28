package tn.esprit.services;

import tn.esprit.entities.Location;
import tn.esprit.tools.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationService {

    private final Connection cnx;

    public LocationService() throws SQLException {
        this.cnx = Database.getInstance().getConnection();
    }

    // ✅ CREATE
    public void addLocation(Location l) throws SQLException {
        String sql = "INSERT INTO location (name, country, latitude, longitude, donation_amount, youtube_embed_url) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, l.getName());
        pst.setString(2, l.getCountry());
        pst.setDouble(3, l.getLatitude());
        pst.setDouble(4, l.getLongitude());
        pst.setDouble(5, l.getDonationAmount());
        pst.setString(6, l.getYoutubeEmbedUrl());
        pst.executeUpdate();
    }

    // ✅ READ ALL
    public List<Location> getAllLocations() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM location";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Location l = new Location(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("country"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getDouble("donation_amount"),
                    rs.getString("youtube_embed_url")
            );
            locations.add(l);
        }

        return locations;
    }

    // ✅ READ BY ID
    public Location getLocationById(int id) throws SQLException {
        String sql = "SELECT * FROM location WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return new Location(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("country"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getDouble("donation_amount"),
                    rs.getString("youtube_embed_url")
            );
        }

        return null;
    }

    // ✅ UPDATE
    public void updateLocation(Location l) throws SQLException {
        String sql = "UPDATE location SET name=?, country=?, latitude=?, longitude=?, donation_amount=?, youtube_embed_url=? WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, l.getName());
        pst.setString(2, l.getCountry());
        pst.setDouble(3, l.getLatitude());
        pst.setDouble(4, l.getLongitude());
        pst.setDouble(5, l.getDonationAmount());
        pst.setString(6, l.getYoutubeEmbedUrl());
        pst.setInt(7, l.getId());
        pst.executeUpdate();
    }

    // ✅ DELETE
    public void deleteLocation(int id) throws SQLException {
        String sql = "DELETE FROM location WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
    }
}
