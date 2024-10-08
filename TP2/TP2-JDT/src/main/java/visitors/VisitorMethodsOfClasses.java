package visitors;

import org.eclipse.jdt.core.dom.*;

import java.util.HashMap;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class VisitorMethodsOfClasses extends Visitor {
    private Map<String, Map<String, List<Map<String, String>>>> callGraph = new HashMap<>();

    @Override
    public boolean visit(TypeDeclaration node) {
        String className = node.getName().getFullyQualifiedName();

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

                List<Map<String, String>> methodsPreviouslyCalled = callGraph.get(className).get(node.getName().getFullyQualifiedName());
                methodsPreviouslyCalled.add(methodCallInfo);
                
                return super.visit(methodInvocation);
            }



        });
    }

    @Override
    public void displayResult() {
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
            } else {
                System.out.println();
            }
        }
    }

    // Method to create a .dot file representing the call graph
    public void createDotFile() {
        String filename = "callGraph.dot";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
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
                	if(receiverType != "Unknown") {                		
                		writer.write(String.format("\t \"%s::%s\" -> \"%s\" [label=\"%s\"];%n", className, methodName, receiverType, method ));
                	}
                }
            }
        }

        writer.write("}\n");
        System.out.println("Fichier .dot créé avec succès : " + filename);
        } catch (IOException e) {
            System.out.println("Erreur lors de la création du fichier .dot : " + e.getMessage());
        }
    }
    private String resolveType(Expression expr, MethodDeclaration method, String className ,TypeDeclaration currentClass) {
    	if(expr == null) return "Unknown";
    	if(expr.toString().equals("System.out")) {
    		return "Unknown";
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
        if (!variableType.equals("Unknown")) {
            return variableType;
        }

        // Sinon, vérifie si c'est un attribut de la classe
        String attributeType = getClassOfAttributes(expr.toString(), currentClass);
        if (!attributeType.equals("Unknown")) {
            return attributeType;
        }

        // Par défaut, retourne "Unknown"
        return "Unknown";
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
        return "Unknown";
    }

    // Recherche le type d'un attribut dans la classe
    private String getClassOfAttributes(String attributeName, TypeDeclaration currentClass) {
        // Parcourt les champs (attributs) de la classe pour trouver une correspondance
        for (FieldDeclaration field : currentClass.getFields()) {
            for (Object fragment : field.fragments()) {
                VariableDeclarationFragment varFragment = (VariableDeclarationFragment) fragment;
                if (varFragment.getName().getFullyQualifiedName().equals(attributeName)) {
                    return field.getType().toString();  // Retourne le type de l'attribut de la classe
                }
            }
        }
        return "Unknown";
    }


}
