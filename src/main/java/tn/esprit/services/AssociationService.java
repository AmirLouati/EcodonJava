package tn.esprit.services;

import tn.esprit.entities.Association;
import tn.esprit.entities.User;
import tn.esprit.tools.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssociationService {
    private final Connection cnx;
    UserService userService = new UserService();
    public AssociationService() throws SQLException {
        cnx = Database.getInstance().getConnection();
    }

    // ✅ CREATE
    public void addAssociation(Association a) throws SQLException {
        String sql = "INSERT INTO association (name, logo, description, user_id) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, a.getName());
        pst.setString(2, a.getLogo());
        pst.setString(3, a.getDescription());
        pst.setInt(4, a.getUser().getId());
        pst.executeUpdate();
    }

    // ✅ READ ALL
    public List<Association> getAllAssociations() throws Exception {
        List<Association> associations = new ArrayList<>();
        String sql = "SELECT * FROM association";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int userId = rs.getInt("user_id");
            User user = userService.getUserById(userId);

            Association a = new Association(
                    rs.getInt("id"),
                    user,
                    rs.getString("name"),
                    rs.getString("logo"),
                    rs.getString("description")
            );
            associations.add(a);
        }
        return associations;
    }

    // ✅ READ BY ID
    public Association getAssociationById(int id) throws Exception {
        String sql = "SELECT * FROM association WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            User user = userService.getUserById(rs.getInt("user_id"));
            return new Association(
                    rs.getInt("id"),
                    user,
                    rs.getString("name"),
                    rs.getString("logo"),
                    rs.getString("description")
            );
        }
        return null;
    }

    // ✅ UPDATE
    public void updateAssociation(Association a) throws SQLException {
        String sql = "UPDATE association SET name=?, logo=?, description=?, user_id=? WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, a.getName());
        pst.setString(2, a.getLogo());
        pst.setString(3, a.getDescription());
        pst.setInt(4, a.getUser().getId());
        pst.setInt(5, a.getId());
        pst.executeUpdate();
    }

    // ✅ DELETE
    public void deleteAssociation(int id) throws SQLException {
        String sql = "DELETE FROM association WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
    }

    // ✅ READ BY USER
    public List<Association> getAssociationsByUser(int userId) throws Exception {
        List<Association> associations = new ArrayList<>();
        String sql = "SELECT * FROM association WHERE user_id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            User user = userService.getUserById(userId);
            Association a = new Association(
                    rs.getInt("id"),
                    user,
                    rs.getString("name"),
                    rs.getString("logo"),
                    rs.getString("description")
            );
            associations.add(a);
        }
        return associations;
    }

    public Association getAssociationByName(String name) throws Exception {
        String sql = "SELECT * FROM association WHERE name = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, name);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            User user = userService.getUserById(rs.getInt("user_id"));
            return new Association(
                    rs.getInt("id"),
                    user,
                    rs.getString("name"),
                    rs.getString("logo"),
                    rs.getString("description")
            );
        }

        return null;
    }

    public List<Association> searchAssociations(String keyword) throws Exception {
        List<Association> associations = new ArrayList<>();
        String sql = "SELECT * FROM association WHERE name LIKE ? OR description LIKE ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        String pattern = "%" + keyword + "%";
        pst.setString(1, pattern);
        pst.setString(2, pattern);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            User user = userService.getUserById(rs.getInt("user_id"));
            Association a = new Association(
                    rs.getInt("id"),
                    user,
                    rs.getString("name"),
                    rs.getString("logo"),
                    rs.getString("description")
            );
            associations.add(a);
        }

        return associations;
    }

    public boolean isUserInAssociation(int userId) {
        String query = "SELECT COUNT(*) FROM association WHERE user_id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Association getAssociationByUserId(int userId) {
        String query = "SELECT * FROM Association WHERE user_id = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(query)) {
            preparedStatement.setInt(1, userId); // Set the user ID parameter

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Map the result set to an Association object
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String logo = resultSet.getString("logo");

                return new Association(id,userService.getUserById(userId), name,description, logo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving association by user ID: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null; // Return null if no association is found
    }

    public int countAssociations() throws SQLException {
        String sql = "SELECT COUNT(*) FROM association";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        return rs.next() ? rs.getInt(1) : 0;
    }

    public double getMonthlyGrowthRate() throws SQLException {
        String sql = """
    SELECT 
        (COUNT(CASE WHEN event_date >= DATE_SUB(NOW(), INTERVAL 1 MONTH) THEN 1 END) - 
        COUNT(CASE WHEN event_date >= DATE_SUB(NOW(), INTERVAL 2 MONTH) AND 
                     event_date < DATE_SUB(NOW(), INTERVAL 1 MONTH) THEN 1 END)) * 100.0 / 
        NULLIF(COUNT(CASE WHEN event_date >= DATE_SUB(NOW(), INTERVAL 2 MONTH) AND 
                    event_date < DATE_SUB(NOW(), INTERVAL 1 MONTH) THEN 1 END), 0) as growth_rate
    FROM event
    """;
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        return rs.next() ? rs.getDouble("growth_rate") : 0;
    }

    public Map<String, Integer> getCategoryDistribution() throws SQLException {
        Map<String, Integer> categoryCounts = new HashMap<>();
        String sql = "SELECT category, COUNT(*) as count FROM association GROUP BY category";

        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            categoryCounts.put(rs.getString("category"), rs.getInt("count"));
        }

        return categoryCounts;
    }

    public String getMostPopularCategory() throws SQLException {
        String sql = """
    SELECT a.Name, COUNT(e.type) as count
    FROM event e
    JOIN association a ON e.association_id = a.id
    GROUP BY a.Name
    ORDER BY count DESC
    LIMIT 1
""";

// Execute the query
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

// Return the name of the association organizing the most events
        return rs.next() ? rs.getString("Name") : "N/A";

    }

    public Map<String, Integer> getMonthlyGrowthData() throws SQLException {
        Map<String, Integer> monthlyGrowth = new HashMap<>();
        String sql = """
        SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(*) as count 
        FROM association 
        WHERE created_at >= DATE_SUB(NOW(), INTERVAL 1 YEAR)
        GROUP BY DATE_FORMAT(created_at, '%Y-%m')
        ORDER BY month
        """;

        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            monthlyGrowth.put(rs.getString("month"), rs.getInt("count"));
        }

        return monthlyGrowth;
    }

    public Map<String, Integer> getUserDistribution() throws SQLException {
        Map<String, Integer> userDistribution = new HashMap<>();
        String sql = " SELECT u.role, COUNT(a.id) as count FROM user u LEFT JOIN association a ON u.id = a.user_id GROUP BY u.role ";

        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            userDistribution.put(rs.getString("role"), rs.getInt("count"));
        }

        return userDistribution;
    }

    public List<Association> getTopAssociations(int limit) throws Exception {
        List<Association> topAssociations = new ArrayList<>();
        String sql = """
    SELECT a.*, 
           COUNT(e.id) as event_count
    FROM association a
    LEFT JOIN event e ON a.id = e.association_id
    GROUP BY a.id
    ORDER BY event_count DESC
    LIMIT ?
    """;

        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, limit);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            User user = userService.getUserById(rs.getInt("user_id"));
            Association a = new Association(
                    rs.getInt("id"),
                    user,
                    rs.getString("name"),
                    rs.getString("logo"),
                    rs.getString("description")
            );
            a.setEventCount(rs.getInt("event_count"));
            a.setMemberCount(0); // Set to 0 since we're not counting members anymore
            topAssociations.add(a);
        }

        return topAssociations;
    }

}
