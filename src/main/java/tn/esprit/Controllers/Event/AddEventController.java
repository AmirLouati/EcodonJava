package tn.esprit.Controllers.Event;

import tn.esprit.entities.Association;
import tn.esprit.entities.Event;
import tn.esprit.entities.Location;
import tn.esprit.services.EventService;
import tn.esprit.services.LocationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AddEventController {

    @FXML private Label labelAdd;
    @FXML private Button addButton;
    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private DatePicker datePicker;
    @FXML private TextField priceField;
    @FXML private ComboBox<Location> locationComboBox;
    @FXML private TextField imagePathField;

    private final EventService eventService = new EventService();
    private final LocationService locationService = new LocationService();  // LocationService to fetch locations
    private Association currentAssociation;
    private ViewEventController viewEventController;
    private Event eventToEdit; // null en mode "ajout"
    private boolean isEditMode = false;

    public AddEventController() throws SQLException {
    }

    public void setAssociation(Association association) {
        this.currentAssociation = association;
        loadLocations();  // Load locations in the ComboBox
    }

    private void loadLocations() {
        try {
            List<Location> locations = locationService.getAllLocations();
            locationComboBox.getItems().addAll(locations);

            // Set custom cell factory to display locations properly
            locationComboBox.setCellFactory(lv -> new ListCell<Location>() {
                @Override
                protected void updateItem(Location location, boolean empty) {
                    super.updateItem(location, empty);
                    setText(empty ? null : location.getName() + " (" + location.getCountry() + ")");
                }
            });

            // Set custom button cell to show the selected value properly
            locationComboBox.setButtonCell(new ListCell<Location>() {
                @Override
                protected void updateItem(Location location, boolean empty) {
                    super.updateItem(location, empty);
                    setText(empty ? null : location.getName() + " (" + location.getCountry() + ")");
                }
            });
        } catch (SQLException e) {
            showError("Error", "Failed to load locations.");
        }
    }
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(imagePathField.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Create a destination directory inside your project (ex: /images/)
                File destDir = new File("images");
                if (!destDir.exists()) {
                    destDir.mkdirs();  // create folder if it doesn't exist
                }

                // Create a destination file in the images folder
                File destFile = new File(destDir, selectedFile.getName());

                // If file with the same name already exists, make a unique name
                int count = 1;
                while (destFile.exists()) {
                    String name = selectedFile.getName();
                    int dotIndex = name.lastIndexOf('.');
                    String base = (dotIndex == -1) ? name : name.substring(0, dotIndex);
                    String extension = (dotIndex == -1) ? "" : name.substring(dotIndex);
                    destFile = new File(destDir, base + "_" + count + extension);
                    count++;
                }

                // Copy the file
                java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath());

                // Save relative path in the text field
                imagePathField.setText(destFile.getPath());

            } catch (Exception e) {
                showError("Image Upload Error", "Could not upload image: " + e.getMessage());
            }
        }
    }

    public void setEventToEdit(Event event) {
        this.eventToEdit = event;
        this.isEditMode = true;
        addButton.setText("Update");
        labelAdd.setText("➕ Update your Événement");

        // Préremplir les champs
        nameField.setText(event.getName());
        typeField.setText(event.getType());
        datePicker.setValue(event.getEventDate());
        priceField.setText(String.valueOf(event.getPrice()));
        imagePathField.setText(event.getImageFilename());

        // Set the location value
        if (event.getLocation() != null) {
            locationComboBox.getSelectionModel().select(event.getLocation());
        }
    }

    @FXML
    private void handleAjouterEvent() {
        try {
            // Collecting data from form fields
            String name = nameField.getText().trim();
            String type = typeField.getText().trim();
            LocalDate eventDate = datePicker.getValue();
            String priceText = priceField.getText().trim();
            Location location = locationComboBox.getValue();
            String imagePath = imagePathField.getText().trim();

            // Validate location
            if (location == null) {
                showError("Validation Error", "Veuillez sélectionner un lieu.");
                return;
            }

            // Validate image path
            if (imagePath.isEmpty()) {
                showError("Validation Error", "Veuillez choisir une image.");
                return;
            }
            if (name.isEmpty() || eventDate == null  || type.isEmpty()
                    || priceText.isEmpty() ) {
                showError("Missing Fields", "Please fill in all required fields.");
                return;
            }
            // Validate name
            if (name.length() < 5) {
                showError("Validation Error", "Le nom doit contenir au moins 5 caractères.");
                return;
            }
            // Validate type
            if (type.length() < 5) {
                showError("Validation Error", "Le type doit contenir au moins 5 caractères.");
                return;
            }
            // Validate date
            if (!eventDate.isAfter(LocalDate.now())) {
                showError("Validation Error", "Veuillez sélectionner une date future.");
                return;
            }
            // Validate price
            double price;
            try {
                price = Double.parseDouble(priceText);
                if (price <= 0) {
                    showError("Validation Error", "Le prix doit être supérieur à 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Validation Error", "Veuillez entrer un prix valide (nombre).");
                return;
            }

            // Create the event object
            Event event = new Event(currentAssociation, location, name, eventDate, price, type, imagePath);

            // Add the event to the database
            if (isEditMode) {
                // Set the ID to ensure update happens on the correct record
                event.setId(eventToEdit.getId());
                eventService.updateEvent(event);
                showInfo("Succès", "Événement mis à jour avec succès!");
            } else {
                eventService.addEvent(event);
                showInfo("Succès", "Événement ajouté avec succès!");
            }

            // Close the current window (Add Event page)
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

            // Notify the ViewEventController to reload events
            if (viewEventController != null) {
                viewEventController.loadEvents();
            }

        } catch (Exception e) {
            showError("Erreur", "Échec de l'ajout de l'événement: " + e.getMessage());
        }
    }



    // ✅ Helper Method: Display Error Alert
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

    // Setter for ViewEventController to notify after event is added
    public void setViewEventController(ViewEventController controller) {
        this.viewEventController = controller;
    }
}
