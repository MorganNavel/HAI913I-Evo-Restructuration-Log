package visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.HashMap;
import java.util.List;
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

    public void displayResult() {
        System.out.println("Graphe d'appels de méthodes :");
        for (Map.Entry<String, Map<String, List<String>>> classEntry : callGraph.entrySet()) {
            String className = classEntry.getKey();
            boolean hasMethodCalls = false;
            System.out.println("Classe : " + className);

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

}
