package fr.grapheimage.ui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Composant UI affichant l'historique des exécutions dans un tableau.
 */
public class StatsTable extends VBox {

    private final TableView<ExecutionRecord> table;
    private final ObservableList<ExecutionRecord> data;

    public StatsTable() {
        super(3);

        data = FXCollections.observableArrayList();

        this.setPadding(new Insets(5));
        this.getStyleClass().add("card-panel");

        Label title = new Label("📊 Historique des exécutions");
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button clearBtn = new Button("Effacer");
        clearBtn.setOnAction(e -> data.clear());

        HBox header = new HBox(10, title, new Region(), clearBtn);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS); // Spacer

        table = new TableView<ExecutionRecord>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(35.0); // Fixe la hauteur des lignes pour la performance
        VBox.setVgrow(table, Priority.ALWAYS);

        createColumns();

        this.getChildren().addAll(header, table);
    }

    private void createColumns() {
        TableColumn<ExecutionRecord, Integer> colId = new TableColumn<>("#");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ExecutionRecord, String> colConn = new TableColumn<>("Connexité");
        colConn.setCellValueFactory(new PropertyValueFactory<>("connectivity"));

        TableColumn<ExecutionRecord, String> colVisit = new TableColumn<>("Nœuds visités");
        colVisit.setCellValueFactory(new PropertyValueFactory<>("visited"));

        TableColumn<ExecutionRecord, Integer> colPath = new TableColumn<>("Taille du chemin obtenu");
        colPath.setCellValueFactory(new PropertyValueFactory<>("pathSize"));

        TableColumn<ExecutionRecord, Double> colCost = new TableColumn<>("Coût");
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colCost.setCellFactory(tc -> new TableCell<ExecutionRecord, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        table.getColumns().addAll(colId, colConn, colVisit, colPath, colCost);
    }

    public void addRecord(String connectivity, int visited, int totalNodes, int pathSize,
            double cost) {
        int id = data.size() + 1;
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(java.util.Locale.FRENCH);
        String visitedStr = String.format("%s sur %s", formatter.format(visited), formatter.format(totalNodes));
        data.add(new ExecutionRecord(id, connectivity, visitedStr, pathSize, cost));
    }

    public static class ExecutionRecord {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty connectivity;
        private final SimpleStringProperty visited;
        private final SimpleIntegerProperty pathSize;
        private final SimpleDoubleProperty cost;

        public ExecutionRecord(int id, String connectivity, String visited, int pathSize,
                double cost) {
            this.id = new SimpleIntegerProperty(id);
            this.connectivity = new SimpleStringProperty(connectivity);
            this.visited = new SimpleStringProperty(visited);
            this.pathSize = new SimpleIntegerProperty(pathSize);
            this.cost = new SimpleDoubleProperty(cost);
        }

        public int getId() {
            return id.get();
        }

        public String getConnectivity() {
            return connectivity.get();
        }

        public String getVisited() {
            return visited.get();
        }

        public int getPathSize() {
            return pathSize.get();
        }

        public double getCost() {
            return cost.get();
        }
    }
}
