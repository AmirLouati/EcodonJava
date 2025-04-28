package tn.esprit.Controllers.Event;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Association;
import tn.esprit.entities.Event;
import tn.esprit.entities.User;
import tn.esprit.services.EventService;
import tn.esprit.services.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class ViewEventController {

    @FXML private Button addButton;
    @FXML
    private HBox adminControls;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ListView<Event> All;
    private EventService eventService = new EventService();
    private ObservableList<Event> publicationList;
    @FXML private Button dashboardButton;
    @FXML private Label userRoleLabel;
    private Association currentAssociation;
    private UserService userService = new UserService();
    private String userRole = "User";
    User currentUser = userService.getUserById(6);
    private String fxml;

    public ViewEventController() throws Exception {
    }

    @FXML
    public void initialize() {
        try {
            userRole = currentUser.getRole();
            if (userRole.equalsIgnoreCase("Admin")) {
                dashboardButton.setText("Dashboard");
                fxml = "/views/Home.fxml";
            } else {
                dashboardButton.setText("Home");
                fxml = "/views/Home.fxml";
            }

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

    public void setAssociation(Association association) {
        this.currentAssociation = association;
        loadEvents();
        if (currentUser != null && currentUser.getId() == currentAssociation.getUser().getId()) {
            addButton.setVisible(true);
            addButton.setOnAction(e -> openAddEventPage());
        }
    }
    @FXML
    private void handleAjouter(ActionEvent event){
        openAddEventPage();
    }
    @FXML
    private void handleBack() {
        loadScene("/views/Association/ViewAssociation.fxml");  // Adjust the path if necessary
    }

    @FXML
    private void handleAddLocation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Event/AddLocation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Location");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh locations if needed
        } catch (IOException e) {
            showError("Error", "Could not open location form");
            e.printStackTrace();
        }
    }
    void loadEvents() {
        try {
            if (currentAssociation == null) {
                showError("No Association", "No association was selected.");
                return;
            }

            publicationList = FXCollections.observableArrayList(
                    eventService.getEventsByAssociation(currentAssociation.getId())
            );
            System.out.println("Loaded events: " + publicationList.size());

            All.setItems(publicationList);
            All.setCellFactory(param -> {
                try {
                    return new EventCell(this);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            All.setPlaceholder(new Label("No events associated with this association."));

        } catch (Exception e) {
            showError("Error", "Failed to load events: " + e.getMessage());
        }
    }
    private void openAddEventPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Event/AddEvent.fxml"));
            Parent root = loader.load();

            // Pass association to the AddEvent controller
            AddEventController controller = loader.getController();
            controller.setAssociation(currentAssociation);

            // Pass the current ViewEventController to AddEventController
            controller.setViewEventController(this);

            // Create a new Stage and set modality
            Stage stage = new Stage();
            stage.setTitle("Add New Event");
            stage.setScene(new Scene(root, 800, 600));

            // Set modality to block interaction with parent window
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // Show the modal window and wait for it to be closed
            stage.showAndWait();

        } catch (IOException e) {
            showError("Error", "Failed to open Add Event page: " + e.getMessage());
        }
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
}
