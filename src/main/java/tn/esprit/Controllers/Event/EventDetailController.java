package tn.esprit.Controllers.Event;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.entities.Event;

import java.io.File;
public class EventDetailController {

    @FXML
    private ImageView eventImage;

    @FXML
    private Label eventName;

    @FXML
    private Label eventType;

    @FXML
    private Label eventDate;

    @FXML
    private Label eventPrice;

    @FXML
    private Label eventLocation;

    private final String DEFAULT_IMAGE_PATH = System.getProperty("user.home") + "/EcodonImages/default-event.png";

    private Event event;

    public void setEvent(Event event) {
        this.event = event;

        eventName.setText("🎫 " + event.getName());
        eventType.setText("Type: " + event.getType());
        eventDate.setText("📅 " + event.getEventDate().toString());
        eventPrice.setText("💵 " + event.getPrice() + " TND");
        eventLocation.setText("📌 " + (event.getLocation() != null ? event.getLocation().getCountry() : "Unknown"));

        File file = new File(event.getImageFilename());
        Image image = file.exists() ? new Image(file.toURI().toString()) : new Image(new File(DEFAULT_IMAGE_PATH).toURI().toString());
        eventImage.setImage(image);
    }

    @FXML
    private void closePopup() {
        ((Stage) eventName.getScene().getWindow()).close();
    }
}
