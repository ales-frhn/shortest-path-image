package fr.grapheimage.image;

import javafx.scene.image.Image;

/**
 * Classe utilitaire pour le chargement des images.
 */
public class ChargeurImage {

    /**
     * Charge une image depuis le chemin spécifié.
     * Supporte les chemins relatifs aux resources ou absolus (file:...).
     * 
     * @param chemin chemin de l'image (ex: "images/test.png" ou URI complet)
     * @return l'objet Image JavaFX chargé, ou null si l'image n'existe pas
     */
    public static Image charger(String chemin) {
        try {
            // Tenter de charger depuis les ressources
            var stream = ChargeurImage.class.getClassLoader().getResourceAsStream(chemin);
            if (stream != null) {
                return new Image(stream);
            }
            // Si la ressource n'existe pas, retourner null
            return null;
        } catch (Exception e) {
            // En cas d'erreur, retourner null
            return null;
        }
    }
}
