package fr.grapheimage.ui;

import fr.grapheimage.algorithmes.*;
import fr.grapheimage.graphe.ConstructeurGraphe;
import fr.grapheimage.graphe.Graphe;
import fr.grapheimage.image.ChargeurImage;
import fr.grapheimage.image.MatriceNiveauxDeGris;
import javafx.animation.AnimationTimer;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import java.io.File;

/**
 * Vue principale de l'application.
 * Gestion de l'affichage de l'image, des interactions utilisateur (clics)
 * et de l'orchestration des algorithmes de recherche.
 */
public class VueImage extends BorderPane {

    private final ImageView imageView;
    private int[] depart = null;
    private int[] arrivee = null;
    private Image img;
    private Graphe g;
    private WritableImage imageBuffer;
    private PixelWriter writer;

    // Composants UI
    private final ControlPanel controlPanel;
    private final StatsTable statsTable;
    private final javafx.scene.control.Label placeholderLabel;

    // Moteur de recherche et Animation
    private GraphSearchEngine engine;
    private AnimationTimer timer;
    private boolean isPlaying = false;

    // Stats temporaires
    private String lastConnectivity;
    private double pendingCost;

    public VueImage() {
        // Initialisation des composants
        controlPanel = new ControlPanel();
        statsTable = new StatsTable();
        imageView = new ImageView();
        imageView.setPreserveRatio(true);

        this.setTop(controlPanel);

        // Zone centrale : SplitPane pour Image (Haut) et Stats (Bas)
        javafx.scene.control.SplitPane splitPane = new javafx.scene.control.SplitPane();
        splitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // Conteneur de l'image (StackPane pour centrage)
        javafx.scene.layout.StackPane imageContainer = new javafx.scene.layout.StackPane();
        imageContainer.setStyle(
                "-fx-background-color: #1a1a1a; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");

        // Label de placeholder pour quand aucune image n'est chargée
        placeholderLabel = new javafx.scene.control.Label("📂 Veuillez charger une image");
        placeholderLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #888888;");
        placeholderLabel.setVisible(true);

        imageContainer.getChildren().addAll(placeholderLabel, imageView);

        // Liaison de la taille de l'image au conteneur
        imageView.fitWidthProperty().bind(imageContainer.widthProperty());
        imageView.fitHeightProperty().bind(imageContainer.heightProperty());

        // Permettre aux conteneurs de rétrécir librement
        imageContainer.setMinHeight(0);
        imageContainer.setMinWidth(0);
        statsTable.setMinHeight(0);
        statsTable.setMinWidth(0);

        splitPane.getItems().addAll(imageContainer, statsTable);
        splitPane.setDividerPositions(0.55); // 55% Image, 45% Stats

        this.setCenter(splitPane);

        // Chargement du CSS
        this.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());

        // Gestionnaires d'événements
        imageView.setOnMouseClicked(this::gererClic);

        controlPanel.getBtnLoad().setOnAction(e -> chargerImage());
        controlPanel.getBtnReset().setOnAction(e -> resetAll());
        controlPanel.getBtnPlay().setOnAction(e -> togglePlay());

        controlPanel.getConnectivitySelector().valueProperty().addListener((obs, oldV, newV) -> {
            reconstruireGraphe();
            resetAll();
        });

        controlPanel.getShowProgressCheckbox().selectedProperty().addListener((obs, oldV, newV) -> {
            refreshDisplay();
        });

        // Image par défaut
        Image defImg = ChargeurImage.charger("images/batman.png");
        if (defImg == null || defImg.isError()) {
            // Fallback si l'image par défaut n'existe pas (évite le crash au démarrage si
            // resource manquante)
            // Le placeholder reste visible
        } else {
            chargerNouvelleImage(defImg);
        }

