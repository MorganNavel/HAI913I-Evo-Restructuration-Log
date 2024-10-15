package visitors;

import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

public class VisitorMethodsOfClasses extends Visitor {
<<<<<<< Updated upstream
    private static final String UNKNOWN = "Unknown";
    private final Map<String, Map<String, List<Map<String, String>>>> callGraph = new HashMap<>();
=======
    private Map<String, Map<String, List<Map<String, String>>>> callGraph = new HashMap<>();
    private ArrayList<String> classes = new ArrayList<>();
    private HashMap<String,Double> couplings = new HashMap<>();
    

>>>>>>> Stashed changes
    @Override
    public boolean visit(TypeDeclaration node) {
        String className = node.getName().getFullyQualifiedName();
        if(!classes.contains(className)) classes.add(className);

        MethodDeclaration[] methods = node.getMethods();

        for (MethodDeclaration method : methods) {
            String methodName = method.getName().getFullyQualifiedName();
            callGraph.putIfAbsent(className, new HashMap<>());
            callGraph.get(className).put(methodName, new ArrayList<>());

            saveCalledMethods(method, className, node);
        }
        return true;
    }

    private void saveCalledMethods(MethodDeclaration node, String className, TypeDeclaration classDeclaration) {
        node.accept(new ASTVisitor() {

            @Override
            public boolean visit(MethodInvocation methodInvocation) {
                String calledMethodName = methodInvocation.getName().getFullyQualifiedName();
                Expression expr = methodInvocation.getExpression();
                
                String receiverType = resolveType(expr, node, className, classDeclaration);
                

                Map<String, String> methodCallInfo = new HashMap<>();
                methodCallInfo.put("methodName", calledMethodName);
                methodCallInfo.put("receiverType", receiverType);
                if(!classes.contains(receiverType) && receiverType != "Unknown") classes.add(receiverType);


                List<Map<String, String>> methodsPreviouslyCalled = callGraph.get(className).get(node.getName().getFullyQualifiedName());
                methodsPreviouslyCalled.add(methodCallInfo);
                
                return super.visit(methodInvocation);
            }
        });
    }


    @Override
    public void displayResult() {
    	System.out.println(classes);
    	this.processApplicationCoupling();
        System.out.println("Graphe d'appels de méthodes :");
        for (Entry<String, Map<String, List<Map<String, String>>>> classEntry : callGraph.entrySet()) {
            String className = classEntry.getKey();
            boolean hasMethodCalls = false;
            System.out.println("-Classe : " + className);

            for (Entry<String, List<Map<String, String>>> methodEntry : classEntry.getValue().entrySet()) {
                String methodName = methodEntry.getKey();
                List<Map<String, String>> calledMethods = methodEntry.getValue();

                if (!calledMethods.isEmpty()) {
                    System.out.println("\tMéthode : " + methodName + " ----> : " + calledMethods);
                    hasMethodCalls = true;
                }
            }

            if (!hasMethodCalls) {
                System.out.println("\tAucune méthode n'appelle d'autres méthodes dans cette classe.\n");
            }
        }
        displayCoupling();
    }

    // Method to create the .dot and .png files of the call graph
    public void createCallGraphFile() {
        String directoryName = "callGraph";
        String dotFilename = directoryName + "/callGraph.dot";
        String pngFilename = directoryName + "/callGraph.png";

        
        // Create the directory if it doesn't exist
        File directory = new File(directoryName);
        if(!directory.exists()) {
            directory.mkdir();
        }

        // Create the .dot file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dotFilename))) {
            writer.write("digraph CallGraph {\n");
            writer.write("\trankdir=LR;\n");  // Representation left to right

            for (Entry<String, Map<String, List<Map<String, String>>>> classEntry : callGraph.entrySet()) {
                String className = classEntry.getKey();
    
                for (Entry<String, List<Map<String, String>>> methodEntry : classEntry.getValue().entrySet()) {
                    String methodName = methodEntry.getKey();
                    List<Map<String, String>> calledMethods = methodEntry.getValue();
    
                    for (Map<String, String> calledMethod : calledMethods) {
                        String receiverType = calledMethod.get("receiverType");
                        String method = calledMethod.get("methodName");
                        if(!Objects.equals(receiverType, UNKNOWN)) {
                            writer.write(String.format("\t \"%s::%s\" -> \"%s\" [label=\"%s\"];%n", className, methodName, receiverType, method ));
                        }
                    }
                }
            }

            writer.write("}\n");
            System.out.println("\nFichier .dot créé avec succès : " + dotFilename);
        } catch (IOException e) {
            System.out.println("Erreur lors de la création du fichier .dot : " + e.getMessage());
        }

