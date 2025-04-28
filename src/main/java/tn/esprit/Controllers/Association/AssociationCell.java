package tn.esprit.Controllers.Association;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.Controllers.Event.ViewEventController;
import tn.esprit.entities.Association;
import tn.esprit.entities.User;
import tn.esprit.services.AssociationService;
import tn.esprit.services.UserService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class AssociationCell extends ListCell<Association> {

    private VBox content;
    private ImageView imageView;
    private Text nameText;
    private Text descriptionText;
    private Label founderLabel;
    private Button btnViewEvents;
    private Button btnEdit;
    private Button btnDelete;
    private BorderPane mainContainer;
    private static final String DEFAULT_IMAGE_PATH = System.getProperty("user.home") + "/EcodonImages/default.png";
    private AssociationService associationService = new AssociationService();  // Make sure the service is implemented

    private UserService userService = new UserService();
    User currentUser = userService.getUserById(6);
    private ViewAssocationController viewAssocationController;

    public AssociationCell(BorderPane mainContainer,ViewAssocationController controller) throws Exception {
        this.mainContainer = mainContainer;
        this.viewAssocationController = controller;

        imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 4, 4);");

        nameText = new Text();
        nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-fill: #2C3E50;");

        descriptionText = new Text();
        descriptionText.setStyle("-fx-font-size: 14px; -fx-fill: #555555;");

        founderLabel = new Label();
        founderLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27AE60;");

        btnViewEvents = new Button("🎉 View Events");
        btnViewEvents.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 10px;");
        btnViewEvents.setOnAction(e -> handleViewEvents());

        // Edit Button
        btnEdit = new Button("✏️ Edit");
        btnEdit.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-background-radius: 10px;");
        btnEdit.setOnAction(e -> handleEditAssociation());

        // Delete Button
        btnDelete = new Button("🗑️ Delete");
        btnDelete.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 10px;");
        btnDelete.setOnAction(e -> handleDeleteAssociation());

        content = new VBox(10, nameText, imageView, descriptionText, founderLabel, btnViewEvents, btnEdit, btnDelete);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 5, 5);");
    }

    @Override
    protected void updateItem(Association association, boolean empty) {
        super.updateItem(association, empty);

        if (empty || association == null) {
            setGraphic(null);
        } else {
            nameText.setText("🏛️ " + association.getName());
            descriptionText.setText("📃 " + association.getDescription());

            if (association.getUser() != null) {
                founderLabel.setText("👤 Founder: " + association.getUser().getNom());
            } else {
                founderLabel.setText("👤 Founder: Unknown");
            }

            String imagePath = association.getLogo();
            Image image = loadImage(imagePath);
            imageView.setImage(image);

            // Only show edit and delete buttons if the current user is the founder
            if (currentUser != null && currentUser.getId() == association.getUser().getId()) {
                btnEdit.setVisible(true);
                btnDelete.setVisible(true);
            } else {
                btnEdit.setVisible(false);
                btnDelete.setVisible(false);
            }

            setGraphic(content);
        }
    }


    private void handleViewEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Event/ViewEvent.fxml"));
            Parent root = loader.load();

            // Get controller and pass the association
            ViewEventController controller = loader.getController();
            controller.setAssociation(getItem());

            // Create new Stage for events
            Stage newStage = new Stage();
            newStage.setTitle("Events for " + getItem().getName());
            newStage.setScene(new Scene(root, 950, 800));
            newStage.show();

            // Close current stage
            Stage currentStage = (Stage) getListView().getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showError("Error", "Erreur lors du chargement de la page des événements");
            e.printStackTrace();
        }
    }

    private void handleEditAssociation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Association/UpdateAssociation.fxml"));
            Parent root = loader.load();

            // Pass the association to the controller
            UpdateAssociationController controller = loader.getController();
            controller.setAssociation(getItem());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Association");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh the list view or perform any necessary updates
            refreshAssociationList();

        } catch (IOException e) {
            showError("Error", "Erreur lors du chargement de l'édition de l'association");
            e.printStackTrace();
        }
    }

    private void handleDeleteAssociation() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this association?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    associationService.deleteAssociation(getItem().getId());
                    userService.removeAssociationRole(currentUser.getId());
                    showInfo("Deleted", "Association deleted successfully.");
                    viewAssocationController.loadAssociations();
                } catch (SQLException e) {
                    showError("Error", "Failed to delete the association: " + e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshAssociationList() {
        viewAssocationController.loadAssociations();
    }

    private Image loadImage(String imagePath) {
        File file = new File(imagePath);
        return file.exists() ? new Image(file.toURI().toString()) : new Image(new File(DEFAULT_IMAGE_PATH).toURI().toString());
    }
}
