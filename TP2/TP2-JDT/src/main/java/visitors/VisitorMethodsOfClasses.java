package visitors;

import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import static cli.Utils.generatePngFromDotFile;

public class VisitorMethodsOfClasses extends Visitor {
    protected static final String UNKNOWN = "Unknown";
    protected final Map<String, Map<String, List<Map<String, String>>>> callGraph = new HashMap<>();
    protected final ArrayList<String> classes = new ArrayList<>();

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
                if(!classes.contains(receiverType) && !receiverType.equals(UNKNOWN)) classes.add(receiverType);

                List<Map<String, String>> methodsPreviouslyCalled = callGraph.get(className).get(node.getName().getFullyQualifiedName());
                methodsPreviouslyCalled.add(methodCallInfo);
                
                return super.visit(methodInvocation);
            }
        });
    }


    @Override
    public void displayResult() {
        System.out.println("\nClasses analysées : " + classes);
        System.out.println("\nGraphe d'appels de méthodes :");
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
        generatePngFromDotFile(dotFilename, pngFilename);
    }

    // Method to resolve the type of expression within a method, returning the class name or "Unknown" if it cannot be determined
    private String resolveType(Expression expr, MethodDeclaration method, String className, TypeDeclaration currentClass) {
        if(expr == null || expr.toString().equals("System.out")) {
            return UNKNOWN;
        }

        // if it's "this", return the name of the current class
        if (expr.toString().equals("this")) {
            return className;
        }

        // Checks if it's a call to a static method (assuming the first letter is uppercase)
        if (Character.isUpperCase(expr.toString().charAt(0))) {
            return expr.toString();  // Le nom de la classe
        }

        // If the expression is a variable, check whether it is declared locally in the method
        String variableType = getVariableTypeInMethod(expr.toString(), method);
        if (!variableType.equals(UNKNOWN)) {
            return variableType;
        }

        // Else check if it's an attribute of the class
        String attributeType = getClassOfAttributes(expr.toString(), currentClass);
        if (!attributeType.equals(UNKNOWN)) {
            return attributeType;
        }

        // By default, return "Unknown"
        return UNKNOWN;
    }

    // Method to search for a variable in the local declarations of the method
    private String getVariableTypeInMethod(String variableName, MethodDeclaration method) {
        // Scans variable declarations in the method to find a match
        for (Object statement : method.getBody().statements()) {
            if (statement instanceof VariableDeclarationStatement) {
                VariableDeclarationStatement varDecl = (VariableDeclarationStatement) statement;
                for (Object fragment : varDecl.fragments()) {
                    VariableDeclarationFragment varFragment = (VariableDeclarationFragment) fragment;
                    if (varFragment.getName().getFullyQualifiedName().equals(variableName)) {
                        return varDecl.getType().toString();  // Returns the type of the local variable
                    }
                }
            }
        }
        return UNKNOWN;
    }

    // Method to find the type of attribute in the class
    private String getClassOfAttributes(String attributeName, TypeDeclaration currentClass) {
        // Scans class fields (attributes) to find a match
        for (FieldDeclaration field : currentClass.getFields()) {
            for (Object fragment : field.fragments()) {
                VariableDeclarationFragment varFragment = (VariableDeclarationFragment) fragment;
                if (varFragment.getName().getFullyQualifiedName().equals(attributeName)) {
                    return field.getType().toString();  // Returns the type of the class attribute
                }
            }
        }
        return UNKNOWN;
    }

}