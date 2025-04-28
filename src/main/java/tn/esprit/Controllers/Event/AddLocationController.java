package tn.esprit.Controllers.Event;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Location;
import tn.esprit.services.GeocodingService;
import tn.esprit.services.LocationService;
import javafx.scene.control.Alert;

import java.sql.SQLException;

public class AddLocationController {
    @FXML private TextField nameField;
    @FXML private TextField countryField;
    @FXML private TextField latField;
    @FXML private TextField longField;
    @FXML private MapView mapView;

    private Marker markerClick;
    private static final int ZOOM_DEFAULT = 8;
    private final Coordinate centerCoordinate = new Coordinate(36.898871, 10.187932);

    private final LocationService locationService = new LocationService();
    private final GeocodingService geocodingService = new GeocodingService();

    public AddLocationController() throws SQLException {
    }

    @FXML
    public void initialize() {
        // Initialize map
        mapView.setCustomMapviewCssURL(getClass().getResource("/custom_mapview.css"));

        // Create marker for clicks
        markerClick = Marker.createProvided(Marker.Provided.BLUE)
                .setVisible(true)
                .setPosition(centerCoordinate);

        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mapView.setZoom(ZOOM_DEFAULT);
                mapView.setCenter(centerCoordinate);
                mapView.addMarker(markerClick);
            }
        });

        setupEventHandlers();

        // Initialize the map view
        mapView.initialize(Configuration.builder()
                .projection(Projection.WEB_MERCATOR)
                .showZoomControls(false)
                .build());
    }

    private void setupEventHandlers() {
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
            final Coordinate newPosition = event.getCoordinate().normalize();

            // Update marker position
            markerClick.setPosition(newPosition);

            // Update coordinate fields
            latField.setText(String.format("%.6f", newPosition.getLatitude()));
            longField.setText(String.format("%.6f", newPosition.getLongitude()));

            // Reverse geocode to get country
            updateCountryFromCoordinates(newPosition);
        });
    }

    private void updateCountryFromCoordinates(Coordinate coordinate) {
        new Thread(() -> {
            try {
                String country = geocodingService.getCountryFromCoordinates(
                        coordinate.getLatitude(),
                        coordinate.getLongitude()
                );

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    countryField.setText(country);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    countryField.setText("Unknown");
                });
            }
        }).start();
    }

    @FXML
    private void handleSaveLocation() {
        try {
            // Validate inputs
            if (nameField.getText().isEmpty() || countryField.getText().isEmpty() ||
                    latField.getText().isEmpty() || longField.getText().isEmpty()) {
                showAlert("Validation Error", "Please select a location on the map and provide a name", Alert.AlertType.ERROR);
                return;
            }

            // Create new location
            Location location = new Location();
            location.setName(nameField.getText());
            location.setCountry(countryField.getText());
            location.setLatitude(Double.parseDouble(latField.getText()));
            location.setLongitude(Double.parseDouble(longField.getText()));

            // Save to database
            locationService.addLocation(location);

            showAlert("Success", "Location saved successfully", Alert.AlertType.INFORMATION);
            closeWindow();

        } catch (Exception e) {
            showAlert("Error", "Failed to save location: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}