package visitors;

import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static cli.Utils.generatePngFromDotFile;

public class VisitorMethods {
    private Map<String, Set<String>> methodCalls = new HashMap<>();

    // Method to scan the called methods in a given method
    public void scan(CtMethod<?> method) {
        String methodName = method.getParent(CtClass.class).getSimpleName() + "." + method.getSimpleName();

        methodCalls.putIfAbsent(methodName, new HashSet<>());

        // Iterate over the statements inside the method
        method.getBody().getStatements().forEach(statement -> {
            if (statement instanceof CtInvocation<?>) {
                CtInvocation<?> invocation = (CtInvocation<?>) statement;

                // Get the actual method declaration from the reference
                CtExecutableReference<?> executableReference = invocation.getExecutable();

                if (executableReference != null) {
                    // Add the called method to the list of methods called by the current method
                    methodCalls.get(methodName).add(executableReference.getDeclaringType().getSimpleName() + "." + executableReference.getSimpleName());
                }
            }
        });
    }


    public CtClass<?> findClassByName(CtModel model, String className) {
        for (CtType<?> ctType : model.getAllTypes()) {
            if ((ctType instanceof CtClass ) && (ctType.getSimpleName().equals(className) || ctType.getQualifiedName().equals(className))) {
                return (CtClass<?>) ctType;
            }
        }
        return null;
    }

    // Method to display the call method graph
    public void displayCallGraph() {
        System.out.println("\nGraphe d'appels de méthodes :");

        Map<String, Set<String>> sortedMethodCalls = new TreeMap<>(methodCalls);
        String lastClassName = "";

        for (Map.Entry<String, Set<String>> entry : sortedMethodCalls.entrySet()) {
            String fullMethodName = entry.getKey();
            Set<String> calledMethods = entry.getValue();

            String className = fullMethodName.split("\\.")[0]; // regex to get the class name from the full method name
            String methodName = fullMethodName.split("\\.")[1]; // regex to get the method name from the full method name
            if (!className.equals(lastClassName)) {
                if (!lastClassName.isEmpty()) {
                    System.out.println();
                }
                System.out.println("-Classe : " + className);
                lastClassName = className;
            }

            if (!calledMethods.isEmpty()) {
                System.out.println("\tMéthode : " + methodName + " appelle : " + calledMethods);
            } else {
                System.out.println("\tMéthode : " + methodName + " n'appelle aucune méthode.");
            }
        }
    }


    // Method to create the .dot and .png files of the call graph
    public void createCallGraph() {
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

            Map<String, Set<String>> sortedMethodCalls = new TreeMap<>(methodCalls);

            for (Map.Entry<String, Set<String>> methodEntry : sortedMethodCalls.entrySet()) {
                String methodName = methodEntry.getKey();
                Set<String> calledMethods = methodEntry.getValue();

                for (String calledMethod : calledMethods) {
                    writer.write("\t\"" + methodName + "\" -> \"" + calledMethod + "\";\n");
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

    public double calculateCouplingBetweenClasses(CtModel model, CtClass<?> classA, CtClass<?> classB) {
        int totalRelations = 0;
        int relationsBetweenAAndB = 0;

        for (CtType<?> ctType : model.getAllTypes()) {
            if (ctType instanceof CtClass) {
                CtClass<?> ctClass = (CtClass<?>) ctType;

                for (CtMethod<?> method : ctClass.getMethods()) {
                    totalRelations += countMethodCalls(method);

                    if (ctClass.getSimpleName().equals(classA.getSimpleName())) {
                        relationsBetweenAAndB += countCallsToClass(method, classB.getSimpleName());
                    }
                    if (ctClass.getSimpleName().equals(classB.getSimpleName())) {
                        relationsBetweenAAndB += countCallsToClass(method, classA.getSimpleName());
                    }
                }
            }
        }

        return (double) relationsBetweenAAndB / totalRelations;
    }

    private int countCallsToClass(CtMethod<?> method, String className) {
        List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<>(CtInvocation.class));
        int count = 0;
        for (CtInvocation<?> invocation : invocations) {
            CtExecutableReference<?> executableReference = invocation.getExecutable();
            if (executableReference != null && executableReference.getDeclaringType() != null) {
                CtTypeReference<?> typeReference = executableReference.getDeclaringType();
                if (typeReference.getSimpleName().equals(className)) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countMethodCalls(CtMethod<?> method) {
        return method.getElements(new TypeFilter<>(CtInvocation.class)).size();
    }

    public void displayCouplingGraph() {
    }

    public void createCouplingGraph() {
    }
}
