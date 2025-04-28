package tn.esprit.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import tn.esprit.entities.Cours;
import tn.esprit.entities.Formation;
import tn.esprit.tools.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursService {

    private final Connection cnx;

    public CoursService() {
        cnx = Database.getInstance().getConnection();
    }

    private final ObjectMapper mapper = new ObjectMapper();

    // ✅ CREATE: Add a new course to the database
    public void addCours(Cours cours) throws Exception {
        String sql = "INSERT INTO cour (cour, decription, id_formation_fk) VALUES (?, ?, ?)";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, cours.getCour());
        pst.setString(2, cours.getDescription());
        pst.setInt(3, cours.getFormation().getId()); // Assuming Formation has an ID field
        pst.executeUpdate();
    }

    // ✅ READ: Retrieve all courses from the database
    public List<Cours> getAllCours() throws Exception {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT * FROM cour";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Cours cours = new Cours();
            cours.setId(rs.getInt("id_cour"));
            cours.setCour(rs.getString("cour"));
            cours.setDescription(rs.getString("decription"));

            // Fetch the associated formation
            int formationId = rs.getInt("id_formation_FK");
            FormationService formationService = new FormationService();
            Formation formation = formationService.getFormationById(formationId); // Retrieve the formation by ID
            cours.setFormation(formation);

            coursList.add(cours);
        }
        return coursList;
    }


    public List<Cours> getAllCoursByFormation(Formation f) throws Exception {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT * FROM cour where id_formation_FK=?";
        PreparedStatement st = cnx.prepareStatement(sql);
        st.setInt(1, f.getId());
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Cours cours = new Cours();
            cours.setId(rs.getInt("id_cour"));
            cours.setCour(rs.getString("cour"));
            cours.setDescription(rs.getString("decription"));

            // Fetch the associated formation
            int formationId = rs.getInt("id_formation_FK");
            FormationService formationService = new FormationService();
            Formation formation = formationService.getFormationById(formationId); // Retrieve the formation by ID
            cours.setFormation(formation);

            coursList.add(cours);
        }
        return coursList;
    }

    // ✅ UPDATE: Update an existing course in the database
    public void updateCours(Cours cours) throws Exception {
        String sql = "UPDATE cour SET cour=?, decription=?, id_formation_fk=? WHERE id_cour=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, cours.getCour());
        pst.setString(2, cours.getDescription());
        pst.setInt(3, cours.getFormation().getId());
        pst.setInt(4, cours.getId());
        pst.executeUpdate();
    }

    // ✅ DELETE: Delete a course from the database
    public void deleteCours(int id) throws SQLException {
        String sql = "DELETE FROM cour WHERE id_cour=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
    }

    // ✅ FIND BY NAME: Retrieve a course by its name
    public Cours getCoursByName(String name) throws Exception {
        String sql = "SELECT * FROM cour WHERE cour=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, name);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Cours cours = new Cours();
            cours.setId(rs.getInt("id_cour"));
            cours.setCour(rs.getString("cour"));
            cours.setDescription(rs.getString("decription"));

            // Fetch the associated formation
            int formationId = rs.getInt("id_formation_fk");
            FormationService formationService = new FormationService();
            Formation formation = formationService.getFormationById(formationId); // Retrieve the formation by ID
            cours.setFormation(formation);

            return cours;
        }

        return null;
    }

    // ✅ FIND BY ID: Retrieve a course by its ID
    public Cours getCoursById(int id) throws Exception {
        String sql = "SELECT * FROM cour WHERE id_cour=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Cours cours = new Cours();
            cours.setId(rs.getInt("id_cour"));
            cours.setCour(rs.getString("cour"));
            cours.setDescription(rs.getString("decription"));

            // Fetch the associated formation
            int formationId = rs.getInt("id_formation_fk");
            FormationService formationService = new FormationService();
            Formation formation = formationService.getFormationById(formationId); // Retrieve the formation by ID
            cours.setFormation(formation);

            return cours;
        }

        return null;
    }

    public List<Cours> searchCourses(String query) {
        List<Cours> matchingCourses = new ArrayList<>();
        FormationService formationService = new FormationService();
        String sql = "SELECT * FROM cour WHERE cour LIKE ?";

        try (PreparedStatement preparedStatement = cnx.prepareStatement(sql)) {
            // Set only one parameter
            preparedStatement.setString(1, "%" + query + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                // Map the result set to a Cours object
                Cours cours = new Cours();
                cours.setId(resultSet.getInt("id_cour"));
                cours.setCour(resultSet.getString("cour"));
                cours.setDescription(resultSet.getString("decription")); // fixed typo

                // Fetch the associated Formation (if applicable)
                int formationId = resultSet.getInt("id_formation_fk");
                if (formationId != 0) { // Assuming 0 means no association
                    Formation formation = formationService.getFormationById(formationId);
                    cours.setFormation(formation);
                }

                matchingCourses.add(cours);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error searching for courses: " + e.getMessage());
        }

        return matchingCourses;
    }
    public List<Cours> getCoursByNames(String name) throws Exception {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT * FROM cour WHERE cour=?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, name);
        ResultSet rs = pst.executeQuery();

        FormationService formationService = new FormationService();

        while (rs.next()) {
            Cours cours = new Cours();
            cours.setId(rs.getInt("id_cour"));
            cours.setCour(rs.getString("cour"));
            cours.setDescription(rs.getString("description")); // Fixed typo

            // Fetch the associated formation
            int formationId = rs.getInt("id_formation_fk");
            if (formationId != 0) {
                Formation formation = formationService.getFormationById(formationId);
                cours.setFormation(formation);
            }

            coursList.add(cours);
        }

        return coursList;
    }



}