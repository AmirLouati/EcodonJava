package tn.esprit.Controllers.Cours;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.API.GeminiAPI;
import tn.esprit.entities.Cours;
import tn.esprit.services.CoursService;

import java.io.IOException;
import java.sql.SQLException;

public class CourseCardController {

    @FXML private Label courseNameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label formationLabel;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button generateQuizButton;

    private Cours course;
    private final CoursService coursService = new CoursService();

    private CoursController coursController; // ➡️ reference to parent controller

    // This setter will allow CoursController to set itself
    public void setCoursController(CoursController controller) {
        this.coursController = controller;
    }

    public void setCourseData(Cours course) {
        this.course = course;

        courseNameLabel.setText(course.getCour());
        descriptionLabel.setText(course.getDescription());
        formationLabel.setText(course.getFormation() != null ? course.getFormation().getTitre() : "No Formation");

        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
        generateQuizButton.setOnAction(event -> handleGenerateQuiz());
    }

    @FXML
    private void handleEdit() {
        System.out.println("Modifier le cours: " + course.getCour());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/cours/UpdateCours.fxml"));
            Parent root = loader.load();

            UpdateCoursController updateController = loader.getController();
            updateController.setCourse(course);

            // Open update window modally
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
            stage.setTitle("Modifier le Cours");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait until the update window is closed

            // After closing the update window, reload the courses
            if (coursController != null) {
                coursController.loadCourses();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open the update window.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Course");
        alert.setHeaderText("Are you sure you want to delete this course?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().get().getButtonData().isDefaultButton()) {
            try {
                coursService.deleteCours(course.getId());

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Course Deleted");
                successAlert.setHeaderText("The course has been deleted.");
                successAlert.showAndWait();

                // After successful delete, reload the course list
                if (coursController != null) {
                    coursController.loadCourses();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Failed to delete course.");
                errorAlert.showAndWait();
            }
        }
    }

    private void handleGenerateQuiz() {
        String courseTitle = course.getCour();
        String prompt = "Generate a simple quiz with 3 multiple-choice questions based on the topic: " + courseTitle;

        String quiz = GeminiAPI.sendToGemini(prompt);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Généré");
        alert.setHeaderText("Quiz basé sur le cours: " + courseTitle);
        alert.setContentText(quiz);
        alert.showAndWait();
    }
}
