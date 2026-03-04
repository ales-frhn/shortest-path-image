package fr.grapheimage.graphe;

import java.util.ArrayList;
import java.util.List;

/**
 * Représentation d'un graphe pondéré sous forme de liste d'adjacence.
 */
public class Graphe {

    /**
     * Une arête reliant ce nœud à un nœud voisin avec un poids donné.
     */
    public static class Arete {
        public final int vers;
        public final double poids;

        public Arete(int vers, double poids) {
            this.vers = vers;
            this.poids = poids;
        }
    }

    private final List<List<Arete>> adj;

    /**
     * Initialise un graphe avec un nombre fixe de nœuds.
     * 
     * @param taille nombre de nœuds
     */
    public Graphe(int taille) {
        adj = new ArrayList<>(taille); // Optimisation légère de la capacité initiale
        for (int i = 0; i < taille; i++) {
            adj.add(new ArrayList<>());
        }
    }

    /**
     * Ajoute une arête non orientée entre u et v.
     * Ajoute u->v et v->u.
     * 
     * @param u     nœud de départ
     * @param v     nœud d'arrivée
     * @param poids coût de la traversée
     */
    public void ajouterArete(int u, int v, double poids) {
        adj.get(u).add(new Arete(v, poids));
        adj.get(v).add(new Arete(u, poids));
    }

    /**
     * Retourne la liste des arêtes partantes du nœud u.
     */
    public List<Arete> voisins(int u) {
        return adj.get(u);
    }

    /**
     * Retourne le nombre de nœuds dans le graphe.
     */
    public int taille() {
        return adj.size();
    }
}
