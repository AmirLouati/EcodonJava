package tn.esprit.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import tn.esprit.entities.User;
import tn.esprit.tools.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserService {

    private final Connection cnx;

    public UserService() {
        cnx = Database.getInstance().getConnection();
    }

    private final ObjectMapper mapper = new ObjectMapper();


    // ✅ CREATE
    public void addUser(User user) throws Exception {
        String sql = "INSERT INTO user (nom, email, password, tel, adress, role, roles) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, user.getNom());
        pst.setString(2, user.getEmail());
        pst.setString(3, user.getPassword());
        pst.setString(4, user.getTel());
        pst.setString(5, user.getAdress());
        pst.setString(6, user.getRole());
        pst.setString(7, mapper.writeValueAsString(user.getRoles()));
        pst.executeUpdate();
    }
    // ✅ READ
    public List<User> getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNom(rs.getString("nom"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setTel(rs.getString("tel"));
            user.setAdress(rs.getString("adress"));
            user.setRole(rs.getString("role"));

            String rolesJson = rs.getString("roles");
            List<String> roles = new ArrayList<>(Arrays.asList(mapper.readValue(rolesJson, String[].class)));
            user.setRoles(roles);

            users.add(user);
        }
        return users;
    }

    // ✅ UPDATE
    public void updateUser(User user) throws Exception {
        String sql = "UPDATE user SET nom=?, email=?, password=?, tel=?, adress=?, role=?, roles=? WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, user.getNom());
        pst.setString(2, user.getEmail());
        pst.setString(3, user.getPassword());
        pst.setString(4, user.getTel());
        pst.setString(5, user.getAdress());
        pst.setString(6, user.getRole());
        pst.setString(7, mapper.writeValueAsString(user.getRoles()));
        pst.setInt(8, user.getId());
        pst.executeUpdate();
    }

    // ✅ DELETE
    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
    }


    // ✅ FIND BY ID
    public User getUserById(int id) throws Exception {
        String sql = "SELECT * FROM user WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNom(rs.getString("nom"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setTel(rs.getString("tel"));
            user.setAdress(rs.getString("adress"));
            user.setRole(rs.getString("role"));

            String rolesJson = rs.getString("roles");
            List<String> roles = new ArrayList<>(Arrays.asList(mapper.readValue(rolesJson, String[].class)));

            user.setRoles(roles);

            return user;
        }

        return null;
    }


    // ✅ FIND BY EMAIL
    public User getUserByEmail(String email) throws Exception {
        String sql = "SELECT * FROM user WHERE email=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setNom(rs.getString("nom"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setTel(rs.getString("tel"));
            user.setAdress(rs.getString("adress"));
            user.setRole(rs.getString("role"));

            String rolesJson = rs.getString("roles");
            List<String> roles = new ArrayList<>(Arrays.asList(mapper.readValue(rolesJson, String[].class)));
            user.setRoles(roles);

            return user;
        }

        return null;
    }

    // ✅ Add a new role to a user if not already present
    public void addRoleToUser(int userId, String newRole) throws Exception {
        User user = getUserById(userId);
        if (user == null) return;
        List<String> roles = user.getRoles();
        if (!roles.contains(newRole)) {
            roles.add(newRole);
            user.setRoles(roles);
            updateUser(user); // saves changes
        }
    }

    // ✅ Remove a role from a user
    public void removeRoleFromUser(int userId, String roleToRemove) throws Exception {
        User user = getUserById(userId);
        if (user == null) return;

        List<String> roles = user.getRoles();
        if (roles.contains(roleToRemove)) {
            roles.remove(roleToRemove);
            user.setRoles(roles);
            updateUser(user);
        }
    }

    // ✅ Check if user has a specific role
    public boolean userHasRole(int userId, String roleToCheck) throws Exception {
        User user = getUserById(userId);
        if (user == null) return false;

        return user.getRoles().contains(roleToCheck);
    }

    // ✅ Replace all roles with a new list
    public void setUserRoles(int userId, List<String> newRoles) throws Exception {
        User user = getUserById(userId);
        if (user == null) return;

        user.setRoles(newRoles);
        updateUser(user);
    }


    // ✅ Verify Login (email + password match)
    public boolean checkLogin(String email, String password) throws Exception {
        User user = getUserByEmail(email);
        if (user != null) {
            return user.getPassword().equals(password); // or use BCrypt if hashed
        }
        return false;
    }
    public void addAssociationRole(int userId) throws Exception {
        User user = getUserById(userId);
        if (user == null) return;

        List<String> roles = new ArrayList<>(user.getRoles());
        if (!roles.contains("ROLE_ASSOCIATION")) {
            roles.add("ROLE_ASSOCIATION");
            user.setRoles(roles);

            System.out.println("Final roles to update: " + roles); // ✅ See what you're saving

            updateUser(user); // Check what this actually does!
        }
    }


    public void removeAssociationRole(int userId) throws Exception {
        User user = getUserById(userId);
        if (user == null) return;
        List<String> originalRoles = user.getRoles();
        List<String> roles = new ArrayList<>(originalRoles);
        if (roles.contains("ROLE_ASSOCIATION")) {
            roles.remove("ROLE_ASSOCIATION");
            user.setRoles(roles);
            if ("ROLE_ASSOCIATION".equals(user.getRole())) {
                user.setRole(roles.isEmpty() ? null : roles.get(0));
            }
            updateUser(user);
        }
    }

    public int countUsersWithAssociations() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT user_id) FROM association";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        return rs.next() ? rs.getInt(1) : 0;
    }

    public int countAllUsers() throws SQLException {
        String query = "SELECT COUNT(*) FROM user";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}