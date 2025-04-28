package tn.esprit.Controllers.Event;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Donation;
import tn.esprit.entities.Event;
import tn.esprit.entities.User;
import tn.esprit.services.DonationService;
import tn.esprit.services.MailService;
import tn.esprit.services.UserService;

import java.time.LocalDateTime;

public class EventDonationController {

    @FXML private Label eventTitleLabel;
    @FXML private TextField donationAmountField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField countryField;
    @FXML private TextField addressField;
    @FXML private TextField apartmentField;
    @FXML private TextField townField;
    @FXML private TextField stateField;
    @FXML private TextField postalCodeField;
    @FXML private TextArea orderNotesArea;
    @FXML private ComboBox<String> donationTypeComboBox;
    @FXML private ComboBox<String> paymentMethodComboBox;
    @FXML private Button donateButton;

    private Event currentEvent;
    private User currentUser;
    private final DonationService donationService = new DonationService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() throws Exception {
        // Initialize combo boxes
        donationTypeComboBox.getItems().addAll("One-time", "Monthly", "Annual");
        paymentMethodComboBox.getItems().addAll("Credit Card", "PayPal", "Bank Transfer");

        // Load current user (you need to implement this based on your auth system)
        currentUser = userService.getUserById(6); // Implement this method in UserService
    }

    public void setEvent(Event event) {
        currentEvent = event;
        eventTitleLabel.setText("Donate to: " + event.getName());
    }

    @FXML
    private void handleDonate(ActionEvent event) {
        // Validate required fields
        if (!validateFields()) {
            return;
        }

        try {
            // Create donation object
            Donation donation = new Donation();
            donation.setEventId(currentEvent.getId());
            donation.setUserId(currentUser != null ? currentUser.getId() : 0); // 0 for anonymous
            donation.setFirstName(firstNameField.getText());
            donation.setLastName(lastNameField.getText());
            donation.setEmail(emailField.getText());
            donation.setPhone(phoneField.getText());
            donation.setCountry(countryField.getText());
            donation.setAddress(addressField.getText());
            donation.setApartment(apartmentField.getText());
            donation.setTown(townField.getText());
            donation.setState(stateField.getText());
            donation.setPostalCode(postalCodeField.getText());
            donation.setOrderNotes(orderNotesArea.getText());
            donation.setDonationAmount(Double.parseDouble(donationAmountField.getText()));
            donation.setDonationType(donationTypeComboBox.getValue());
            donation.setPaymentMethod(paymentMethodComboBox.getValue());
            donation.setCreatedAt(LocalDateTime.now());

            // Process payment (you would integrate with Stripe/PayPal here)
            boolean paymentSuccess = processPayment(donation);
            String email ="azizrihani.pro@gmail.com";
            String object ="Donation seccessfull";
            String message="Thank you for your donation for the event"+currentEvent.getName()
                    +"was successfully made on"+ donation.getCreatedAt().toLocalDate().toString()
                    +"for the amount"+donation.getDonationAmount();
            if (paymentSuccess) {
                // Save donation to database
                donationService.addDonation(donation);
                // send mail
                MailService mailService = new MailService();
                mailService.sendEmail(
                        email,
                        object,
                        message
                );
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Thank You",
                        "Your donation of " + donation.getDonationAmount() + " TND has been processed successfully!");

                // Close the window
                ((Stage) donateButton.getScene().getWindow()).close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Payment Failed",
                        "There was an issue processing your payment. Please try again.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid donation amount.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (firstNameField.getText().isEmpty()) {
            errors.append("- First name is required\n");
        }
        if (lastNameField.getText().isEmpty()) {
            errors.append("- Last name is required\n");
        }
        if (emailField.getText().isEmpty()) {
            errors.append("- Email is required\n");
        }
        if (donationAmountField.getText().isEmpty()) {
            errors.append("- Donation amount is required\n");
        } else {
            try {
                double amount = Double.parseDouble(donationAmountField.getText());
                if (amount <= 0) {
                    errors.append("- Donation amount must be positive\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Invalid donation amount format\n");
            }
        }
        if (paymentMethodComboBox.getValue() == null) {
            errors.append("- Payment method is required\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please fix the following errors:\n\n" + errors.toString());
            return false;
        }

        return true;
    }

    private boolean processPayment(Donation donation) {
        // In a real application, you would integrate with Stripe/PayPal here
        // For now, we'll just simulate a successful payment
        return true;
    }

    @FXML
    private void closePopup() {
        ((Stage) eventTitleLabel.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}