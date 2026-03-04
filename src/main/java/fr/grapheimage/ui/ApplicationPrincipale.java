package fr.grapheimage.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application ImageGraphPathFinder.
 * Lance l'interface graphique permettant de visualiser l'algorithme
 * de recherche de plus court chemin (Dijkstra) sur une image.
 */
public class ApplicationPrincipale extends Application {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        VueImage root = new VueImage();

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Recherche de plus court chemin dans une image avec Dijkstra");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
