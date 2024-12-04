package com.example.client;

import com.example.tp3logging.dto.LoginCredential;
import com.example.tp3logging.models.Product;
import com.example.tp3logging.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import static com.example.client.Utils.getUserChoice;

public class CLI {
    private static final String URL = "http://localhost:8080/api/";
    private static boolean LOGGED = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int choice = -1;
        while (choice != 0) {
            System.out.println("1. Se connecter");
            System.out.println("2. S'inscrire");
            System.out.println("3. Consulter les utilisateurs");
            System.out.println("0. Quitter");
            System.out.print("Votre choix : ");
            choice = getUserChoice(scanner);

            switch (choice) {
                case 1:
                    System.out.print("Email : ");
                    String email = scanner.next();
                    System.out.print("Mot de passe : ");
                    String password = scanner.next();
                    LOGGED = login(email, password);
                    if (LOGGED) {
                        System.out.println("Vous êtes connecté");
                        printMenu();
                        handleMenu(scanner);
                    }
                    break;
                case 2:
                    // TODO
                    break;
                case 3:
                    displayAllUsers();
                case 0:
                    System.out.println("Au revoir");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Choix invalide");
            }
        }
    }

    private static void displayAllUsers() {
        RestTemplate restTemplate = new RestTemplate();
        User[] users = restTemplate.getForObject(URL + "users", User[].class);
        if (users != null && users.length > 0) {
            System.out.println("Liste des utilisateurs : ");
            Arrays.stream(users).forEach(System.out::println);
        }
        System.out.println("Aucun utilisateur trouvé");
    }

    private static boolean login(String email, String password) {
        RestTemplate restTemplate = new RestTemplate();
        LoginCredential loginCredential = new LoginCredential();
        loginCredential.setEmail(email);
        loginCredential.setPassword(password);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(URL + "auth/login", loginCredential, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Vous êtes connecté");
                return true;
            } else {
                System.out.println("Erreur de connexion ");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
            return false;
        }
    }

    private static void printMenu() {
        System.out.println("1. Consulter tous les produits");
        System.out.println("2. Ajouter un produit");
        System.out.println("3. Rechercher un produit par ID");
        System.out.println("4. Supprimer un produit par ID");
        System.out.println("0. Se déconnecter");
        System.out.print("Votre choix : ");
    }

    private static void handleMenu(Scanner scanner) {
        int choice = -1;
        while (choice != 0) {
            printMenu();
            choice = getUserChoice(scanner);
            switch (choice) {
                case 1:
                    // Voir tous les produits
                    displayProducts();
                    break;
                case 2:
                    // Ajouter un produit
                    addProduct(scanner);
                    break;
                case 3:
                    // Rechercher un produit par ID
                    getProductById(scanner);
                    break;
                case 4:
                    // Supprimer un produit par ID
                    deleteProductById(scanner);
                    break;
                case 0:
                    System.out.println("Déconnexion");
                    break;
                default:
                    System.out.println("Choix invalide");
            }
        }
    }

    private static void displayProducts(){
        RestTemplate restTemplate = new RestTemplate();
        Product[] products = restTemplate.getForObject(URL + "products", Product[].class);
        if (products != null && products.length > 0) {
            System.out.println("Liste des produits : ");
            Arrays.stream(products).forEach(System.out::println);
        }
        System.out.println("Aucun produit trouvé");
    }

    private static void addProduct(Scanner scanner) {
        System.out.print("Nom du produit : ");
        String name = scanner.next();
        System.out.print("Prix du produit : ");
        double price = scanner.nextDouble();
        System.out.print("Date d'expiration (dd-MM-yyyy) : ");
        String date = scanner.next();
        LocalDate expirationDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setExpirationDate(new Date(expirationDate.toEpochDay()));
        ResponseEntity<String> response = new RestTemplate().postForEntity(URL + "products", product, String.class);
        System.out.println(response.getBody());
    }

    private static void getProductById(Scanner scanner) {
        System.out.print("Enter product ID: ");
        int id = getUserChoice(scanner);
        RestTemplate restTemplate = new RestTemplate();
        Product product = restTemplate.getForObject(URL + "products/" + id, Product.class);
        if (product != null) {
            System.out.println("Product found: " + product);
        } else {
            System.out.println("Product not found");
        }

    }

    private static void deleteProductById(Scanner scanner) {
        System.out.print("Enter product ID: ");
        int id = getUserChoice(scanner);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(URL + "products/" + id);
        System.out.println("Product deleted");
    }

}
