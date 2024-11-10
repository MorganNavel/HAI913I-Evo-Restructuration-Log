# Application d'analyse d'application Java avec Eclipse JDT

## TP 2 - Compréhension des programmes avec Eclipse JDT

### HAI913I : Évolution et Restructuration des Logiciels

## Auteurs :

- [Éric GILLES](https://github.com/eric-gilles)
- [Morgan NAVEL](https://github.com/MorganNavel)

## Description de l'application
Ce TP fait suite au projet d'analyse de code source réalisé :
- [TP1 - Analyse statique de code source](https://github.com/MorganNavel/HAI913I-Evo-Restructuration-Log/releases/TP1_part2)

Ce TP est divisé en 3 exercices :

- Ex1 : Analyse Statique du Code Source d'une application
- Ex2 : Construction du graphe d'appel de l'application
- Ex3 : Clustering et Identification de Modules

### 1) Analyse Statique du Code Source d'une application pour calculer les informations suivantes :

- Nombre de classes de l’application.
- Nombre de lignes de code de l’application.
- Nombre total de méthodes de l’application.
- Nombre total de packages de l’application.
- Nombre moyen de méthodes par classe.
- Nombre moyen de lignes de code par méthode.
- Nombre moyen d’attributs par classe.
- Les 10% des classes qui possèdent le plus grand nombre de méthodes.
- Les 10% des classes qui possèdent le plus grand nombre d’attributs.
- Les classes qui font partie en même temps des deux catégories précédentes.
- Les classes qui possèdent plus de X méthodes (la valeur de X est donnée).
- Les 10% des méthodes qui possèdent le plus grand nombre de lignes de code (par classe).
- Le nombre maximal de paramètres par rapport à toutes les méthodes de l’application.

### 2) Construction du graphe d'appel de l'application

- Afficher le graphe d'appel de l'application sous forme de graphe orienté.
- Création d'un fichier .dot afin de visualiser le graphe d'appel de l'application.
- Calculer le couplage entre deux classes.
- Afficher le couplage entre classes.
- Créer le fichier .dot et png du graphe de couplage entre classes.

### 3) Clustering et Identification de Modules

- Clusterisation hiérarchique des classes.

### Utilisation d'une CLI pour exécuter les différentes fonctionnalités de l'application.

## Installation

### Prérequis

- Java 8
- Maven
- Dot (Graphviz pour la visualisation des graphes générés en .dot et .png)

### Installation

1. Télécharger le zip du projet à l'adresse suivante :
   [(https://github.com/MorganNavel/HAI913I-Evo-Restructuration-Log/releases/download/TP2/HAI913I_TP2JDT_GILLES_NAVEL.zip)]()
2. Décompresser le projet dans un répertoire de votre choix.
3. Ouvrir le projet dans votre éditeur de code (VScode, IntelliJ IDEA, Eclipse IDE)
4. Ouvrir la classe `UserInterface.java` située dans le package `cli`.
5. Exécuter la classe `UserInterface.java` en cliquant sur le bouton `Run` situé en haut à droite de la classe.

## Utilisation

- Suivre les instructions affichées dans la console pour exécuter les différentes fonctionnalités de l'application en commençant par donner le chemin du répertoire de l'application à analyser.

### Menu Principal de la CLI

```shell
Répertoire valide.

--- Menu Principal - Analyse avec Spoon ---
1: Exercice 1 - Analyse Code Source
2: Exercice 2 - Graphe D'appel et Couplage
3: Exercice 3 - Clustering et Identification de Modules
0: Quitter
Choisissez une option:
```

### Menu de l'exercice 1 : Analyse Code Source

```shell
--- Exercice 1 : Analyse Code Source ---
1: Nombre de classes de l'application.
2: Nombre de lignes de code de l'application.
3: Nombre total de méthodes de l'application.
4: Nombre total de packages de l'application.
5: Nombre moyen de méthodes par classe.
6: Nombre moyen de lignes de code par méthode.
7: Nombre moyen d'attributs par classe.
8: Les 10% des classes avec le plus grand nombre de méthodes.
9: Les 10% des classes avec le plus grand nombre d'attributs.
10: Classes présentes dans les deux catégories précédentes.
11: Classes avec plus de X méthodes (X est à définir).
12: Les 10% des méthodes avec le plus grand nombre de lignes de code.
13: Nombre maximal de paramètres parmi toutes les méthodes.
0: Retour au menu principal.

Choisissez une option: 
```

### Menu de l'exercice 2 : Graphe D'appel et Couplage

```shell
--- Exercice 2 : Graphe D'appel et Couplage ---
Graphe d'appels:
1: Afficher le graphe d'appels de méthodes.
2: Créer le fichier .dot et png du graphe d'appels.

Couplage:
3: Calculer le couplage entre deux classes.
4: Afficher le couplage entre classes.
5: Créer le fichier .dot et png du graphe de couplage entre classe.
0: Retour au menu principal.

Choisissez une option: 
```

### Menu de l'exercice 3 : Clustering et Identification de Modules

```shell
--- Exercice 3 : Clustering & Identification de Modules ---
1: Clusterisation hiérarchique des classes.
0: Retour au menu principal.

Choisissez une option: 
```
