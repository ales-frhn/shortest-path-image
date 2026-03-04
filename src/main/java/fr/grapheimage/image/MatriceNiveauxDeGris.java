package fr.grapheimage.image;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

/**
 * Classe utilitaire pour la manipulation des pixels et la conversion en niveaux
 * de gris.
 */
public class MatriceNiveauxDeGris {

    /**
     * Convertit une image JavaFX en une matrice d'entiers représentant les niveaux
     * de gris (0-255).
     * La méthode utilise la moyenne simple des composantes RGB : (R + G + B) / 3.
     * 
     * @param img l'image source à convertir
     * @return un tableau 2D [hauteur][largeur] contenant les valeurs de gris
     *         (0-255)
     */
    public static int[][] convertir(Image img) {
        int largeur = (int) img.getWidth();
        int hauteur = (int) img.getHeight();

        int[][] gris = new int[hauteur][largeur];
        PixelReader lecteur = img.getPixelReader();

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                int argb = lecteur.getArgb(x, y);

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = (argb) & 0xFF;

                // Calcul de la moyenne pour le niveau de gris (méthode simple conservée pour
                // compatibilité)
                int niveau = (r + g + b) / 3;

                gris[y][x] = niveau;
            }
        }

        return gris;
    }
}
