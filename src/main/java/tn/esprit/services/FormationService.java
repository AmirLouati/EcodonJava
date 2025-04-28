package tn.esprit.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import tn.esprit.entities.Association;
import tn.esprit.entities.Formation;
import tn.esprit.tools.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FormationService {

    private final Connection cnx;

    public FormationService() {
        cnx = Database.getInstance().getConnection();
    }

    private final ObjectMapper mapper = new ObjectMapper();

    // ✅ CREATE: Add a new formation to the database
    public void addFormation(Formation formation) throws Exception {
        String sql = "INSERT INTO formation (association_id, titre, description, formateur, date_debut) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, formation.getAssociation().getId()); // Assuming Association has an ID field
        pst.setString(2, formation.getTitre());
        pst.setString(3, formation.getDescription());
        pst.setString(4, formation.getFormateur());
        pst.setTimestamp(5, Timestamp.valueOf(formation.getDateDebut())); // Convert LocalDateTime to Timestamp
        pst.executeUpdate();
    }

    // ✅ READ: Retrieve all formations from the database
    public List<Formation> getAllFormations() throws Exception {
        List<Formation> formations = new ArrayList<>();
        String sql = "SELECT * FROM formation";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Formation formation = new Formation();
            formation.setId(rs.getInt("id"));
            formation.setTitre(rs.getString("titre"));
            formation.setDescription(rs.getString("description"));
            formation.setFormateur(rs.getString("formateur"));
            formation.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime()); // Convert Timestamp to LocalDateTime

            // Fetch the associated association
            int associationId = rs.getInt("association_id");
            AssociationService associationService = new AssociationService();
            Association association = associationService.getAssociationById(associationId); // Retrieve the association by ID
            formation.setAssociation(association);

            formations.add(formation);
        }
        return formations;
    }

    // ✅ UPDATE: Update an existing formation in the database
    public void updateFormation(Formation formation) throws Exception {
        String sql = "UPDATE formation SET association_id=?, titre=?, description=?, formateur=?, date_debut=? WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, formation.getAssociation().getId());
        pst.setString(2, formation.getTitre());
        pst.setString(3, formation.getDescription());
        pst.setString(4, formation.getFormateur());
        pst.setTimestamp(5, Timestamp.valueOf(formation.getDateDebut())); // Convert LocalDateTime to Timestamp
        pst.setInt(6, formation.getId());
        pst.executeUpdate();
    }

    // ✅ DELETE: Delete a formation from the database
    public void deleteFormation(int id) throws SQLException {
        String sql = "DELETE FROM formation WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
    }

    // ✅ FIND BY TITLE: Retrieve a formation by its title
    public Formation getFormationByTitle(String titre) throws Exception {
        String sql = "SELECT * FROM formation WHERE titre=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, titre);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Formation formation = new Formation();
            formation.setId(rs.getInt("id"));
            formation.setTitre(rs.getString("titre"));
            formation.setDescription(rs.getString("description"));
            formation.setFormateur(rs.getString("formateur"));
            formation.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime());

            // Fetch the associated association
            int associationId = rs.getInt("association_id");
            AssociationService associationService = new AssociationService();
            Association association = associationService.getAssociationById(associationId); // Retrieve the association by ID
            formation.setAssociation(association);

            return formation;
        }

        return null;
    }

    // ✅ FIND BY ID: Retrieve a formation by its ID
    public Formation getFormationById(int id) throws Exception {
        String sql = "SELECT * FROM formation WHERE id=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Formation formation = new Formation();
            formation.setId(rs.getInt("id"));
            formation.setTitre(rs.getString("titre"));
            formation.setDescription(rs.getString("description"));
            formation.setFormateur(rs.getString("formateur"));
            formation.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime());

            // Fetch the associated association
            int associationId = rs.getInt("association_id");
            AssociationService associationService = new AssociationService();
            Association association = associationService.getAssociationById(associationId); // Retrieve the association by ID
            formation.setAssociation(association);

            return formation;
        }

        return null;
    }

    public List<Formation> getFormationsByAssociation(int associationId) {
        List<Formation> formations = new ArrayList<>();
        String query = "SELECT * FROM Formation WHERE association_id = ?";

        try (PreparedStatement preparedStatement = cnx.prepareStatement(query)) {
            preparedStatement.setInt(1, associationId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String titre = resultSet.getString("titre");
                String description = resultSet.getString("description");
                String formateur = resultSet.getString("formateur");
                LocalDateTime dateDebut = resultSet.getTimestamp("date_debut").toLocalDateTime();

                Formation formation = new Formation();
                formation.setId(id);
                formation.setTitre(titre);
                formation.setDescription(description);
                formation.setFormateur(formateur);
                formation.setDateDebut(dateDebut);

                formations.add(formation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving formations by association ID: " + e.getMessage());
        }

        return formations;
    }

    public Formation getFormationByTitre(String titre) {
        Formation formation = null;
        String sql = "SELECT * FROM formation WHERE titre = ?";

        try (PreparedStatement preparedStatement = cnx.prepareStatement(sql)) {
            preparedStatement.setString(1, titre);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                formation = new Formation();
                formation.setId(resultSet.getInt("id"));
                formation.setTitre(resultSet.getString("titre"));
                formation.setDescription(resultSet.getString("description"));
                formation.setFormateur(resultSet.getString("formateur"));
                // Set other fields as needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching formation by title: " + e.getMessage());
        }

        return formation;
    }

}