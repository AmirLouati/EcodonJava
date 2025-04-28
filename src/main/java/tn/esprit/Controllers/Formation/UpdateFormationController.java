package tn.esprit.Controllers.Formation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.Controllers.SharedData;
import tn.esprit.entities.Formation;
import tn.esprit.services.FormationService;

import java.time.LocalDateTime;

public class UpdateFormationController {

    @FXML
    private TextField titreField;
    @FXML
    private Label titreMessage;
    @FXML
    private TextField descriptionField;
    @FXML
    private Label descriptionMessage;
    @FXML
    private TextField formateurField;
    @FXML
    private Label formateurMessage;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private Label dateMessage;
    private Formation selectedFormation; // The formation being updated
    private final FormationService formationService = new FormationService();

    public void initialize() throws Exception {
        selectedFormation = formationService.getFormationById(SharedData.getSelectedFormationId());
        System.out.println("aaaaaa"+selectedFormation.getId());
        System.out.println("eeeeeeeeeee"+selectedFormation.toString());
        // Add real-time validation for each field
        titreField.setOnKeyReleased(event -> validateTitre());
        descriptionField.setOnKeyReleased(event -> validateDescription());
        formateurField.setOnKeyReleased(event -> validateFormateur());
        dateDebutPicker.setOnAction(event -> validateDate());

        titreField.setText(selectedFormation.getTitre());
        descriptionField.setText(selectedFormation.getDescription());
        formateurField.setText(selectedFormation.getFormateur());
        dateDebutPicker.setValue(selectedFormation.getDateDebut().toLocalDate());
    }

    // Real-time validation methods
    private void validateTitre() {
        String titre = titreField.getText();
        if (titre.isEmpty()) {
            titreMessage.setText("Title is required.");
            titreMessage.getStyleClass().removeAll("valid");
            titreMessage.getStyleClass().add("invalid");
        } else if (titre.length() < 3) {
            titreMessage.setText("Title must be at least 3 characters.");
            titreMessage.getStyleClass().removeAll("valid");
            titreMessage.getStyleClass().add("invalid");
        } else {
            titreMessage.setText("Looks good!");
            titreMessage.getStyleClass().removeAll("invalid");
            titreMessage.getStyleClass().add("valid");
        }
    }

    private void validateDescription() {
        String description = descriptionField.getText();
        if (description.isEmpty()) {
            descriptionMessage.setText("Description is required.");
            descriptionMessage.getStyleClass().removeAll("valid");
            descriptionMessage.getStyleClass().add("invalid");
        } else if (description.length() < 10) {
            descriptionMessage.setText("Description must be at least 10 characters.");
            descriptionMessage.getStyleClass().removeAll("valid");
            descriptionMessage.getStyleClass().add("invalid");
        } else {
            descriptionMessage.setText("Looks good!");
            descriptionMessage.getStyleClass().removeAll("invalid");
            descriptionMessage.getStyleClass().add("valid");
        }
    }

    private void validateFormateur() {
        String formateur = formateurField.getText();
        if (formateur.isEmpty()) {
            formateurMessage.setText("Formateur is required.");
            formateurMessage.getStyleClass().removeAll("valid");
            formateurMessage.getStyleClass().add("invalid");
        } else if (!formateur.matches("[a-zA-Z\\s]+")) {
            formateurMessage.setText("Formateur name should only contain letters and spaces.");
            formateurMessage.getStyleClass().removeAll("valid");
            formateurMessage.getStyleClass().add("invalid");
        } else {
            formateurMessage.setText("Looks good!");
            formateurMessage.getStyleClass().removeAll("invalid");
            formateurMessage.getStyleClass().add("valid");
        }
    }

    private void validateDate() {
        LocalDateTime dateDebut = dateDebutPicker.getValue() != null
                ? dateDebutPicker.getValue().atStartOfDay()
                : null;
        if (dateDebut == null) {
            dateMessage.setText("Date is required.");
            dateMessage.getStyleClass().removeAll("valid");
            dateMessage.getStyleClass().add("invalid");
        } else if (dateDebut.isBefore(LocalDateTime.now())) {
            dateMessage.setText("Date cannot be in the past.");
            dateMessage.getStyleClass().removeAll("valid");
            dateMessage.getStyleClass().add("invalid");
        } else {
            dateMessage.setText("Looks good!");
            dateMessage.getStyleClass().removeAll("invalid");
            dateMessage.getStyleClass().add("valid");
        }
    }

    // Handle Save Button
    @FXML
    private void handleSave(ActionEvent event) {
        try {
            String titre = titreField.getText();
            String description = descriptionField.getText();
            String formateur = formateurField.getText();
            LocalDateTime dateDebut = dateDebutPicker.getValue() != null
                    ? dateDebutPicker.getValue().atStartOfDay()
                    : null;

            // Validate all fields before saving
            validateTitre();
            validateDescription();
            validateFormateur();
            validateDate();

            if (titre.isEmpty() || description.isEmpty() || formateur.isEmpty() || dateDebut == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please correct the errors in the form.", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            System.out.println(selectedFormation.getId()+"eeeeeeeee");

            // Update the formation fields
            selectedFormation.setTitre(titre);
            selectedFormation.setDescription(description);
            selectedFormation.setFormateur(formateur);
            selectedFormation.setDateDebut(dateDebut);

            // Save the updated formation to the database
            formationService.updateFormation(selectedFormation);

            // Close the window
            ((Stage) titreField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update formation.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Handle Cancel Button
    @FXML
    private void handleCancel(ActionEvent event) {
        ((Stage) titreField.getScene().getWindow()).close();
    }
}