package visitors;

import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import utils.Pair;

import static utils.Utils.generatePngFromDotFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class VisitorMethods {
    private final Map<String, Set<String>> methodCalls = new HashMap<>();
    private final Map<Pair<String, String>, Double> couplingGraph = new HashMap<>();

    // Method to launch the analysis of the methods
    public void launchAnalysis(CtModel model) {
        if (model == null) {
            System.out.println("Erreur : Modèle non initialisé.");
            return;
        }

        for (CtMethod<?> method : model.getElements(new TypeFilter<>(CtMethod.class))) {
            scan(method);
        }
    }

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

    // Method to find a class by its name in the model
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

    // Method to calculate the coupling between two classes
    public static double calculateCouplingBetweenClasses(CtModel model, String classA, String classB) {
        int totalRelations = 0;
        int relationsBetweenAAndB = 0;

        for (CtType<?> ctType : model.getAllTypes()) {
            if (ctType instanceof CtClass) {
                CtClass<?> ctClass = (CtClass<?>) ctType;

                for (CtMethod<?> method : ctClass.getMethods()) {
                    totalRelations += countMethodCalls(method);

                    if (ctClass.getSimpleName().equals(classA) || ctClass.getQualifiedName().equals(classA)) {
                        relationsBetweenAAndB += countCallsToClass(method, classB);
                    }
                    if (ctClass.getSimpleName().equals(classB) || ctClass.getQualifiedName().equals(classB)) {
                        relationsBetweenAAndB += countCallsToClass(method, classA);
                    }
                }
            }
        }

        return (double) relationsBetweenAAndB / totalRelations;
    }

    // Method to count the calls to a class in a method
    private static int countCallsToClass(CtMethod<?> method, String className) {
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

    // Method to count the method calls in a method
    private static int countMethodCalls(CtMethod<?> method) {
        return method.getElements(new TypeFilter<>(CtInvocation.class)).size();
    }

    // Method to generate the coupling graph
    public void generateCouplingGraph(CtModel model) {
        List<CtClass<?>> classes = model.getElements(new TypeFilter<>(CtClass.class));

        for (CtClass<?> classA : classes) {
            for (CtClass<?> classB : classes) {
                if (!classA.equals(classB)) {
                    double coupling = calculateCouplingBetweenClasses(model, classA.getSimpleName(), classB.getSimpleName());
                    couplingGraph.put(new Pair<>(classA.getQualifiedName(), classB.getQualifiedName()), coupling);
                }
            }
        }
    }

    // Method to display the coupling graph
    public void displayCouplingGraph() {
        System.out.println("\nGraphe de couplage :");

        // Convert the entry set to a list and sort it
        List<Map.Entry<Pair<String, String>, Double>> sortedEntries = new ArrayList<>(couplingGraph.entrySet());
        sortedEntries.sort(Map.Entry.comparingByKey( (o1, o2) -> {
            if (o1.getLeft().equals(o2.getLeft())) {
                return o1.getRight().compareTo(o2.getRight());
            }
            return o1.getLeft().compareTo(o2.getLeft());
        }));

        for (Map.Entry<Pair<String, String>, Double> entry : sortedEntries) {
            Pair<String, String> classes = entry.getKey();
            double coupling = entry.getValue();
            if (coupling > 0) {
                System.out.println("Couplage entre " + classes.getLeft() + " et " + classes.getRight() + " : " + coupling);
            }
        }
    }

    // Method to create the .dot and .png files of the coupling graph
    public void createCouplingGraph() {
        String directoryName = "couplingGraph";
        String dotFilename = directoryName + "/couplingGraph.dot";
        String pngFilename = directoryName + "/couplingGraph.png";

        // Create the directory if it doesn't exist
        File directory = new File(directoryName);
        if(!directory.exists()) {
            directory.mkdir();
        }

        // Create the .dot file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dotFilename))) {
            writer.write("graph CouplingGraph {\n");

            for (Map.Entry<Pair<String, String>, Double> entry : couplingGraph.entrySet()) {
                Pair<String, String> classes = entry.getKey();
                double coupling = entry.getValue();
                if (coupling > 0) {
                    writer.write("\t\"" + classes.getLeft() + "\" -- \"" + classes.getRight() + "\" [label=\"" + coupling + "\"];\n");
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
}
