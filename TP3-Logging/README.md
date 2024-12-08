# Software Logging Application

## TP 3 -  Introduction to Software Logging and Observability

### HAI913I : Évolution et Restructuration des Logiciels

## Auteurs :

- [Éric GILLES](https://github.com/eric-gilles)
- [Morgan NAVEL](https://github.com/MorganNavel)

## Description de l'application

Ce TP est composé de 2 parties :
- [src/main/java/](./src/main/java/) : Application Java avec une API REST pour la gestion de produits et d'utilisateurs.
- [src/main/generated/](./src/main/generated/) : Application Java générée par un générateur de code avec Spoon.

## Prérequis
- Java 11
- Maven

### Installation
1. Télécharger le zip du projet à l'adresse suivante :  
(https://github.com/MorganNavel/HAI913I-Evo-Restructuration-Log/releases/download/TP3-Logging/HAI913I_TP3-Logging_GILLES_NAVEL.zip)

2. Décompresser le projet

3. Ouvrir le projet dans votre éditeur de code(VScode, IntelliJ IDEA, Eclipse IDE)

4. Ouvrir la classe `ParsingMain.java` située dans le répertoire `src/main/java/com/example/spoon/`.

5. Exécuter la classe `ParsingMain.java` en cliquant sur le bouton `Run` situé en haut à droite de la classe afin de parser et logguer le code source de l'application.

6. Ouvrir le fichier `pom.xml` situé à la racine du projet et modifier le répertoire de l'application à build comme ci-dessous :

```xml
        <!-- Spoon & Logging generated code from the API -->
        <sourceDirectory>src/main/generated/</sourceDirectory>
        <!-- API Source code without Spoon & Logging -->
        <!-- <sourceDirectory>src/main/java/</sourceDirectory> -->
```
Lancer la commande `mvn compile` pour compiler le projet ou clicker sur l'icone de Maven dans votre IDE.

7. Lancer la classe `TP3APIApplication.java` située dans le package `src/main/generated/com/example/tp3logging/TP3APIApplication.java` pour démarrer l'API REST.

8. Lancer la CLI classe `CLI.java` située dans le package `src/main/generated/com/example/tp3logging/cli/CLI.java` pour exécuter les différentes fonctionnalités de l'application.

## Utilisation
- Suivre les instructions affichées dans la console pour exécuter les différentes fonctionnalités de l'application.

### Menu de connexion de la CLI

```shell
========== MENU DE CONNEXION ==========
1. Se connecter
2. S`inscrire
3. Simuler des scénarios utilisateurs
0. Quitter
Votre choix :
```

Une fois connecté, le menu principal de la CLI s'affiche :

### Menu Principal de la CLI

```shell
========== MENU ==========
1. Consulter tous les produits
2. Ajouter un produit
3. Rechercher un produit par ID
4. Supprimer un produit par ID
0. Se déconnecter
Votre choix :
```

## Logging

Le résultat des logs est affiché dans la console de l'API REST mais aussi dans le fichier `logs/app.log` situé à la racine du projet.


## Parsing des Log en JSON des Utilisateurs
Pour afficher les Utilisateurs au format JSON, il suffit de lancer la classe `LogToUserJsonConverter.java` située dans le package `src/main/generated/com/example/logging/` et ainsi afficher les logs des utilisateurs au format JSON dans le fichier `logs/users-profiles.json` situé à la racine du projet.
