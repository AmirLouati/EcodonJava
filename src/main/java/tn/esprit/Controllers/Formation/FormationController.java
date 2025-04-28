package tn.esprit.Controllers.Formation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.Controllers.Cours.CoursController;
import tn.esprit.Controllers.SharedData;
import tn.esprit.entities.Association;
import tn.esprit.entities.Formation;
import tn.esprit.entities.User;
import tn.esprit.services.AssociationService;
import tn.esprit.services.FormationService;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FormationController implements Initializable {

    @FXML private BorderPane borderPane;
    @FXML private ListView<Formation> formationListView;
    @FXML private Button dashboardButton;
    @FXML private Label userRoleLabel;

    private String associationFxml;

    private final FormationService formationService = new FormationService();
    private final UserService userService = new UserService();
    private final AssociationService associationService = new AssociationService();

    private User currentUser = userService.getUserById(6); // Currently logged-in user
    private String fxml;

    private Association currentAssociation  = associationService.getAssociationByUserId(currentUser.getId());

    public FormationController() throws Exception {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println(currentAssociation.toString());

            loadFormations();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading formations.", ButtonType.OK);
            alert.showAndWait();
        }

        int userId = 6;
        User user = null;
        try {
            user = userService.getUserById(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String realRole = "User"; // Default role

        if (user.getRoles().contains("ROLE_ADMIN")) {
            realRole = "Admin";
        }

        if (realRole.equals("Admin")) {
            dashboardButton.setText("Dashboard");
            associationFxml = "/views/Association/ListAssociation.fxml";
            userRoleLabel.setText("ADMIN");
            fxml = "/views/Home.fxml";
        } else {
            boolean isInAssociation = associationService.isUserInAssociation(userId);
            if (isInAssociation) {
                realRole = "Association";
            }
            fxml = "/views/Home.fxml";
            dashboardButton.setText("Home");
            associationFxml = "/views/Association/ViewAssociation.fxml";
            userRoleLabel.setText(realRole.toUpperCase());
        }
    }

    @FXML
    private void loadHome() {
        loadScene(fxml);
    }

    @FXML
    private void loadAssociation() {
        loadScene(associationFxml);
    }
    private void loadScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root,950,800);
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load formations into the ListView
    private void loadFormations() throws Exception {
        if (formationListView == null) {
            System.err.println("formationListView is null!");
            return;
        }

        // Fetch formations from the database
        List<Formation> formations = formationService.getFormationsByAssociation(currentAssociation.getId());
        ObservableList<Formation> observableList = FXCollections.observableArrayList(formations);

        // Debugging: Print all formations
        for (Formation formation : formations) {
            System.out.println("Formation ID: " + formation.getId());
            System.out.println("Title: " + formation.getTitre());
            System.out.println("Description: " + formation.getDescription());
            System.out.println("Formateur: " + formation.getFormateur());
            System.out.println("Date de début: " + formation.getDateDebut());
        }

        // Debugging: Check if observableList is empty
        if (observableList.isEmpty()) {
            System.err.println("No formations found for the current association.");
        } else {
            System.out.println("Formations loaded successfully.");
        }

        // Set items and apply the custom cell factory
        formationListView.setItems(observableList);
        formationListView.setCellFactory(param -> new FormationListCell());
    }

    // Add Button: Open a new window to add a formation
    @FXML
    private void handleAddButton(ActionEvent event) {
        try {
            System.out.println("qqqqqqqq"+currentAssociation);
            SharedData.setCurrentAssociation(currentAssociation);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/formation/AddFormation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Formation");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait for the window to close

            // Refresh the ListView after adding
            loadFormations();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Add Formation window.", ButtonType.OK);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error refreshing data.", ButtonType.OK);
            alert.showAndWait();
        }
    }


    // Update Button: Open a new window to update the selected formation
    @FXML
    private void handleUpdateButton(ActionEvent event) {
        try {
            // Get the selected formation from the ListView
            Formation selectedFormation = formationListView.getSelectionModel().getSelectedItem();
            if (selectedFormation == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a formation to update.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            System.out.println(selectedFormation.getId());
            System.out.println(selectedFormation);
            SharedData.setSelectedFormationId(selectedFormation.getId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/formation/UpdateFormation.fxml"));
            Parent root = loader.load();

            // Pass the selected formation to the UpdateFormation controller

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Formation");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait for the window to close

            // Refresh the ListView after updating
            loadFormations();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Update Formation window.", ButtonType.OK);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error refreshing data.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Delete Button: Delete the selected formation
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Formation selectedFormation = formationListView.getSelectionModel().getSelectedItem();
        if (selectedFormation == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a formation to delete.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Confirm deletion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this formation?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                formationService.deleteFormation(selectedFormation.getId());
                loadFormations(); // Refresh the ListView
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete formation.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    // Custom ListCell to display formation details
    static class FormationListCell extends ListCell<Formation> {
        private final Label titleLabel = new Label();
        private final Label descriptionLabel = new Label();
        private final Label formateurLabel = new Label();
        private final Label dateLabel = new Label();

        public FormationListCell() {
            super();

            // Apply styles for each data label
            titleLabel.getStyleClass().add("data");
            descriptionLabel.getStyleClass().add("data");
            formateurLabel.getStyleClass().add("data");
            dateLabel.getStyleClass().add("data");

            // Create prefix labels (e.g., "Title:", "Description:")
            Label titlePrefix = new Label("Title:");
            titlePrefix.getStyleClass().add("prefix");

            Label descriptionPrefix = new Label("Description:");
            descriptionPrefix.getStyleClass().add("prefix");

            Label formateurPrefix = new Label("Formateur:");
            formateurPrefix.getStyleClass().add("prefix");

            Label datePrefix = new Label("Date de début:");
            datePrefix.getStyleClass().add("prefix");

            // Use a VBox to hold all labels
            VBox vbox = new VBox(8); // Spacing of 8 pixels between labels
            vbox.setPadding(new Insets(10)); // Add padding for spacing

            // Add prefix labels and their corresponding data labels
            vbox.getChildren().addAll(titlePrefix, titleLabel);
            vbox.getChildren().addAll(descriptionPrefix, descriptionLabel);
            vbox.getChildren().addAll(formateurPrefix, formateurLabel);
            vbox.getChildren().addAll(datePrefix, dateLabel);

            setGraphic(vbox); // Set the VBox as the graphic for the cell
        }
        @Override
        protected void updateItem(Formation formation, boolean empty) {
            super.updateItem(formation, empty);

            if (empty || formation == null) {
                setText(null);
                setGraphic(null);
            } else {
                titleLabel.setText("Title: " + (formation.getTitre() != null ? formation.getTitre() : "N/A"));
                descriptionLabel.setText("Description: " + (formation.getDescription() != null ? formation.getDescription() : "N/A"));
                formateurLabel.setText("Formateur: " + (formation.getFormateur() != null ? formation.getFormateur() : "N/A"));
                dateLabel.setText("Date de début: " + (formation.getDateDebut() != null ? formation.getDateDebut().toString() : "N/A"));

                Button viewCoursesButton = new Button("View Courses");
                viewCoursesButton.getStyleClass().add("view-courses-button");
                viewCoursesButton.setOnAction(e -> {
                    try {
                        SharedData.setFormationid(formation.getId());
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Cours/ListCours.fxml"));
                        Parent root = loader.load();

                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Courses for " + formation.getTitre());
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                VBox vbox = new VBox(8);
                vbox.setPadding(new Insets(10));
                vbox.getChildren().setAll(
                        titleLabel,
                        descriptionLabel,
                        formateurLabel,
                        dateLabel,
                        viewCoursesButton
                );

                setGraphic(vbox);
            }
        }


    }
}