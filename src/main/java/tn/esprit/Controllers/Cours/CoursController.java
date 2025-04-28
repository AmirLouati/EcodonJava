package tn.esprit.Controllers.Cours;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import com.lowagie.text.Document;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.Controllers.SharedData;
import tn.esprit.entities.Cours;
import tn.esprit.entities.Formation;
import tn.esprit.entities.User;
import tn.esprit.services.AssociationService;
import tn.esprit.services.CoursService;
import tn.esprit.services.FormationService;
import tn.esprit.services.UserService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CoursController {
    @FXML
    private Button PDFbutton;

    @FXML
    private FlowPane courseCardsContainer;

    @FXML
    private TextField searchField;

    private UserService userService = new UserService();
    private User currentUser;
    private FormationService form = new FormationService();
    private Formation formation;

    {
        try {
            currentUser = userService.getUserById(6);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final CoursService coursService = new CoursService();
    private ObservableList<Cours> coursList = FXCollections.observableArrayList();

    public void initialize() throws Exception {
        formation = form.getFormationById(SharedData.getFormationid());

        // Load all courses into the FlowPane
        loadCourses();

        // Add a listener to the search field for dynamic search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleDynamicSearch(newValue.trim());
        });
    }

    // Load all courses from the database
    public void loadCourses() {
        try {
            courseCardsContainer.getChildren().clear(); // Clear existing cards
            List<Cours> courses = coursService.getAllCoursByFormation(formation);

            for (Cours course : courses) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/cours/CourseCard.fxml"));
                VBox card = loader.load();

                CourseCardController controller = loader.getController();
                controller.setCourseData(course);
                controller.setCoursController(this); // ➡️ set the parent controller


                courseCardsContainer.getChildren().add(card); // Add the card to the container
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load courses.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Handle dynamic search
    private void handleDynamicSearch(String query) {
        try {
            courseCardsContainer.getChildren().clear(); // Clear existing cards
            List<Cours> filteredCourses;

            if (query.isEmpty()) {
                filteredCourses = coursService.getAllCoursByFormation(formation); // Reload all courses
            } else {
                filteredCourses = coursService.searchCourses(query); // Filter courses
            }

            for (Cours course : filteredCourses) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/cours/CourseCard.fxml"));
                VBox card = loader.load();

                CourseCardController controller = loader.getController();
                controller.setCourseData(course);
                controller.setCoursController(this); // ➡️ set the parent controller


                courseCardsContainer.getChildren().add(card); // Add the card to the container
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to search courses.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Handle Add Button
    @FXML
    private void handleAddButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/cours/AddCours.fxml"));
            Parent root = loader.load();

            AddCoursController coursController = loader.getController();
            AssociationService associationService = new AssociationService();
            coursController.setCurrentAssociation(associationService.getAssociationByUserId(currentUser.getId()));

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Course");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadCourses(); // Refresh courses after adding
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Add Course window.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Handle Update Button
    @FXML
    private void handleUpdateButton(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a course to update.", ButtonType.OK);
        alert.showAndWait();
    }

    // Handle Delete Button
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a course to delete.", ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    void handlePDFbutton(ActionEvent event) {
        try {
            // Let user choose save location
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Courses PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("courses_list.pdf");

            File file = fileChooser.showSaveDialog(PDFbutton.getScene().getWindow());

            if (file != null) {
                // Get the list of courses
                List<Cours> courses = coursService.getAllCoursByFormation(formation);

                // Generate the PDF
                generateCoursPdfByFormation(formation, courses, file.getAbsolutePath());

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "PDF generated successfully!", ButtonType.OK);
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to generate PDF.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // This method should be inside the controller (CoursController)
    private void generateCoursPdfByFormation(Formation formation, List<Cours> coursList, String filePath) {
        Document document = new Document();

        try {
            // Create a PDF writer instance and link it to the output file
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add some title or header to the PDF
            document.add(new Paragraph("Courses for Formation: " + formation.getTitre()));
            document.add(new Paragraph(" ")); // Empty line for spacing

            // Add course details to the PDF
            for (Cours cours : coursList) {
                document.add(new Paragraph("Course: " + cours.getCour()));
                document.add(new Paragraph("Description: " + cours.getDescription()));
                document.add(new Paragraph(" ")); // Empty line between courses
            }

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            // Close the document after writing
            document.close();
        }
    }


    public void handleSearch(ActionEvent actionEvent) {
    }
}