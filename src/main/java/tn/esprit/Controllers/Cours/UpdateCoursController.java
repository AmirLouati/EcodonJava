package tn.esprit.Controllers.Cours;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Association;
import tn.esprit.entities.Cours;
import tn.esprit.entities.Formation;
import tn.esprit.services.CoursService;
import tn.esprit.services.FormationService;

import java.util.List;

public class UpdateCoursController {

    @FXML
    private Label associationLabel;

    @FXML
    private ComboBox<String> formationComboBox;

    @FXML
    private TextField courseNameField;
    @FXML
    private Label courseNameMessage;

    @FXML
    private TextField descriptionField;
    @FXML
    private Label descriptionMessage;

    @FXML
    private Label formationMessage;

    private final CoursService coursService = new CoursService();
    private final FormationService formationService = new FormationService();

    private Cours selectedCourse; // The course being updated
    private ObservableList<String> formationNames = FXCollections.observableArrayList();

    public void initialize() {
        // Add real-time validation for each field
        courseNameField.setOnKeyReleased(event -> validateCourseName());
        descriptionField.setOnKeyReleased(event -> validateDescription());

        // Add listener to capture the selected formation
        formationComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                formationMessage.setText("Please select a formation.");
                formationMessage.getStyleClass().removeAll("valid");
                formationMessage.getStyleClass().add("invalid");
            } else {
                formationMessage.setText("");
                formationMessage.getStyleClass().removeAll("invalid");
            }
        });
    }

    /**
     * Sets the course to be updated.
     * Pre-fills the fields with the current course data.
     *
     * @param selectedCourse The course to update.
     */
    public void setCourse(Cours selectedCourse) {
        this.selectedCourse = selectedCourse;

        // Display the association's name
        Association currentAssociation = selectedCourse.getFormation().getAssociation(); // Assuming Formation has an Association
        if (associationLabel != null && currentAssociation != null) {
            associationLabel.setText(currentAssociation.getName());
        }

        // Fetch and populate the formations of the association
        try {
            List<Formation> formations = formationService.getFormationsByAssociation(currentAssociation.getId());
            for (Formation formation : formations) {
                formationNames.add(formation.getTitre());
            }
            formationComboBox.setItems(formationNames);

            // Pre-select the current formation
            formationComboBox.getSelectionModel().select(selectedCourse.getFormation().getTitre());
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load formations.", ButtonType.OK);
            alert.showAndWait();
        }

        // Pre-fill the course details
        courseNameField.setText(selectedCourse.getCour());
        descriptionField.setText(selectedCourse.getDescription());
    }

    // Real-time validation methods
    private void validateCourseName() {
        String courseName = courseNameField.getText();
        if (courseName.isEmpty()) {
            courseNameMessage.setText("Course name is required.");
            courseNameMessage.getStyleClass().removeAll("valid");
            courseNameMessage.getStyleClass().add("invalid");
        } else if (courseName.length() < 3) {
            courseNameMessage.setText("Course name must be at least 3 characters.");
            courseNameMessage.getStyleClass().removeAll("valid");
            courseNameMessage.getStyleClass().add("invalid");
        } else {
            courseNameMessage.setText("Looks good!");
            courseNameMessage.getStyleClass().removeAll("invalid");
            courseNameMessage.getStyleClass().add("valid");
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

    // Handle Save Button
    @FXML
    private void handleSaveButton(ActionEvent event) {
        try {
            String courseName = courseNameField.getText();
            String description = descriptionField.getText();
            String selectedFormationTitle = formationComboBox.getValue();

            // Validate all fields before saving
            validateCourseName();
            validateDescription();

            if (courseName.isEmpty() || description.isEmpty() || selectedFormationTitle == null || selectedFormationTitle.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please correct the errors in the form.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            // Find the selected formation by its title
            Formation selectedFormation = formationService.getFormationByTitre(selectedFormationTitle);
            if (selectedFormation == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Selected formation not found.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            // Update the course fields
            selectedCourse.setCour(courseName);
            selectedCourse.setDescription(description);
            selectedCourse.setFormation(selectedFormation);

            // Save the updated course
            coursService.updateCours(selectedCourse);

            // Close the window
            ((Stage) courseNameField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update course.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Handle Cancel Button
    @FXML
    private void handleCancelButton(ActionEvent event) {
        ((Stage) courseNameField.getScene().getWindow()).close();
    }
}