package tn.esprit.services;

import tn.esprit.entities.Association;
import tn.esprit.entities.Event;
import tn.esprit.entities.Location;
import tn.esprit.tools.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventService {
    private final Connection cnx;

    private final AssociationService associationService = new AssociationService();
    private final LocationService locationService = new LocationService();

    public EventService() throws SQLException {
        this.cnx = Database.getInstance().getConnection();
    }

    // ✅ CREATE
    public void addEvent(Event e) throws SQLException {
        String sql = "INSERT INTO event (association_id, location_id, name, event_date, price, type, image_filename, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, e.getAssociation().getId());
        pst.setInt(2, e.getLocation().getId());
        pst.setString(3, e.getName());
        pst.setDate(4, Date.valueOf(e.getEventDate()));
        pst.setDouble(5, e.getPrice());
        pst.setString(6, e.getType());
        pst.setString(7, e.getImageFilename());
        pst.setDate(8, Date.valueOf(e.getCreatedAt()));
        pst.setDate(9, Date.valueOf(e.getUpdatedAt()));
        pst.executeUpdate();
    }

    // ✅ READ ALL
    public List<Event> getAllEvents() throws Exception {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Association a = associationService.getAssociationById(rs.getInt("association_id"));
            Location l = locationService.getLocationById(rs.getInt("location_id"));

            Event e = new Event(
                    rs.getInt("id"),
                    a,
                    l,
                    rs.getString("name"),
                    rs.getDate("event_date").toLocalDate(),
                    rs.getDouble("price"),
                    rs.getString("type"),
                    rs.getString("image_filename"),
                    rs.getDate("created_at").toLocalDate(),
                    rs.getDate("updated_at").toLocalDate()
            );

            events.add(e);
        }

        return events;
    }

    // ✅ READ BY ID
    public Event getEventById(int id) throws Exception {
        String sql = "SELECT * FROM event WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Association a = associationService.getAssociationById(rs.getInt("association_id"));
            Location l = locationService.getLocationById(rs.getInt("location_id"));

            return new Event(
                    rs.getInt("id"),
                    a,
                    l,
                    rs.getString("name"),
                    rs.getDate("event_date").toLocalDate(),
                    rs.getDouble("price"),
                    rs.getString("type"),
                    rs.getString("image_filename"),
                    rs.getDate("created_at").toLocalDate(),
                    rs.getDate("updated_at").toLocalDate()
            );
        }

        return null;
    }

    // ✅ UPDATE
    public void updateEvent(Event e) throws SQLException {
        String sql = "UPDATE event SET association_id=?, location_id=?, name=?, event_date=?, price=?, type=?, image_filename=?, updated_at=? WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, e.getAssociation().getId());
        pst.setInt(2, e.getLocation().getId());
        pst.setString(3, e.getName());
        pst.setDate(4, Date.valueOf(e.getEventDate()));
        pst.setDouble(5, e.getPrice());
        pst.setString(6, e.getType());
        pst.setString(7, e.getImageFilename());
        pst.setDate(8, Date.valueOf(LocalDate.now()));
        pst.setInt(9, e.getId());
        pst.executeUpdate();
    }

    // ✅ DELETE
    public void deleteEvent(int id) throws SQLException {
        String sql = "DELETE FROM event WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
    }

    // 🔍 EXTRA: Get events by association
    public List<Event> getEventsByAssociation(int associationId) throws Exception {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event WHERE association_id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, associationId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Event e = getEventById(rs.getInt("id"));
            events.add(e);
        }

        return events;
    }

    // 🔍 EXTRA: Get events by location
    public List<Event> getEventsByLocation(int locationId) throws Exception {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event WHERE location_id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, locationId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Event e = getEventById(rs.getInt("id"));
            events.add(e);
        }

        return events;
    }

    // 🔍 EXTRA: Get future events
    public List<Event> getUpcomingEvents() throws Exception {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event WHERE event_date >= CURDATE() ORDER BY event_date ASC";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Event e = getEventById(rs.getInt("id"));
            events.add(e);
        }

        return events;
    }

    public double getMonthlyGrowthRate() throws SQLException {
        String sql = """
    SELECT 
        /* Current month count */
        COUNT(CASE 
            WHEN event_date >= DATE_FORMAT(NOW(), '%Y-%m-01') 
            AND event_date < DATE_FORMAT(NOW() + INTERVAL 1 MONTH, '%Y-%m-01') 
            THEN 1 
        END) as current_month_count,
        
        /* Previous month count */
        COUNT(CASE 
            WHEN event_date >= DATE_FORMAT(NOW() - INTERVAL 1 MONTH, '%Y-%m-01') 
            AND event_date < DATE_FORMAT(NOW(), '%Y-%m-01') 
            THEN 1 
        END) as previous_month_count
    FROM event
    """;

        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        if (rs.next()) {
            int currentMonthCount = rs.getInt("current_month_count");
            int previousMonthCount = rs.getInt("previous_month_count");

            // Handle division by zero and cases with no previous events
            if (previousMonthCount == 0) {
                return currentMonthCount > 0 ? Double.POSITIVE_INFINITY : 0.0;
            }

            return ((currentMonthCount - previousMonthCount) * 100.0) / previousMonthCount;
        }

        return 0.0;
    }

    public Map<String, Integer> getCategoryDistribution() throws SQLException {
        Map<String, Integer> typeDistribution = new HashMap<>();
        String sql = """
        SELECT 
            type, 
            COUNT(*) as count 
        FROM event 
        WHERE type IS NOT NULL AND type != ''
        GROUP BY type
        ORDER BY count DESC
        """;

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                typeDistribution.put(rs.getString("type"), rs.getInt("count"));
            }
        }

        return typeDistribution;
    }
    // 🔍 EXTRA: Search by type
    public List<Event> searchByType(String type) throws Exception {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event WHERE type LIKE ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, "%" + type + "%");
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Event e = getEventById(rs.getInt("id"));
            events.add(e);
        }

        return events;
    }

}
