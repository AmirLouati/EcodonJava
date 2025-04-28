package tn.esprit.Controllers.Association;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Association;
import tn.esprit.entities.User;
import tn.esprit.services.AssociationService;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class ViewAssocationController {
    @FXML
    public Button searchButton;
    @FXML
    public TextField searchField;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ListView<Association> All;
    private AssociationService associationService = new AssociationService();
    private ObservableList<Association> publicationList;
    @FXML private Button dashboardButton;
    @FXML private Button associationBtn;
    @FXML private Label userRoleLabel;

    private UserService userService = new UserService();
    private String userRole = "User";
    private String fxml;

    public ViewAssocationController() throws SQLException {
    }

    @FXML
    public void initialize() {
        try {
            loadAssociations();

            // Fetch current user
            User currentUser = userService.getUserById(6);

            // Get the actual role from the 'roles' list
            if (currentUser.getRoles().contains("ROLE_ADMIN")) {
                userRole = "Admin";
                dashboardButton.setText("Dashboard");
                fxml = "/views/Home.fxml";
            } else if (currentUser.getRoles().contains("ROLE_ASSOCIATION")) {
                userRole = "Association";
                dashboardButton.setText("Home");
                associationBtn.setText("My Association");
                fxml = "/views/Home.fxml";
            } else {
                userRole = "User";
                dashboardButton.setText("Home");
                fxml = "/views/Home.fxml";
            }

            // Update label
            userRoleLabel.setText(userRole.toUpperCase());

        } catch (Exception e) {
            showError("Initialization Error", e.getMessage());
        }
    }


    @FXML
    private void loadHome() {
        loadScene(fxml);
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
    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            // Fetch current user
            User currentUser = userService.getUserById(6);  // Change this to dynamically get the logged-in user

            // Check if the user already has the "ROLE_ASSOCIATION"
            if (currentUser.getRoles().contains("ROLE_ASSOCIATION")) {
                // Show an alert that the user cannot add another association
                showError("Action Not Allowed", "You are already part of an association and cannot add a new one.");
                return;  // Prevent the Add Association window from opening
            }

            // Open Add Association window if the user is not an association member
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/association/AddAssociation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait for the window to close

            // Refresh the TableView after adding
            loadAssociations();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Add Association window.", ButtonType.OK);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error refreshing data.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void openChatbotWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Chatbot.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Chatbot");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le Chatbot : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleShowStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Association/AssociationStats.fxml"));
            Parent root = loader.load();

            Stage statsStage = new Stage();
            statsStage.setTitle("Association Statistics Dashboard");
            statsStage.setScene(new Scene(root));
            statsStage.initModality(Modality.APPLICATION_MODAL);
            statsStage.initOwner(borderPane.getScene().getWindow());
            statsStage.show();
        } catch (IOException e) {
            showAlert("Error", "Could not load statistics dashboard", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    void loadAssociations() {
        try {
            publicationList = FXCollections.observableArrayList(associationService.getAllAssociations());
            All.setItems(publicationList);
            All.setCellFactory(param -> {
                try {
                    return new AssociationCell(borderPane,this);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            showError("Error", "Failed to load publications: " + e.getMessage());
        }

    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }


    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ Helper Method: Display Success Alert
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            // If search field is empty, show all associations
            All.setItems(publicationList);
        } else {
            // Filter associations whose name contains the search text
            ObservableList<Association> filteredList = FXCollections.observableArrayList();
            for (Association association : publicationList) {
                if (association.getName() != null && association.getName().toLowerCase().contains(searchText)) {
                    filteredList.add(association);
                }
            }
            All.setItems(filteredList);
        }
    }


}
