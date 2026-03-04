package fr.grapheimage.graphe;

/**
 * Classe utilitaire pour construire un graphe à partir d'une matrice de niveaux
 * de gris.
 */
public class ConstructeurGraphe {

    /**
     * Mode de connexité pour la construction du graphe.
     */
    public enum ModeConnexite {
        CONNEXITE4, // 4 voisins (Nord, Sud, Est, Ouest)
        CONNEXITE8 // 8 voisins (Diagonales incluses)
    }

    /**
     * Construit un graphe pondéré à partir d'une grille de pixels.
     * Le poids d'une arête dépend de la distance géométrique et de la différence
     * d'intensité.
     * 
     * @param gris matrice des niveaux de gris [hauteur][largeur]
     * @param mode mode de connexité (4 ou 8 voisins)
     * @return le graphe construit
     */
    public static Graphe construire(int[][] gris, ModeConnexite mode) {
        int h = gris.length;
        int w = gris[0].length;
        Graphe g = new Graphe(w * h);

        // Directions : {dx, dy}
        int[][] dirs4 = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        int[][] dirs8 = {
                { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
        };

        int[][] dirs = (mode == ModeConnexite.CONNEXITE4) ? dirs4 : dirs8;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int id = y * w + x;

                for (int[] d : dirs) {
                    int nx = x + d[0];
                    int ny = y + d[1];

                    // Vérification des bornes de l'image
                    if (nx >= 0 && ny >= 0 && nx < w && ny < h) {
                        int nid = ny * w + nx;

                        // Poids = Distance géométrique + Différence de couleur
                        // Distance : 1 pour cardinal, √2 (~1.414) pour diagonal
                        int diff = Math.abs(gris[y][x] - gris[ny][nx]);
                        double distGeo = (d[0] != 0 && d[1] != 0) ? Math.sqrt(2) : 1.0;
                        double poids = distGeo + diff;

                        // Ajout de l'arête orientée (l'autre sens sera traité quand on sera sur le
                        // voisin)
                        // Note : Graphe.ajouterArete ajoute les deux sens, donc ici on ajoute
                        // potentiellement en double si on ne fait pas attention.
                        // Mais 'ajouterArete' dans Graphe ajoute u->v ET v->u.
                        // ICI : On itère sur TOUS les pixels.
                        // Si on est en (0,0), on ajoute (0,0)->(0,1).
                        // Quand on sera en (0,1), on ajoutera (0,1)->(0,0).
                        // Graphe.java ajoute DEJA le retour. Donc on ajoute chaque arête 2 fois (4
                        // entrées dans la liste d'adjacence au total pour une connexion ?)
                        // VERIFICATION : Graphe.java : adj.get(u).add(...); adj.get(v).add(...);
                        // C'est un problème potentiel de performance (2x trop d'arêtes), mais la
                        // consigne est "ne pas modifier le comportement".
                        // Si le comportement actuel fonctionne, on le laisse.
                        // Correction : pour ne pas changer le comportement, on ne touche pas à la
                        // logique de la boucle.

                        // Cependant, l'implémentation actuelle de `g.ajouterArete` ajoute u->v et v->u.
                        // Comme on parcourt toute la grille, pour chaque paire (u, v), on va appeler
                        // ajouterArete quand on est sur u, ET quand on est sur v.
                        // Donc on aura des doublons dans la liste d'adjacence.
                        // C'est inefficace mais fonctionnel (A* prendra juste le premier ou les deux).
                        // Respect strict de la consigne "Ne pas modifier le comportement" -> je laisse
                        // tel quel.

                        g.ajouterArete(id, nid, poids);
                    }
                }
            }
        }
        return g;
    }
}
