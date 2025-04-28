package tn.esprit.Controllers.Event;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.entities.User;
import tn.esprit.services.DonationService;
import tn.esprit.services.EventService;
import tn.esprit.services.UserService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class EventCell extends ListCell<Event> {

    private VBox content;
    private ImageView imageView;
    private Text eventName;
    private Label dateLabel;
    private Label typeLabel;
    private Label priceLabel;
    private Label locationLabel;
    private Button btnDetails;
    private Button btnDonate;
    private Button btnEdit;
    private Button btnDelete;
    private static final String DEFAULT_IMAGE_PATH = System.getProperty("user.home") + "/EcodonImages/default-event.png";
    private Event currentEvent;
    private EventService eventService = new EventService();
    private UserService userService = new UserService();
    private DonationService donationService = new DonationService();
    private ViewEventController viewEventController;

    public EventCell(ViewEventController controller) throws SQLException {

        viewEventController = controller;

        imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 4, 4);");

        eventName = new Text();
        eventName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-fill: #2C3E50;");

        typeLabel = new Label();
        typeLabel.setStyle("-fx-background-color: #8E44AD; -fx-text-fill: white; -fx-padding: 3px 8px; -fx-background-radius: 10px;");

        dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2C3E50;");

        priceLabel = new Label();
        priceLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #27AE60; -fx-font-weight: bold;");

        locationLabel = new Label();
        locationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        btnDetails = new Button("📍 View Details");
        btnDetails.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 10px;");
        btnDetails.setOnAction(e -> handleViewDetails());

        btnDonate = new Button("💚 Make a Donation");
        btnDonate.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-background-radius: 10px;");
        btnDonate.setOnAction(e -> handleDonation());

        btnEdit = new Button("✏️ Edit");
        btnEdit.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-background-radius: 10px;");
        btnEdit.setOnAction(e -> handleEditEvent());

        btnDelete = new Button("🗑️ Delete");
        btnDelete.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 10px;");
        btnDelete.setOnAction(e -> handleDeleteEvent());

        content = new VBox(8, eventName, imageView, typeLabel, dateLabel, priceLabel, locationLabel, btnDetails, btnDonate, btnEdit, btnDelete);
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 5, 5);");
    }

    @Override
    protected void updateItem(Event event, boolean empty) {
        super.updateItem(event, empty);

        if (empty || event == null) {
            setGraphic(null);
        } else {
            this.currentEvent = event;
            eventName.setText("🎫 " + event.getName());
            typeLabel.setText("Type: " + event.getType());
            dateLabel.setText("📅 Date: " + event.getEventDate().toString());
            priceLabel.setText("💵 Price: " + event.getPrice() + " TND");
            locationLabel.setText("📌 Location: " + (event.getLocation() != null ? event.getLocation().getCountry() : "Unknown"));

            String imagePath = event.getImageFilename();
            Image image = loadImage(imagePath);
            imageView.setImage(image);

            // Fetch current user (you should replace the user ID with the actual logged-in user)
            User currentUser;
            try {
                currentUser = userService.getUserById(6);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Check if the current user is associated with the event's association
            if (currentUser.getRoles().contains("ROLE_ASSOCIATION") && currentUser.getId() == event.getAssociation().getUser().getId()) {
                // If the user is the creator of the event (associated with the event's association), show the Edit and Delete buttons
                System.out.println("dddd"+getItem().getAssociation().getUser().getId());
                btnEdit.setVisible(true);
                btnDelete.setVisible(true);
            } else {
                // Otherwise, hide the Edit and Delete buttons
                btnEdit.setVisible(false);
                btnDelete.setVisible(false);
            }

            if (donationService.hasUserDonatedToEvent(currentUser.getId(), currentEvent.getId())) {
                btnDonate.setText("💚 Make Another Donation");
            } else {
                btnDonate.setText("💚 Make a Donation");
            }


            setGraphic(content);
        }
    }

    private void handleViewDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Event/EventDetail.fxml"));
            Parent root = loader.load();

            EventDetailController controller = loader.getController();
            controller.setEvent(currentEvent);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Event Details");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); // Make it modal
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDonation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Event/EventDonation.fxml"));
            Parent root = loader.load();

            EventDonationController controller = loader.getController();
            controller.setEvent(currentEvent);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Make a Donation");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); // Prevent interacting with the background
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEditEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Event/AddEvent.fxml")); // ✅ load AddEvent.fxml
            Parent root = loader.load();

            AddEventController controller = loader.getController();
            controller.setAssociation(currentEvent.getAssociation());
            controller.setEventToEdit(currentEvent);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Event");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            viewEventController.loadEvents(); // ✅ refresh list

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleDeleteEvent() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this event?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    eventService.deleteEvent(currentEvent.getId());
                    showInfo("Deleted", "Event deleted successfully.");
                    viewEventController.loadEvents(); // ✅ reload list
                } catch (Exception ex) {
                    showError("Error", "Failed to delete the event: " + ex.getMessage());
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

    private void refreshEventList() {
        // Assuming you have a method to refresh the event list
        // For example: getParentController().loadEvents();
    }

    private Image loadImage(String imagePath) {
        File file = new File(imagePath);
        return file.exists() ? new Image(file.toURI().toString()) : new Image("file:" + DEFAULT_IMAGE_PATH);
    }
}
