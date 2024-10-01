package visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.HashMap;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class VisitorMethodsOfClasses extends Visitor {
    private Map<String, Map<String, List<String>>> callGraph = new HashMap<>();

    @Override
    public boolean visit(TypeDeclaration node) {
        String className = node.getName().getFullyQualifiedName();
        
        MethodDeclaration[] methods = node.getMethods();
        
        for (MethodDeclaration method : methods) {
            String methodName = method.getName().getFullyQualifiedName();
            callGraph.putIfAbsent(className, new HashMap<>());
            callGraph.get(className).put(methodName, new ArrayList<>());

            saveCalledMethods(method, className);
        }
        return true;
    }

    private void saveCalledMethods(MethodDeclaration node, String className) {
        node.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodInvocation methodInvocation) {
                String calledMethodName = methodInvocation.getName().getFullyQualifiedName();
                List<String> methodsPreviouslyCalled = callGraph.get(className).get(node.getName().getFullyQualifiedName());
                if(!methodsPreviouslyCalled.contains(calledMethodName)) {
                	methodsPreviouslyCalled.add(calledMethodName);
                }
                return super.visit(methodInvocation);
            }
        });
    }

    @Override
    public void displayResult() {
        System.out.println("Graphe d'appels de méthodes :");
        for (Map.Entry<String, Map<String, List<String>>> classEntry : callGraph.entrySet()) {
            String className = classEntry.getKey();
            boolean hasMethodCalls = false;
            System.out.println("-Classe : " + className);

            for (Map.Entry<String, List<String>> methodEntry : classEntry.getValue().entrySet()) {
                String methodName = methodEntry.getKey();
                List<String> calledMethods = methodEntry.getValue();
                
                if (!calledMethods.isEmpty()) {
                    System.out.println("\tMéthode : " + methodName + " ----> : " + calledMethods);
                    hasMethodCalls = true;
                }
            }

            if (!hasMethodCalls) {
                System.out.println("\tAucune méthode n'appelle d'autres méthodes dans cette classe.\n");
            } else {
                System.out.println("");
            }
        }
    }

    public void createDotFile() {
        String filename = "callGraph.dot";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
        writer.write("digraph CallGraph {\n");
        writer.write("\trankdir=LR;\n");  // Représentation du graphe de gauche à droite

        for (Map.Entry<String, Map<String, List<String>>> classEntry : callGraph.entrySet()) {
            String className = classEntry.getKey();

            for (Map.Entry<String, List<String>> methodEntry : classEntry.getValue().entrySet()) {
                String methodName = methodEntry.getKey();
                List<String> calledMethods = methodEntry.getValue();

                if (!calledMethods.isEmpty()) {
                    for (String calledMethod : calledMethods) {
                        // Ajouter un lien entre la méthode et celles qu'elle appelle
                        writer.write(String.format("\t\"%s::%s\" -> \"%s\";%n", className, methodName, calledMethod));
                    }
                }
            }
        }

        writer.write("}\n");  // Fin du fichier .dot
        System.out.println("Fichier .dot créé avec succès : " + filename);
        } catch (IOException e) {
            System.out.println("Erreur lors de la création du fichier .dot : " + e.getMessage());
        }
    }

}
