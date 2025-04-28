package tn.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class DashboardApp extends Application {

    @Override
    public void start(Stage stage) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/views/Home.fxml"));
            Scene scene = new Scene(root, 950, 700);
            stage.setTitle("Ecodon");
            Image icon = new Image(getClass().getResourceAsStream("/Images/ecodon.png"));
            stage.getIcons().add(icon);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
