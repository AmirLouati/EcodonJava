package tn.esprit.Controllers.Association;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Association;
import tn.esprit.entities.User;
import tn.esprit.services.AssociationService;
import tn.esprit.services.UserService;

import java.io.File;
import java.sql.SQLException;

public class AddAssociationController {

    private File selectedImageFile;

    @FXML
    private ImageView logoImageView;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Label nameValidationMessage;
    @FXML
    private Label descriptionValidationMessage;

    private final UserService userService = new UserService();
    private User currentUser;

    {
        try {
            currentUser = userService.getUserById(6);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final AssociationService associationService = new AssociationService();

    public AddAssociationController() throws SQLException {
    }

    @FXML
    public void initialize() {
        // Add listeners to validate input fields in real time
        nameField.setOnKeyReleased(event -> validateName());
        descriptionField.setOnKeyReleased(event -> validateDescription());
    }

    // Validate Name Field
    private void validateName() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            nameValidationMessage.setText("Name is required.");
            nameValidationMessage.getStyleClass().removeAll("success");
            nameValidationMessage.getStyleClass().add("validation-message");
            nameField.getStyleClass().removeAll("valid");
            nameField.getStyleClass().add("invalid");
        } else if (name.length() < 3) {
            nameValidationMessage.setText("Name must be at least 3 characters long.");
            nameValidationMessage.getStyleClass().removeAll("success");
            nameValidationMessage.getStyleClass().add("validation-message");
            nameField.getStyleClass().removeAll("valid");
            nameField.getStyleClass().add("invalid");
        } else {
            nameValidationMessage.setText("Looks good!");
            nameValidationMessage.getStyleClass().removeAll("validation-message");
            nameValidationMessage.getStyleClass().add("success");
            nameField.getStyleClass().removeAll("invalid");
            nameField.getStyleClass().add("valid");
        }
    }

    // Validate Description Field
    private void validateDescription() {
        String description = descriptionField.getText();
        if (description == null || description.trim().isEmpty()) {
            descriptionValidationMessage.setText("Description is required.");
            descriptionValidationMessage.getStyleClass().removeAll("success");
            descriptionValidationMessage.getStyleClass().add("validation-message");
            descriptionField.getStyleClass().removeAll("valid");
            descriptionField.getStyleClass().add("invalid");
        } else if (description.length() < 10) {
            descriptionValidationMessage.setText("Description must be at least 10 characters long.");
            descriptionValidationMessage.getStyleClass().removeAll("success");
            descriptionValidationMessage.getStyleClass().add("validation-message");
            descriptionField.getStyleClass().removeAll("valid");
            descriptionField.getStyleClass().add("invalid");
        } else {
            descriptionValidationMessage.setText("Looks good!");
            descriptionValidationMessage.getStyleClass().removeAll("validation-message");
            descriptionValidationMessage.getStyleClass().add("success");
            descriptionField.getStyleClass().removeAll("invalid");
            descriptionField.getStyleClass().add("valid");
        }
    }

    // Handle Save Button
    @FXML
    private void handleSave(ActionEvent event) {
        validateName();
        validateDescription();

        if (nameField.getStyleClass().contains("invalid") || descriptionField.getStyleClass().contains("invalid") || selectedImageFile == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please fix all errors and upload a logo.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            // Create a new association
            Association association = new Association();
            association.setName(nameField.getText());
            association.setDescription(descriptionField.getText());
            association.setLogo(selectedImageFile.getAbsolutePath());
            association.setUser(currentUser);

            // Save the association to the database
            associationService.addAssociation(association);
            userService.addRoleToUser(currentUser.getId(),"ROLE_ASSOCIATION");

            // Close the window
            ((Stage) nameField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add association.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Handle Cancel Button
    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    // Handle Upload Image Button
    @FXML
    private void handleUploadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Logo Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        // Open the file chooser dialog
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            // Display the selected image in the ImageView
            Image image = new Image(selectedImageFile.toURI().toString());
            logoImageView.setImage(image);
        }
    }
}