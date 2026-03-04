package fr.grapheimage.algorithmes;

import java.util.List;

/**
 * Moteur de lecture pour l'animation pas à pas des algorithmes de recherche.
 * Permet de rejouer l'historique de l'exécution (visites + chemin final).
 */
public class GraphSearchEngine {

    private final List<Integer> visitedOrder;
    private final int[] path;

    // État de la lecture
    private int currentStep = 0;

    public GraphSearchEngine(Dijkstra.Resultat resultat, int source, int cible) {
        this.visitedOrder = resultat.ordreVisite;
        this.path = Dijkstra.reconstruireChemin(source, cible, resultat.pred);
    }

    /**
     * Avance d'une étape dans l'animation.
     * 
     * @return true si une nouvelle étape a été traitée, false si terminé.
     */
    public boolean step() {
        if (isFinished()) {
            return false;
        }
        currentStep++;
        return true;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public boolean isFinished() {
        int total = visitedOrder.size() + (path != null ? path.length : 0);
        return currentStep >= total;
    }

    /**
     * Récupère l'identifiant du nœud à dessiner pour l'étape donnée.
     * 
     * @param stepIndex l'index de l'étape
     * @return l'id du nœud, ou -1 si invalide
     */
    public int getHistoryNode(int stepIndex) {
        if (stepIndex < 0) {
            return -1;
        }

        // Phase 1 : Nœuds visités
        if (stepIndex < visitedOrder.size()) {
            return visitedOrder.get(stepIndex);
        }

        // Phase 2 : Chemin final
        int pathIndex = stepIndex - visitedOrder.size();
        if (path != null && pathIndex < path.length) {
            return path[pathIndex];
        }

        return -1;
    }

    /**
     * Indique si l'étape correspond à la phase de tracé du chemin final.
     */
    public boolean isPathStep(int stepIndex) {
        return stepIndex >= visitedOrder.size();
    }

    public int getTotalSteps() {
        return visitedOrder.size() + (path != null ? path.length : 0);
    }

    public int getPathSize() {
        return (path != null) ? path.length : 0;
    }

    public int getVisitedCount() {
        return visitedOrder.size();
    }
}
