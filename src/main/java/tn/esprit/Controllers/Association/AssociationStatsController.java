package tn.esprit.Controllers.Association;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Association;
import tn.esprit.services.AssociationService;
import tn.esprit.services.EventService;
import tn.esprit.services.UserService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AssociationStatsController implements Initializable {

    // Charts
    @FXML private PieChart categoriesPieChart;
    @FXML private LineChart<String, Number> growthLineChart;
    @FXML private BarChart<String, Number> userDistributionChart;

    // Table
    @FXML private TableView<Association> topAssociationsTable;
    @FXML private TableColumn<Association, String> nameColumn;
    @FXML private TableColumn<Association, Integer> eventsColumn;
    @FXML private TableColumn<Association, Integer> membersColumn;

    // Stats card labels
    @FXML private Label totalAssociationsLabel;
    @FXML private Label usersWithAssociationsLabel;
    @FXML private Label popularCategoryLabel;
    @FXML private Label totalAssociationsNote;
    @FXML private Label usersWithAssociationsNote;
    @FXML private Label popularCategoryNote;
    @FXML private Label categoriesChartNote;
    @FXML private Label growthChartNote;
    @FXML private Label distributionChartNote;
    @FXML private Label topAssociationsNote;
    @FXML private Label lastUpdatedLabel;

    // Stats cards
    @FXML private VBox totalAssociationsCard;
    @FXML private VBox usersWithAssociationsCard;
    @FXML private VBox popularCategoriesCard;

    private final AssociationService associationService = new AssociationService();
    private final EventService eventService = new EventService();

    private final UserService userService = new UserService();

    public AssociationStatsController() throws SQLException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeAnimations();
        setupTableColumns();

        try {
            loadStatsCards();
            loadCategoriesDistribution();
            //loadGrowthTrends();
            //loadUserDistribution();
            loadTopAssociations();

            // Set last updated time
            lastUpdatedLabel.setText("Last updated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeAnimations() {
        // Add animation classes to elements
        totalAssociationsCard.getStyleClass().add("bounce-in");
        usersWithAssociationsCard.getStyleClass().add("bounce-in");
        popularCategoriesCard.getStyleClass().add("bounce-in");
        categoriesPieChart.getStyleClass().add("fade-in");
        growthLineChart.getStyleClass().add("fade-in");
        userDistributionChart.getStyleClass().add("slide-up");
        topAssociationsTable.getStyleClass().add("zoom-in");

        // Create staggered animation
        SequentialTransition seqTransition = new SequentialTransition(
                createCardAnimation(totalAssociationsCard),
                createCardAnimation(usersWithAssociationsCard),
                createCardAnimation(popularCategoriesCard),
                createChartAnimation(categoriesPieChart),
                createChartAnimation(growthLineChart),
                createChartAnimation(userDistributionChart),
                createTableAnimation(topAssociationsTable)
        );

        // Play animation after scene is shown
        Platform.runLater(seqTransition::play);
    }

    private Animation createCardAnimation(VBox card) {
        TranslateTransition translate = new TranslateTransition(Duration.millis(600), card);
        translate.setFromY(50);
        translate.setToY(0);

        FadeTransition fade = new FadeTransition(Duration.millis(600), card);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition parallel = new ParallelTransition(translate, fade);
        parallel.setOnFinished(e -> card.getStyleClass().add("show"));

        return parallel;
    }

    private Animation createChartAnimation(Node chart) {
        if (chart.getStyleClass().contains("fade-in")) {
            FadeTransition fade = new FadeTransition(Duration.millis(500), chart);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setOnFinished(e -> chart.getStyleClass().add("show"));
            return fade;
        } else {
            TranslateTransition translate = new TranslateTransition(Duration.millis(500), chart);
            translate.setFromY(20);
            translate.setToY(0);

            FadeTransition fade = new FadeTransition(Duration.millis(500), chart);
            fade.setFromValue(0);
            fade.setToValue(1);

            ParallelTransition parallel = new ParallelTransition(translate, fade);
            parallel.setOnFinished(e -> chart.getStyleClass().add("show"));
            return parallel;
        }
    }

    private Animation createTableAnimation(TableView<?> table) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), table);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1);
        scale.setToY(1);

        FadeTransition fade = new FadeTransition(Duration.millis(500), table);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition parallel = new ParallelTransition(scale, fade);
        parallel.setOnFinished(e -> table.getStyleClass().add("show"));
        return parallel;
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> {
            Association association = cellData.getValue();
            return new SimpleStringProperty(association.getName());
        });
        eventsColumn.setCellValueFactory(cellData -> cellData.getValue().eventCountProperty().asObject());
        membersColumn.setCellValueFactory(cellData -> cellData.getValue().memberCountProperty().asObject());
    }

    private void loadStatsCards() throws SQLException {
        int totalAssociations = associationService.countAssociations();
        int usersWithAssociations = userService.countUsersWithAssociations();
        String popularCategory = associationService.getMostPopularCategory();

        totalAssociationsLabel.setText(String.valueOf(totalAssociations));
        usersWithAssociationsLabel.setText(String.valueOf(usersWithAssociations));
        popularCategoryLabel.setText(popularCategory);

        totalAssociationsNote.setText(String.format("%.1f%% growth this month",
                eventService.getMonthlyGrowthRate()));
        usersWithAssociationsNote.setText(String.format("%.1f%% of total users",
                (usersWithAssociations * 100.0 / userService.countAllUsers())));
        popularCategoryNote.setText("Most common category");
    }

    private void loadCategoriesDistribution() throws SQLException {
        Map<String, Integer> categoryCounts = eventService.getCategoryDistribution();

        categoriesPieChart.setData(FXCollections.observableArrayList(
                categoryCounts.entrySet().stream()
                        .map(entry -> new PieChart.Data(
                                entry.getKey() + " (" + entry.getValue() + ")",
                                entry.getValue()
                        ))
                        .toList()
        ));

        categoriesChartNote.setText("Distribution across " + categoryCounts.size() + " categories");
        animatePieChart(categoriesPieChart);
    }

    private void animatePieChart(PieChart pieChart) {
        for (final PieChart.Data data : pieChart.getData()) {
            data.getNode().setOnMouseEntered(e -> {
                data.getNode().setEffect(new Glow(0.3));
                data.getNode().setScaleX(1.05);
                data.getNode().setScaleY(1.05);
            });

            data.getNode().setOnMouseExited(e -> {
                data.getNode().setEffect(null);
                data.getNode().setScaleX(1.0);
                data.getNode().setScaleY(1.0);
            });
        }
    }

    private void loadGrowthTrends() throws SQLException {
        Map<String, Integer> monthlyGrowth = associationService.getMonthlyGrowthData();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("New Associations");

        monthlyGrowth.forEach((month, count) ->
                series.getData().add(new XYChart.Data<>(month, count))
        );

        growthLineChart.getData().add(series);
        growthChartNote.setText("Association growth over the last 12 months");
        animateLineChart(growthLineChart);
    }

    private void animateLineChart(LineChart<String, Number> lineChart) {
        for (XYChart.Series<String, Number> series : lineChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setOnMouseEntered(e -> {
                        node.setEffect(new DropShadow(10, Color.web("#3b82f6")));
                        node.setScaleX(1.2);
                        node.setScaleY(1.2);
                    });
                    node.setOnMouseExited(e -> {
                        node.setEffect(null);
                        node.setScaleX(1.0);
                        node.setScaleY(1.0);
                    });
                }
            }
        }
    }

    private void loadUserDistribution() throws SQLException {
        Map<String, Integer> userDistribution = associationService.getUserDistribution();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Associations");

        userDistribution.forEach((userType, count) ->
                series.getData().add(new XYChart.Data<>(userType, count))
        );

        userDistributionChart.getData().add(series);
        distributionChartNote.setText("Association ownership by user type");
        animateBarChart(userDistributionChart);
    }

    private void animateBarChart(BarChart<String, Number> barChart) {
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                node.setOnMouseEntered(e -> {
                    node.setEffect(new DropShadow(10, Color.web("#3b82f6")));
                    node.setScaleX(1.05);
                    node.setScaleY(1.05);
                });
                node.setOnMouseExited(e -> {
                    node.setEffect(null);
                    node.setScaleX(1.0);
                    node.setScaleY(1.0);
                });
            }
        }
    }

    private void loadTopAssociations() throws Exception {
        List<Association> topAssociations = associationService.getTopAssociations(5);
        topAssociationsTable.setItems(FXCollections.observableArrayList(topAssociations));
        topAssociationsNote.setText("Top associations by activity and members");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) totalAssociationsCard.getScene().getWindow();
        stage.close();
    }
}