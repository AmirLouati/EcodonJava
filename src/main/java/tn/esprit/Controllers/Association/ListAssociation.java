package tn.esprit.Controllers.Association;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entities.Association;
import tn.esprit.services.AssociationService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListAssociation implements Initializable {
    @FXML
    public TextField SearchText;
    @FXML
    public Button SearchButton;
    @FXML
    private TableView<Association> associationTable;
    @FXML
    private TableColumn<Association, Integer> idColumn;
    @FXML
    private TableColumn<Association, String> nameColumn;
    @FXML
    private TableColumn<Association, String> descriptionColumn;
    @FXML
    private TableColumn<Association, String> logoColumn;
    private final AssociationService associationService = new AssociationService();

    public ListAssociation() throws SQLException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadAssociations();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading associations: " + e.getMessage());
        }
    }

    // Load associations into the TableView
    private void loadAssociations() throws Exception {
        List<Association> associations = associationService.getAllAssociations();
        ObservableList<Association> observableList = FXCollections.observableArrayList(associations);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Description"));

        // Configure the logo column to display images
        logoColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Association, String> call(TableColumn<Association, String> param) {
                return new TableCell<>() {
                    private final ImageView imageView = new ImageView();
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            // Load the image from the file path or URL
                            Image image = new Image("file:" + item, 50, 50, true, true); // Adjust size as needed
                            imageView.setImage(image);
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        logoColumn.setCellValueFactory(new PropertyValueFactory<>("Logo"));

        associationTable.setItems(observableList);
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/association/AddAssociation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Association");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait for the window to close

            // Refresh the TableView after adding
            loadAssociations();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Add Association window.", ButtonType.OK);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error refreshing data.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Update Button: Open a new window to update the selected association
    @FXML
    private void handleUpdateButton(ActionEvent event) {
        Association selectedAssociation = associationTable.getSelectionModel().getSelectedItem();
        if (selectedAssociation == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an association to update.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/association/UpdateAssociation.fxml"));
            Parent root = loader.load();

            // Pass the selected association to the UpdateAssociation controller
            UpdateAssociationController updateController = loader.getController();
            updateController.setAssociation(selectedAssociation);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Association");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait for the window to close

            // Refresh the TableView after updating
            loadAssociations();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open Update Association window.", ButtonType.OK);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error refreshing data.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Delete Button: Delete the selected association
    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Association selectedAssociation = associationTable.getSelectionModel().getSelectedItem();
        if (selectedAssociation == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an association to delete.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Confirm deletion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this association?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                associationService.deleteAssociation(selectedAssociation.getId());
                loadAssociations(); // Refresh the TableView
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete association.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = SearchText.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            try {
                loadAssociations(); // Reload all associations
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<Association> associations = associationService.getAllAssociations();
                ObservableList<Association> filteredList = FXCollections.observableArrayList();
                for (Association association : associations) {
                    if (association.getName() != null && association.getName().toLowerCase().contains(searchText)) {
                        filteredList.add(association);
                    }
                }
                associationTable.setItems(filteredList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}