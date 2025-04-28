package tn.esprit.services;

import tn.esprit.entities.Donation;
import tn.esprit.tools.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DonationService {

    private Connection connection;

    public DonationService() {
        connection = Database.getInstance().getConnection();
    }

    // ✅ CREATE
    public void addDonation(Donation donation) {
        String query = "INSERT INTO donation (event_id, user_id, first_name, last_name, country, address, apartment, town, state, postal_code, phone, email, order_notes, donation_amount, donation_type, payment_method, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, donation.getEventId());
            stmt.setInt(2, donation.getUserId());
            stmt.setString(3, donation.getFirstName());
            stmt.setString(4, donation.getLastName());
            stmt.setString(5, donation.getCountry());
            stmt.setString(6, donation.getAddress());
            stmt.setString(7, donation.getApartment());
            stmt.setString(8, donation.getTown());
            stmt.setString(9, donation.getState());
            stmt.setString(10, donation.getPostalCode());
            stmt.setString(11, donation.getPhone());
            stmt.setString(12, donation.getEmail());
            stmt.setString(13, donation.getOrderNotes());
            stmt.setDouble(14, donation.getDonationAmount());
            stmt.setString(15, donation.getDonationType());
            stmt.setString(16, donation.getPaymentMethod());
            stmt.setTimestamp(17, Timestamp.valueOf(donation.getCreatedAt()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ READ
    public List<Donation> getAllDonations() {
        List<Donation> donations = new ArrayList<>();
        String query = "SELECT * FROM donation";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                donations.add(mapDonation(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return donations;
    }

    public Donation getDonationById(int id) {
        String query = "SELECT * FROM donation WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapDonation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasUserDonatedToEvent(int userId, int eventId) {
        String query = "SELECT COUNT(*) FROM donation WHERE user_id = ? AND event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<Donation> getDonationsByEventId(int eventId) {
        List<Donation> donations = new ArrayList<>();
        String query = "SELECT * FROM donation WHERE event_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                donations.add(mapDonation(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return donations;
    }

    public List<Donation> getDonationsByUserId(int userId) {
        List<Donation> donations = new ArrayList<>();
        String query = "SELECT * FROM donation WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                donations.add(mapDonation(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return donations;
    }

    public List<Donation> getDonationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Donation> donations = new ArrayList<>();
        String query = "SELECT * FROM donation WHERE created_at BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                donations.add(mapDonation(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return donations;
    }

    // ✅ UPDATE
    public boolean updateDonation(Donation donation) {
        String query = "UPDATE donation SET event_id = ?, user_id = ?, first_name = ?, last_name = ?, country = ?, address = ?, apartment = ?, town = ?, state = ?, postal_code = ?, phone = ?, email = ?, order_notes = ?, donation_amount = ?, donation_type = ?, payment_method = ?, created_at = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, donation.getEventId());
            stmt.setInt(2, donation.getUserId());
            stmt.setString(3, donation.getFirstName());
            stmt.setString(4, donation.getLastName());
            stmt.setString(5, donation.getCountry());
            stmt.setString(6, donation.getAddress());
            stmt.setString(7, donation.getApartment());
            stmt.setString(8, donation.getTown());
            stmt.setString(9, donation.getState());
            stmt.setString(10, donation.getPostalCode());
            stmt.setString(11, donation.getPhone());
            stmt.setString(12, donation.getEmail());
            stmt.setString(13, donation.getOrderNotes());
            stmt.setDouble(14, donation.getDonationAmount());
            stmt.setString(15, donation.getDonationType());
            stmt.setString(16, donation.getPaymentMethod());
            stmt.setTimestamp(17, Timestamp.valueOf(donation.getCreatedAt()));
            stmt.setInt(18, donation.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ DELETE
    public boolean deleteDonation(int id) {
        String query = "DELETE FROM donation WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ ADVANCED METHODS

    public double getTotalDonationsByEventId(int eventId) {
        String query = "SELECT SUM(donation_amount) AS total FROM donation WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalDonationsByUserId(int userId) {
        String query = "SELECT SUM(donation_amount) AS total FROM donation WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // OPTIONAL: Get donation stats (e.g., count per event or per type)
    public int getDonationCountByEventId(int eventId) {
        String query = "SELECT COUNT(*) AS count FROM donation WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // MAP FUNCTION
    private Donation mapDonation(ResultSet rs) throws SQLException {
        Donation d = new Donation();
        d.setId(rs.getInt("id"));
        d.setEventId(rs.getInt("event_id"));
        d.setUserId(rs.getInt("user_id"));
        d.setFirstName(rs.getString("first_name"));
        d.setLastName(rs.getString("last_name"));
        d.setCountry(rs.getString("country"));
        d.setAddress(rs.getString("address"));
        d.setApartment(rs.getString("apartment"));
        d.setTown(rs.getString("town"));
        d.setState(rs.getString("state"));
        d.setPostalCode(rs.getString("postal_code"));
        d.setPhone(rs.getString("phone"));
        d.setEmail(rs.getString("email"));
        d.setOrderNotes(rs.getString("order_notes"));
        d.setDonationAmount(rs.getDouble("donation_amount"));
        d.setDonationType(rs.getString("donation_type"));
        d.setPaymentMethod(rs.getString("payment_method"));
        d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return d;
    }
}