        setupTimer();
    }

    private void chargerImage() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File f = fc.showOpenDialog(null);
        if (f != null) {
            chargerNouvelleImage(new Image(f.toURI().toString()));
        }
    }

    private int currentTotalNodes;

    private void chargerNouvelleImage(Image newImg) {
        if (newImg == null)
            return;
        this.img = newImg;
        imageView.setImage(this.img);

        // Cacher le placeholder quand une image est chargée
        placeholderLabel.setVisible(false);

        // Mise à jour du compteur de nœuds
        this.currentTotalNodes = (int) (img.getWidth() * img.getHeight());

        depart = null;
        arrivee = null;
        reconstruireGraphe();
        resetSearchdState();
    }

    private void reconstruireGraphe() {
        if (img == null)
            return;
        int[][] gris = MatriceNiveauxDeGris.convertir(img);

        String selectedConn = controlPanel.getConnectivitySelector().getValue();
        ConstructeurGraphe.ModeConnexite mode = "4-voisins".equals(selectedConn)
                ? ConstructeurGraphe.ModeConnexite.CONNEXITE4
                : ConstructeurGraphe.ModeConnexite.CONNEXITE8;

        g = ConstructeurGraphe.construire(gris, mode);
    }

    private void resetAll() {
        resetSearchdState();
        depart = null;
        arrivee = null;
        redessinerImageBase();
    }

    private void resetSearchdState() {
        engine = null;
        isPlaying = false;
        controlPanel.getBtnPlay().setText("▶ Lecture");
        controlPanel.getBtnPlay().setDisable(true);
        if (timer != null) {
            timer.stop();
        }
        // Nettoyage du buffer visuel
        if (img != null) {
            redessinerImageBase();
        }
    }

    private void redessinerImageBase() {
        if (img == null)
            return;

        // Réaffichage de l'image de base
        imageView.setImage(img);

        // Création du buffer pour dessiner par-dessus
        imageBuffer = new WritableImage(img.getPixelReader(), (int) img.getWidth(), (int) img.getHeight());
        writer = imageBuffer.getPixelWriter();
        imageView.setImage(imageBuffer);

        if (depart != null) {
            drawPoint(depart[0], depart[1], 0xFFFF0000); // Rouge (Départ)
        }
        if (arrivee != null) {
            drawPoint(arrivee[0], arrivee[1], 0xFF00FF00); // Vert (Arrivée)
        }
    }

    private void gererClic(MouseEvent event) {
        if (img == null)
            return;

        // Mapping des coordonnées (Vue -> Image)
        double vW = imageView.getBoundsInLocal().getWidth();
        double vH = imageView.getBoundsInLocal().getHeight();
        double iW = img.getWidth();
        double iH = img.getHeight();

        // Calcul de l'échelle (fit)
        double scaleX = vW / iW;
        double scaleY = vH / iH;
        double scaleFactor = Math.min(scaleX, scaleY);

        double actualW = iW * scaleFactor;
        double actualH = iH * scaleFactor;

        double offX = (vW - actualW) / 2;
        double offY = (vH - actualH) / 2;

        double clickX = event.getX() - offX;
        double clickY = event.getY() - offY;

        // Clic hors image
        if (clickX < 0 || clickY < 0 || clickX >= actualW || clickY >= actualH)
            return;

        int px = (int) (clickX / scaleFactor);
        int py = (int) (clickY / scaleFactor);

        if (depart == null) {
            depart = new int[] { py, px };
        } else if (arrivee == null) {
            arrivee = new int[] { py, px };
            controlPanel.getBtnPlay().setDisable(false);
        } else {
            // 3ème clic : Reset et nouveau départ
            depart = new int[] { py, px };
            arrivee = null;
            resetSearchdState();
        }
        redessinerImageBase();
    }

    private void drawPoint(int y, int x, int color) {
        // Dessine un carré 5x5
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                int ty = y + i;
                int tx = x + j;
                if (ty >= 0 && ty < img.getHeight() && tx >= 0 && tx < img.getWidth()) {
                    writer.setArgb(tx, ty, color);
                }
            }
        }
    }

    /**
     * Redessine l'image complète en fonction de l'historique et des options.
     */
    private void refreshDisplay() {
        if (engine == null || img == null)
            return;

        // 1. Reset fond
        redessinerImageBase();

        // 2. Redessiner l'état courant
        boolean showProgress = controlPanel.getShowProgressCheckbox().isSelected();
        int maxStep = engine.isFinished() ? engine.getTotalSteps() : engine.getCurrentStep();

        // Optimisation : accès direct au pixel writer pour tout redessiner
        // On parcourt l'historique
        for (int k = 0; k < maxStep; k++) {
            int nodeId = engine.getHistoryNode(k);
            if (nodeId == -1)
                continue;

            boolean isPath = engine.isPathStep(k);

            // Logique d'affichage
            // Si c'est un chemin (Rouge) -> Toujours afficher
            // Si c'est visité (Bleu) -> Afficher seulement si checkbox cochée

            if (isPath) {
                drawPixelNode(nodeId, 0xFFFF0000);
            } else if (showProgress) {
                drawPixelNode(nodeId, 0x404A9EFF); // Bleu semi-transparent
            }
        }
    }

    private void drawPixelNode(int nodeId, int color) {
        int y = nodeId / (int) img.getWidth();
        int x = nodeId % (int) img.getWidth();

        // Gestion de la transparence manuel si nécessaire, ou setArgb direct
        if ((color >> 24) != 0xFF) {
            setPixelWithAlpha(x, y, color);
        } else {
            writer.setArgb(x, y, color);
        }
    }

    /**
     * Applique une couleur avec transparence (Alpha Blending).
     */
    private void setPixelWithAlpha(int x, int y, int fgArgb) {
        int bgArgb = imageBuffer.getPixelReader().getArgb(x, y);
        int blended = blendColors(bgArgb, fgArgb);
        writer.setArgb(x, y, blended);
    }

    private int blendColors(int bgArgb, int fgArgb) {
        float alpha = ((fgArgb >> 24) & 0xFF) / 255.0f;

        int bgR = (bgArgb >> 16) & 0xFF;
        int bgG = (bgArgb >> 8) & 0xFF;
        int bgB = bgArgb & 0xFF;

        int fgR = (fgArgb >> 16) & 0xFF;
        int fgG = (fgArgb >> 8) & 0xFF;
        int fgB = fgArgb & 0xFF;

        int r = (int) (fgR * alpha + bgR * (1 - alpha));
        int g = (int) (fgG * alpha + bgG * (1 - alpha));
        int b = (int) (fgB * alpha + bgB * (1 - alpha));

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private void togglePlay() {
        if (isPlaying) {
            pause();
        } else {
            if (engine == null) {
                lancerRecherche();
            }
            play();
        }
    }

    private void lancerRecherche() {
        int w = (int) img.getWidth();
        int srcId = depart[0] * w + depart[1];
        int tgtId = arrivee[0] * w + arrivee[1];

        // Exécution de l'algorithme de Dijkstra
        Dijkstra.Resultat res = Dijkstra.calculer(g, srcId, tgtId);

        // Initialisation du moteur d'animation
        engine = new GraphSearchEngine(res, srcId, tgtId);

        // Vérification du coût
        double cost = (tgtId >= 0 && tgtId < res.dist.length) ? res.dist[tgtId] : -1;
        if (cost == Double.MAX_VALUE) {
            cost = -1;
        }

        this.pendingCost = cost;

        // Mémorisation des paramètres pour les stats
        lastConnectivity = controlPanel.getConnectivitySelector().getValue();
    }

    private void play() {
        isPlaying = true;
        controlPanel.getBtnPlay().setText("⏸ Pause");
        timer.start();
    }

    private void pause() {
        isPlaying = false;
        controlPanel.getBtnPlay().setText("▶ Lecture");
        timer.stop();
    }

    private void setupTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPlaying || engine == null)
                    return;

                // Vitesse
                int speed = (int) controlPanel.getSpeedSlider().getValue();

                int stepsToProcess = Math.max(1, speed * 10); // Multiplicateur pour fluidité

                for (int i = 0; i < stepsToProcess; i++) {
                    if (engine.isFinished()) {
                        pause();
                        refreshDisplay(); // Force le rafraîchissement final selon la checkbox

                        // Stats finales
                        statsTable.addRecord(lastConnectivity, engine.getVisitedCount(),
                                currentTotalNodes, engine.getPathSize(), pendingCost);
                        break;
                    }

                    int nodeId = engine.getHistoryNode(engine.getCurrentStep());
                    boolean isPath = engine.isPathStep(engine.getCurrentStep());
                    engine.step();

                    if (nodeId != -1) {
                        // PENDANT l'animation, on dessine TOUJOURS pour visualiser la progression
                        // C'était la demande : "l'animation soit dans tout les cas visible"
                        int color;
                        if (!isPath) {
                            color = 0x404A9EFF;
                        } else {
                            color = 0xFFFF0000;
                        }

                        drawPixelNode(nodeId, color);
                    }
                }
            }
        };
    }
}
