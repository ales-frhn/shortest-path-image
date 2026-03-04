package fr.grapheimage.ui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Barre de contrôle contenant les boutons et sélecteurs pour configurer et
 * lancer les algorithmes.
 */
public class ControlPanel extends HBox {

    private final ComboBox<String> connectivitySelector;
    private final CheckBox showProgress;
    private final Slider speedSlider;
    private final Button btnPlay;
    private final Button btnReset;
    private final Button btnLoad;
    private final Label speedLabel;

    public ControlPanel() {
        super(15); // Espacement global
        this.getStyleClass().add("toolbar-panel");
        this.setAlignment(Pos.CENTER_LEFT);

        // Initialisation des composants
        connectivitySelector = new ComboBox<>();
        showProgress = new CheckBox("Afficher la progression");
        showProgress.setStyle("-fx-text-fill: white;");
        showProgress.setSelected(true); // Par défaut on voit tout
        speedSlider = new Slider(1, 100, 50);
        speedLabel = new Label("x50");
        btnPlay = new Button("Lecture");
        btnReset = new Button("Reset");
        btnLoad = new Button("Image");

        // --- GROUPE CONNEXITÉ ---
        connectivitySelector.setPrefWidth(120);
        VBox boxConn = createLabeledControl("Connexité :", connectivitySelector);

        // --- GROUPE AFFICHAGE ---
        VBox boxDisplay = createLabeledControl("Affichage :", showProgress);

        // --- GROUPE ACTIONS ---
        btnLoad.setGraphic(new Label("📂")); // Icône dossier

        // --- GROUPE LECTURE ---
        btnPlay.setGraphic(new Label("▶"));
        btnPlay.getStyleClass().add("btn-primary"); // Style spécifique pour bouton principal
        btnReset.setGraphic(new Label("⟳"));

        HBox playbackBtns = new HBox(10, btnPlay, btnReset);
        playbackBtns.setAlignment(Pos.CENTER_LEFT);

        // Vitesse d'animation
        HBox speedRow = new HBox(10, speedSlider, speedLabel);
        speedRow.setAlignment(Pos.CENTER_LEFT);
        VBox boxSpeed = new VBox(5, createSectionLabel("Vitesse :"), speedRow);
        boxSpeed.setAlignment(Pos.CENTER_LEFT);

        HBox playbackContent = new HBox(20, playbackBtns, boxSpeed);
        playbackContent.setAlignment(Pos.CENTER_LEFT);

        // Configuration des événements
        speedSlider.valueProperty().addListener((obs, old, val) -> {
            speedLabel.setText(String.format("x%d", val.intValue()));
        });
        speedLabel.setMinWidth(35);

        // Population de la liste déroulante
        connectivitySelector.getItems().addAll("4-voisins", "8-voisins");
        connectivitySelector.setValue("4-voisins");

        // Ajout à la mise en page principale
        this.getChildren().addAll(
                boxConn,
                createSeparator(),
                boxDisplay,
                createSeparator(),
                btnLoad,
                createSeparator(),
                playbackContent);
    }

    private VBox createLabeledControl(String title, Control node) {
        Label lbl = createSectionLabel(title);
        VBox box = new VBox(5, lbl, node);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Label createSectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("section-label");
        return lbl;
    }

    private Separator createSeparator() {
        Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep.setPrefHeight(40);
        return sep;
    }

    // Getters pour que le Contrôleur (VueImage) puisse s'abonner aux événements

    public Slider getSpeedSlider() {
        return speedSlider;
    }

    public Button getBtnPlay() {
        return btnPlay;
    }

    public Button getBtnReset() {
        return btnReset;
    }

    public Button getBtnLoad() {
        return btnLoad;
    }

    public ComboBox<String> getConnectivitySelector() {
        return connectivitySelector;
    }

    public CheckBox getShowProgressCheckbox() {
        return showProgress;
    }

}