        // Create the .png file from the .dot file
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFilename, "-o", pngFilename);
            Process p = pb.start();
            p.waitFor();
            System.out.println("Fichier .png créé avec succès : " + pngFilename);
        } catch (IOException | InterruptedException e) {
            System.out.println("Erreur lors de la création du fichier .png : " + e.getMessage());
        }
    }

    private String resolveType(Expression expr, MethodDeclaration method, String className, TypeDeclaration currentClass) {
        if(expr == null) return UNKNOWN;
        if(expr.toString().equals("System.out")) {
            return UNKNOWN;
        }
        // Si c'est "this", retourne le nom de la classe courante
        if (expr.toString().equals("this")) {
            return className;
        }

        // Vérifie si c'est un appel à une méthode statique (en supposant que la première lettre est une majuscule)
        if (Character.isUpperCase(expr.toString().charAt(0))) {
            return expr.toString();  // Le nom de la classe
        }

        // Si l'expression est une variable, vérifie si elle est déclarée localement dans la méthode
        String variableType = getVariableTypeInMethod(expr.toString(), method);
        if (!variableType.equals(UNKNOWN)) {
            return variableType;
        }

        // Sinon, vérifie si c'est un attribut de la classe
        String attributeType = getClassOfAttributes(expr.toString(), currentClass);
        if (!attributeType.equals(UNKNOWN)) {
            return attributeType;
        }

        // Par défaut, retourne "Unknown"
        return UNKNOWN;
    }

    // Recherche une variable dans les déclarations locales de la méthode
    private String getVariableTypeInMethod(String variableName, MethodDeclaration method) {
        // Parcourt les déclarations de variables dans la méthode pour trouver une correspondance
        for (Object statement : method.getBody().statements()) {
            if (statement instanceof VariableDeclarationStatement) {
                VariableDeclarationStatement varDecl = (VariableDeclarationStatement) statement;
                for (Object fragment : varDecl.fragments()) {
                    VariableDeclarationFragment varFragment = (VariableDeclarationFragment) fragment;
                    if (varFragment.getName().getFullyQualifiedName().equals(variableName)) {
                        return varDecl.getType().toString();  // Retourne le type de la variable locale
                    }
                }
            }
        }
        return UNKNOWN;
    }

    // Recherche le type d'un attribut dans la classe
    private String getClassOfAttributes(String attributeName, TypeDeclaration currentClass) {
        for (FieldDeclaration field : currentClass.getFields()) {
            for (Object fragment : field.fragments()) {
                VariableDeclarationFragment varFragment = (VariableDeclarationFragment) fragment;
                if (varFragment.getName().getFullyQualifiedName().equals(attributeName)) {
                    return field.getType().toString();
                }
            }
        }
        return UNKNOWN;
    }
 // Nombre de relations (appels) entre les méthodes de deux classes A et B
    private int countCallsBetweenClasses(String classA, String classB) {
        int count = 0;
        for (Entry<String, Map<String, List<Map<String, String>>>> classEntry : callGraph.entrySet()) {
            String className = classEntry.getKey();
            if (className.equals(classA) || className.equals(classB)) {
                for (Entry<String, List<Map<String, String>>> methodEntry : classEntry.getValue().entrySet()) {
                    List<Map<String, String>> calledMethods = methodEntry.getValue();
                    for (Map<String, String> calledMethod : calledMethods) {
                        String receiverType = calledMethod.get("receiverType");
                        if (receiverType.equals("Unknown")) continue;
                        if ((className.equals(classA) && classB.equals(receiverType)) || (className.equals(classB) && classA.equals(receiverType))) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    // Nombre total de relations (appels) entre les méthodes de toutes les classes
    private int countTotalBinaryCalls() {
        int count = 0;
        for (Map<String, List<Map<String, String>>> methodsInClass : callGraph.values()) {
            for (List<Map<String, String>> calledMethods : methodsInClass.values()) {
                for (Map<String, String> calledMethod : calledMethods) {
                    String receiverType = calledMethod.get("receiverType");
                    if (!receiverType.equals("Unknown")) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    // Calcul du couplage entre deux classes
    public double calculateCoupling(String classA, String classB) {
        int callsBetweenAandB = countCallsBetweenClasses(classA, classB);
        int totalBinaryCalls = countTotalBinaryCalls();

        if (totalBinaryCalls == 0) {
            return 0.0;
        }

        return (double) callsBetweenAandB / totalBinaryCalls;
    }

    public void processApplicationCoupling() {
        for (String classA : classes) {
            for (String classB : classes) {
                if (!classA.equals(classB)) {
                    double coupling = calculateCoupling(classA, classB);
                    if(!couplings.containsKey(classA+"-"+classB) && !couplings.containsKey(classB+"-"+classA) && coupling>0) {
                    	couplings.put(classA+"-"+classB, coupling);
                    }
                }
            }
        }
    }
    private void displayCoupling() {
    	System.out.println(couplings);
    }



<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes


}