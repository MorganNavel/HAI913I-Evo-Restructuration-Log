package visitors;

import org.eclipse.jdt.core.dom.*;

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

                String calledClassName = getString(methodInvocation);

                // Enregistrer l'appel de méthode avec la classe appelante et la classe appelée
                String fullMethodCall = className + "::" + node.getName().getFullyQualifiedName() + " ---> "
                                        + calledClassName + "::" + calledMethodName;

                List<String> methodsPreviouslyCalled = callGraph.get(className).get(node.getName().getFullyQualifiedName());
                if (!methodsPreviouslyCalled.contains(fullMethodCall)) {
                    methodsPreviouslyCalled.add(fullMethodCall);
                }

                return super.visit(methodInvocation);
            }

            private String getString(MethodInvocation methodInvocation) {
                Expression expression = methodInvocation.getExpression();
                String calledClassName = "unknown";

                if (expression != null) {
                    ITypeBinding typeBinding = expression.resolveTypeBinding();
                    if (typeBinding != null) {
                        calledClassName = typeBinding.getQualifiedName();
                    }
                }





                return calledClassName;
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

        for (Map.Entry<String, Map<String, List<String>>> classEntry : callGraph.entrySet()) {
            String className = classEntry.getKey();

            for (Map.Entry<String, List<String>> methodEntry : classEntry.getValue().entrySet()) {
                String methodName = methodEntry.getKey();
                List<String> calledMethods = methodEntry.getValue();

                for (String calledMethod : calledMethods) {
                    writer.write(String.format("\t\"%s::%s\" -> \"%s\";%n", className, methodName, calledMethod));
                }
            }
        }

        writer.write("}\n");
        System.out.println("Fichier .dot créé avec succès : " + filename);
        } catch (IOException e) {
            System.out.println("Erreur lors de la création du fichier .dot : " + e.getMessage());
        }
    }

}
