package tn.esprit.Controllers;


import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import tn.esprit.API.GeminiAPI;

public class ChatbotController {

    @FXML private TextArea chatArea;
    @FXML private TextField userInputField;

    @FXML
    public void initialize() {
        chatArea.setEditable(false);
        chatArea.appendText("Bot: Hello! How can I assist you today?\n");
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        String userInput = userInputField.getText().trim();
        if (!userInput.isEmpty()) {
            chatArea.appendText("You: " + userInput + "\n");

            // Get response from Gemini API
            String response = GeminiAPI.sendToGemini(userInput);
            chatArea.appendText("Bot: " + response + "\n");

            userInputField.clear();
        }
    }
}
