# 🗺️ Recherche du plus court chemin dans une image avec Dijkstra

Application de visualisation de l'algorithme de Dijkstra pour la recherche du plus court chemin dans une image modélisée comme un graphe de pixels.

![Java](https://img.shields.io/badge/Java-21-orange?logo=java) ![JavaFX](https://img.shields.io/badge/JavaFX-21.0.2-blue) ![Maven](https://img.shields.io/badge/Maven-build-red?logo=apachemaven)

---

## 📋 Description

**ImageGraphPathFinder** transforme une image en graphe pondéré où chaque pixel devient un sommet. L'application implémente l'algorithme de Dijkstra pour trouver le chemin optimal entre deux pixels sélectionnés par l'utilisateur, en tenant compte à la fois de la distance géométrique et de la variation d'intensité lumineuse.

### Fonctionnalités principales

| Fonctionnalité | Détail |
|---|---|
| 🖼️ Chargement d'images | Support PNG, JPEG, JPG, BMP, GIF |
| 🖱️ Sélection interactive | Cliquez pour définir le pixel de départ et d'arrivée |
| ▶️ Animation en temps réel | Visualisation pas à pas de l'exploration de Dijkstra |
| 🔗 Modes de connexité | Graphe 4-connexe (cardinales) ou 8-connexe (diagonales incluses) |
| 📊 Statistiques détaillées | Nœuds visités, coût du chemin, etc. |
| 🎨 Interface moderne | Dark theme, animations fluides, contrôles intuitifs |

---

## 🚀 Installation et lancement

### Prérequis

- Java 21 ou supérieur
- Maven

Vérifier votre version de Java :
```bash
java -version
# Résultat attendu : java version "21.x.x" ou supérieur
```

### Option 1 — Maven (ligne de commande)

```bash
# 1. Se placer à la racine du projet
cd CodeMiniProjet

# 2. Compiler
mvn clean compile

# 3. Lancer
mvn javafx:run
```

### Option 2 — IntelliJ IDEA *(recommandé)*

1. Ouvrir le projet dans IntelliJ IDEA
2. `File` → `Project Structure` → `Project` → configurer le SDK Java 21
3. Clic-droit sur `ApplicationPrincipale.java` → `Run 'ApplicationPrincipale.main()'`

### Option 3 — Eclipse

1. `File` → `Import` → `Existing Maven Projects`
2. `Build Path` → `Configure Build Path` → `JRE System Library` → Java 21
3. Clic-droit sur `ApplicationPrincipale.java` → `Run As` → `Java Application`

---

## 🖱️ Utilisation

1. Sélectionnez un mode de connexité **(4-voisins ou 8-voisins)**
2. Cliquez sur un pixel de **départ** 🔴
3. Cliquez sur un pixel d'**arrivée** 🟢
4. Appuyez sur **Lecture** pour lancer l'animation
5. Observez l'exploration *(pixels bleus)* et le chemin final *(pixels rouges)*

### Contrôles disponibles

| Contrôle | Action |
|---|---|
| Connexité | Choisir entre 4 ou 8 directions |
| Affichage | Masquer/Afficher la progression |
| Image | Charger une image personnalisée |
| Lecture | Lancer/mettre en pause l'animation |
| Reset | Réinitialiser la recherche |
| Vitesse | Ajuster la vitesse d'animation (x1 à x100) |

---

## 🏗️ Architecture du projet

```
src/main/java/fr/grapheimage/
├── algorithmes/
│   ├── Dijkstra.java              # Algorithme de Dijkstra
│   └── GraphSearchEngine.java     # Moteur d'animation
├── graphe/
│   ├── Graphe.java                # Représentation du graphe (liste d'adjacence)
│   └── ConstructeurGraphe.java    # Construction du graphe depuis une image
├── image/
│   ├── ChargeurImage.java         # Chargement des images
│   └── MatriceNiveauxDeGris.java  # Conversion en niveaux de gris
└── ui/
    ├── ApplicationPrincipale.java # Point d'entrée
    ├── VueImage.java              # Vue principale
    ├── ControlPanel.java          # Panneau de contrôle
    └── StatsTable.java            # Tableau de statistiques
```

---

## 📐 Modélisation algorithmique

### Représentation du graphe

- **Sommets** : Chaque pixel `(x, y)` est converti en identifiant unique : `id = y × largeur + x`
- **Arêtes** : Relient les pixels voisins (4 ou 8 directions selon le mode)
- **Structure** : Liste d'adjacence

### Calcul des poids

Le poids d'une arête entre deux pixels A et B est :

```
poids(A, B) = c + |intensité(A) - intensité(B)|
```

| Déplacement | Valeur de c |
|---|---|
| Horizontal ou vertical | 1 |
| Diagonal | √2 |

> Le chemin optimal favorise les zones homogènes en couleur tout en minimisant la distance géométrique.

### Modes de connexité

- **4-connexe** : Nord, Sud, Est, Ouest
- **8-connexe** : Cardinales + Diagonales

### Garanties de Dijkstra

- ✅ **Optimalité** : Garantie dans tous les cas (poids strictement positifs)
- ⚡ **Arrêt anticipé** : S'arrête dès que la cible est atteinte
- 📈 **Complexité** : `O((V + E) log V)` avec file de priorité

---

## 👤 Auteur

**Ales Ferhani**  
Projet réalisé dans le cadre du module d'Algorithmique Avancée — L3 Informatique, Université Paris Cité  
[LinkedIn](https://www.linkedin.com/in/ales-ferhani-a47b37365) · [GitHub](https://github.com/ales-frhn)
