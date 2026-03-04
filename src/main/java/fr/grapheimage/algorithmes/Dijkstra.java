package fr.grapheimage.algorithmes;

import fr.grapheimage.graphe.Graphe;
import fr.grapheimage.graphe.Graphe.Arete;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Implémentation de l'algorithme de Dijkstra pour trouver le chemin le plus
 * court
 * dans un graphe pondéré à poids positifs.
 */
public class Dijkstra {

    /**
     * Structure immuable contenant les résultats de l'algorithme.
     */
    public static class Resultat {
        public final int[] pred; // Tableau des prédécesseurs
        public final double[] dist; // Distances minimales depuis la source
        public final List<Integer> ordreVisite; // Ordre chronologique de visite (pour l'animation)

        public Resultat(int[] pred, double[] dist, List<Integer> ordre) {
            this.pred = pred;
            this.dist = dist;
            this.ordreVisite = ordre;
        }
    }

    /**
     * Calcule le plus court chemin entre une source et une cible.
     * 
     * @param g      le graphe à parcourir
     * @param source l'identifiant du nœud source
     * @param cible  l'identifiant du nœud cible
     * @return un objet Resultat contenant les distances, les prédécesseurs et
     *         l'ordre de visite
     */
    public static Resultat calculer(Graphe g, int source, int cible) {
        int n = g.taille();

        double[] dist = new double[n];
        int[] pred = new int[n];
        boolean[] visited = new boolean[n];
        List<Integer> ordreVisite = new ArrayList<>();

        // Initialisation : distances infinies sauf pour la source
        Arrays.fill(dist, Double.MAX_VALUE);
        Arrays.fill(pred, -1);
        dist[source] = 0;

        // File de priorité triée par distance croissante
        PriorityQueue<NodeD> pq = new PriorityQueue<>(Comparator.comparingDouble(node -> node.dist));
        pq.offer(new NodeD(source, 0));

        // Boucle principale
        while (!pq.isEmpty()) {
            NodeD current = pq.poll();
            int u = current.id;

            if (visited[u]) {
                continue;
            }
            visited[u] = true;
            ordreVisite.add(u);

            // Arrêt prématuré si la cible est atteinte
            if (u == cible) {
                break;
            }

            // Relaxation des voisins
            for (Arete e : g.voisins(u)) {
                int v = e.vers;
                double w = e.poids;

                if (!visited[v] && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pred[v] = u;
                    pq.offer(new NodeD(v, dist[v]));
                }
            }
        }

        return new Resultat(pred, dist, ordreVisite);
    }

    /**
     * Classe interne représentant un nœud dans la file de priorité.
     */
    private static class NodeD {
        int id;
        double dist;

        public NodeD(int id, double dist) {
            this.id = id;
            this.dist = dist;
        }
    }

    /**
     * Reconstruit le chemin optimal depuis la source jusqu'à la cible.
     * 
     * @param source l'identifiant du nœud source
     * @param cible  l'identifiant du nœud cible
     * @param pred   tableau des prédécesseurs
     * @return le chemin sous forme de tableau d'identifiants, ou null si aucun
     *         chemin n'existe
     */
    public static int[] reconstruireChemin(int source, int cible, int[] pred) {
        List<Integer> chemin = new ArrayList<>();
        int cur = cible;

        // Remonter les prédécesseurs
        while (cur != -1) {
            chemin.add(cur);
            if (cur == source) {
                break;
            }
            cur = pred[cur];
        }

        // Si on n'est pas remonté jusqu'à la source (graphe non connexe ou chemin non
        // trouvé)
        if (cur == -1) {
            return null;
        }

        Collections.reverse(chemin);
        return chemin.stream().mapToInt(i -> i).toArray();
    }
}
