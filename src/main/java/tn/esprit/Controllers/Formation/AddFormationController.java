package tn.esprit.Controllers.Formation;

import com.google.api.client.util.DateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.Controllers.SharedData;
import tn.esprit.entities.Association;
import tn.esprit.entities.Formation;
import tn.esprit.services.FormationService;
import tn.esprit.services.GmailService;
import tn.esprit.services.TwilioService;

import java.time.LocalDateTime;

public class AddFormationController {

    @FXML
    private Label associationLabel; // To display the current association

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

    private final FormationService formationService = new FormationService();
    private Association currentAssociation; // The currently selected association

    public void initialize() {
        currentAssociation = SharedData.getCurrentAssociation();
        // Add real-time validation for each field
        titreField.setOnKeyReleased(event -> validateTitre());
        descriptionField.setOnKeyReleased(event -> validateDescription());
        formateurField.setOnKeyReleased(event -> validateFormateur());
        dateDebutPicker.setOnAction(event -> validateDate());
        System.out.println("dsdssdsd"+currentAssociation);
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
    private void handleSaveButton(ActionEvent event) {
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

            if (titre.isEmpty() || description.isEmpty() || formateur.isEmpty() || dateDebut == null || currentAssociation == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please correct the errors in the form.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            // Create the new formation
            Formation formation = new Formation();
            formation.setTitre(titre);
            formation.setDescription(description);
            formation.setFormateur(formateur);
            formation.setDateDebut(dateDebut);
            formation.setAssociation(currentAssociation);
            LocalDateTime localDateTime = LocalDateTime.now();
            String email ="Louati.amir@esprit.tn";
            String object ="Formaton created successfully";
            String message="A formation was crated on: "+localDateTime+" with name: "+formation.getTitre()
                    +" for formateur: "+ formation.getFormateur();
            // Save the formation
            formationService.addFormation(formation);

            GmailService mailService = new GmailService();
            mailService.sendEmail(
                    email,
                    object,
                    message
            );
            //String smsMessage = "New created for" + formation.getDateDebut() + " has been successfully added! ";
            //TwilioService.sendSMS(smsMessage);

            // Close the window
            ((Stage) titreField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save formation.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Handle Cancel Button
    @FXML
    private void handleCancelButton(ActionEvent event) {
        ((Stage) titreField.getScene().getWindow()).close();
    }
}