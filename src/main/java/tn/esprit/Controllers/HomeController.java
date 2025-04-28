package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import tn.esprit.entities.User;
import tn.esprit.services.AssociationService;
import tn.esprit.services.UserService;

import java.io.IOException;

public class HomeController {
    @FXML private BorderPane borderPane;
    @FXML private Button dashboardButton;
    @FXML private Label userRoleLabel;

    private String associationFxml;
    private UserService userService = new UserService();

    @FXML
    public void initialize() throws Exception {
        int userId = 5;
        User user = userService.getUserById(userId);

        String realRole = "User"; // Default role

        if (user.getRoles().contains("ROLE_ADMIN")) {
            realRole = "Admin";
        }

        AssociationService associationService = new AssociationService();

        if (realRole.equals("Admin")) {
            dashboardButton.setText("Dashboard");
            associationFxml = "/views/Association/ListAssociation.fxml";
            userRoleLabel.setText("ADMIN");
        } else {
            boolean isInAssociation = associationService.isUserInAssociation(userId);
            if (isInAssociation) {
                realRole = "Association";
            }
            dashboardButton.setText("Home");
            associationFxml = "/views/Association/ViewAssociation.fxml";
            userRoleLabel.setText(realRole.toUpperCase());
        }
    }
    @FXML
    private void loadAssociation() {
        loadScene(associationFxml);
    }

    @FXML
    private void loadFormation() {
        loadScene("/views/Formation/ListFormation.fxml");
    }
    private void loadScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root,950,800);
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
