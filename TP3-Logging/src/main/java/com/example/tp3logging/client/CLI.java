package com.example.tp3logging.client;

import com.example.tp3logging.dto.LoginCredentials;
import com.example.tp3logging.models.Product;
import com.example.tp3logging.models.User;
import com.example.tp3logging.services.JWTService;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.tp3logging.client.Utils.getUserChoice;

public class CLI {
    private static final String URL = "http://localhost:8080/api/";
    private static final String jwtToken = null;
    private static String email = null;
    private static final Random random = new Random();
    private static final JWTService jwtService = new JWTService();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            Utils.printMenuConnexion();
            int choice = getUserChoice(scanner);
            if (choice == -1) {
                System.err.println("Erreur : Veuillez entrer un nombre valide.");
                continue;
            }
            switch (choice) {
                case 1:
                    System.out.print("Email : ");
                    String email = scanner.nextLine();
                    System.out.print("Mot de passe : ");
                    String password = scanner.nextLine();
                    if (login(email, password)) {
                        CLI.email = email;
                        handleMenu(scanner);
                    }
                    break;
                case 2:
                    register(scanner);
                    break;
                case 3:
                    //displayAllUsers();
                    simulateUserScenarios();
                    break;
                case 0:
                    System.out.println("Au revoir");
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide");
                    break;
            }
        }
    }

    private static void register(Scanner scanner) {
        System.out.print("Nom : ");
        String name = scanner.nextLine();
        System.out.print("Âge : ");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setAge(age);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(URL + "auth/register", user, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("Utilisateur créé avec succès.");
                System.out.println("Vous pouvez maintenant vous connecter.\n\n");
            } else {
                System.err.println("Erreur lors de la création de l'utilisateur.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'utilisateur : " + e.getMessage());
        }
    }

    public static List<User> displayAllUsers() {
        RestTemplate restTemplate = new RestTemplate();
        User[] users = restTemplate.getForObject(URL + "users", User[].class);
        /*if (users != null && users.length > 0) {
            System.out.println("Liste des utilisateurs : ");
            Arrays.stream(users).forEach(System.out::println);
            System.out.println("\n\n");
        } else {
            System.out.println("Aucun utilisateur trouvé");
        }*/
        assert users != null;
        return new ArrayList<>(Arrays.asList(users));
    }

    private static boolean login(String email, String password) {
        RestTemplate restTemplate = new RestTemplate();
        LoginCredentials loginCredentials = new LoginCredentials();
        loginCredentials.setEmail(email);
        loginCredentials.setPassword(password);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(URL + "auth/login", loginCredentials, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Vous êtes connecté");
                return true;
            } else {
                System.err.println("Erreur de connexion ");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            return false;
        }
    }

    private static void handleMenu(Scanner scanner) {
        int choice = -1;
        while (choice != 0) {
            Utils.printMenu();
            choice = getUserChoice(scanner);
            switch (choice) {
                case 1:
                    displayProducts();
                    break;
                case 2:
                    addProduct(scanner);
                    break;
                case 3:
                    getProductById(scanner);
                    break;
                case 4:
                    deleteProductById(scanner);
                    break;
                case 0:
                    System.out.println("Déconnexion");
                    break;
                default:
                    System.err.println("Choix invalide");
            }
        }
    }

    private static void displayProducts() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = URL + "products?userId=" + jwtService.getUserIdFromToken(jwtToken, email);
        ResponseEntity<Product[]> response = new RestTemplate().exchange(url, HttpMethod.GET, entity, Product[].class);
        Product[] products = response.getBody();
        if (products != null && products.length > 0) {
            System.out.println("\nListe des produits : ");
            Arrays.stream(products).forEach(product -> {
                System.out.println("ID: " + product.getProductId());
                System.out.println("Nom: " + product.getName());
                System.out.println("Prix: " + product.getPrice());
                System.out.println("Date d'expiration: " + new Date(product.getExpirationDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                System.out.println();
            });
        } else {
            System.out.println("\nAucun produit trouvé\n");
        }
    }

    private static void addProduct(Scanner scanner) {
        System.out.print("\nNom du produit : ");
        String name = scanner.nextLine();
        System.out.print("Prix du produit : ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Date d'expiration (dd-MM-yyyy) : ");
        String date = scanner.nextLine();
        LocalDate expirationDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setExpirationDate(Date.from(expirationDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<Product> entity = new HttpEntity<>(product, headers);

        try {
            String url = URL + "products/product?userId=" + jwtService.getUserIdFromToken(jwtToken, email);
            ResponseEntity<String> response = new RestTemplate().postForEntity(url, entity, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("Produit ajouté avec succès");
            } else {
                System.err.println("Erreur lors de l'ajout du produit");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du produit : " + e.getMessage());
        }
    }

    private static void getProductById(Scanner scanner) {
        System.out.print("\nEntrez l'ID du produit : ");
        int productId = Integer.parseInt(scanner.nextLine());
        Product product = getProductById(productId);
        if (product != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = new Date(product.getExpirationDate().getTime()).toInstant()
                    .atZone(ZoneId.systemDefault()).format(formatter);
            System.out.println("\nProduit trouvé : ");
            System.out.println("ID: " + product.getProductId());
            System.out.println("Nom: " + product.getName());
            System.out.println("Prix: " + product.getPrice());
            System.out.println("Date d'expiration: " + formattedDate);
        } else {
            System.out.println("\nProduit non trouvé");
        }
    }

    private static void deleteProductById(Scanner scanner) {
        System.out.print("\nEntrez l'ID du produit : ");
        int productId = Integer.parseInt(scanner.nextLine());

        Product product = getProductById(productId);
        if (product != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = URL + "products/" + productId + "?userId=" + jwtService.getUserIdFromToken(jwtToken, email);
            new RestTemplate().exchange(url, HttpMethod.DELETE, entity, Void.class);
            System.out.println("\n Le produit " + product.getName() + " a été supprimé");
        } else {
            System.out.println("Produit non trouvé");
        }
    }

    private static Product getProductById(int productId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            String url = URL + "products/" + productId + "?userId=" + jwtService.getUserIdFromToken(jwtToken, email);
            ResponseEntity<Product> response = new RestTemplate().exchange(url, HttpMethod.GET, entity, Product.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du produit : " + e.getMessage());
            return null;
        }
    }

    // Add this method to the CLI class
    private static void simulateUserScenarios() {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("\nSimulation de scénarios utilisateurs");
        System.out.println("----------------------------");
        System.out.println("Création de 5 utilisateurs");
        for (int i = 4; i <= 9; i++) {
            String email = "user" + i + "@example.com";
            String password = "password" + i;
            String name = "User" + i;
            int age = 20 + i;

            // Register user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setAge(age);
            restTemplate.postForEntity(URL + "auth/register", user, String.class);

            // Login user
            LoginCredentials loginCredentials = new LoginCredentials();
            loginCredentials.setEmail(email);
            loginCredentials.setPassword(password);
            ResponseEntity<String> response = restTemplate.postForEntity(URL + "auth/login", loginCredentials, String.class);
            String token = response.getBody();

            // Perform operations
            for (int j = 1; j < 3; j++) {
                int operation = (random.nextInt(1, 4));
                System.out.print("User " + i + " réalise l'opération ");
                switch (operation) {
                    case 1:
                        System.out.print(" Affichage de tous les produits\n");
                        displayProducts(token, email);
                        break;
                    case 2:
                        System.out.print(" Ajout de produit\n");
                        addProduct(token, email, "Product" + j, 10.0 * j, "01-01-2025");
                        break;
                    case 3:
                        System.out.print(" Recherche de produit par ID\n");
                        getProductById(token, email, j);
                        break;
                    case 4:
                        System.out.print("Suppression de produit par ID\n");
                        deleteProductById(token, email, j);
                        break;
                    default:
                        break;
                }
            }
            // Suppression des utilisateurs
            restTemplate.exchange(URL + "users/" + i, HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Void.class);
        }
        System.out.println("----------------------------\n\n");
    }

    private static void displayProducts(String token, String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = URL + "products?userId=" + jwtService.getUserIdFromToken(token, email);
        new RestTemplate().exchange(url, HttpMethod.GET, entity, Product[].class);
    }

    private static void addProduct(String token, String email, String name, double price, String expirationDate) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        LocalDate date = LocalDate.parse(expirationDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        product.setExpirationDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Product> entity = new HttpEntity<>(product, headers);
        String url = URL + "products/product?userId=" + jwtService.getUserIdFromToken(token, email);
        new RestTemplate().postForEntity(url, entity, String.class);
    }

    private static void getProductById(String token, String email, int productId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = URL + "products/" + productId + "?userId=" + jwtService.getUserIdFromToken(token, email);
        new RestTemplate().exchange(url, HttpMethod.GET, entity, Product.class);
    }

    private static void deleteProductById(String token, String email, int productId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = URL + "products/" + productId + "?userId=" + jwtService.getUserIdFromToken(token, email);
        new RestTemplate().exchange(url, HttpMethod.DELETE, entity, Void.class);
    }
}