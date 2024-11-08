package visitors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static cli.Utils.generatePngFromDotFile;

public class CouplingCalculator extends VisitorMethodsOfClasses {
    protected final HashMap<String, Double> couplings = new HashMap<>();

    // Method to calculate the coupling between two classes
    public double calculateCoupling(String classA, String classB) {
        int callsBetweenAandB = countCallsBetweenClasses(classA, classB);
        int totalBinaryCalls = countTotalBinaryCalls(classA, classB);
        if (totalBinaryCalls == 0){
            return 0.0;
        }
        System.out.println("Couplage "+classA+ " <--> "+classB+": "+(double) callsBetweenAandB / totalBinaryCalls);
        return (double) callsBetweenAandB / totalBinaryCalls;
    }

    // Method to count the number of calls between methods of two classes A and B
    private int countCallsBetweenClasses(String classA, String classB) {
        int count = 0;
        for (Entry<String, Map<String, List<Map<String, String>>>> classEntry : callGraph.entrySet()) {
            String className = classEntry.getKey();
            if (className.equals(classA) || className.equals(classB)) {
                for (Entry<String, List<Map<String, String>>> methodEntry : classEntry.getValue().entrySet()) {
                    List<Map<String, String>> calledMethods = methodEntry.getValue();
                    for (Map<String, String> calledMethod : calledMethods) {
                        String receiverType = calledMethod.get("receiverType");
                        if (receiverType.equals(VisitorMethodsOfClasses.UNKNOWN)) continue;
                        if ((className.equals(classA) && classB.equals(receiverType)) || (className.equals(classB) && classA.equals(receiverType))) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    // Method to count the total number of binary calls between methods of all classes
    private int countTotalBinaryCalls(String classA, String classB) {
        int count = 0;
        for (Entry<String, Map<String, List<Map<String, String>>>> methodsInClass : callGraph.entrySet()) {
        	String className = methodsInClass.getKey();
        	if(className.equals(classA) || className.equals(classB)) {
              for (List<Map<String, String>> calledMethods : methodsInClass.getValue().values()) {
                  for (Map<String, String> calledMethod : calledMethods) {
                      String receiverType = calledMethod.get("receiverType");
                      if (!receiverType.equals(VisitorMethodsOfClasses.UNKNOWN)) {
                          count++;
                      }
                  }
              }
        	}
 
        }
        return count;
    }

    // Method to process the coupling between all classes
    public void processApplicationCoupling() {
        for (String classA : classes) {
            for (String classB : classes) {
                if (!classA.equals(classB)) {
                    double coupling = calculateCoupling(classA, classB);
                    if (!couplings.containsKey(classA + "-" + classB) && !couplings.containsKey(classB + "-" + classA) && coupling > 0) {
                        couplings.put(classA + "-" + classB, coupling);
                    }
                }
            }
        }
        //displayCoupling();
    }

    // Method to calculate the average coupling of a cluster
    public double calculateClusterCoupling(String className, List<String> cluster) {
        double totalCoupling = 0.0;
        for (String clusterClass : cluster) {
            totalCoupling += calculateCoupling(className, clusterClass);
        }
        return totalCoupling / cluster.size();
    }

    // Method to display the coupling between all classes
    public void displayCoupling() {
        processApplicationCoupling();
        System.out.println("Couplage entre Classes : \n");
        couplings.forEach((classes, couplage) -> System.out.println(classes + " : " + couplage));
    }

    // Method to create the .dot and .png files of the coupling graph
    public void createCouplingGraph() {
        String directoryName = "couplingGraph";
        String dotFilename = directoryName + "/couplingGraph.dot";
        String pngFilename = directoryName + "/couplingGraph.png";
        this.processApplicationCoupling();


        // Create the directory if it doesn't exist
        File directory = new File(directoryName);
        if(!directory.exists()) {
            directory.mkdir();
        }

        // Create the .dot file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dotFilename))) {
            writer.write("digraph CouplingGraph {\n");
            writer.write("\trankdir=LR;\n");  // Representation left to right

            couplings.forEach((classes, couplage) -> {
                System.out.println(classes);
                String [] splittedClasses =  classes.split("-");
                try {
                    writer.write(String.format("\t \"%s\" -> \"%s\" [label=\"%s\"];%n", splittedClasses[0], splittedClasses[1], couplage));

                } catch (IOException e) {
                    System.out.println("Erreur lors de la création du fichier .dot : " + e.getMessage());
                }

            });

            writer.write("}\n");
            System.out.println("\nFichier .dot créé avec succès : " + dotFilename);
        } catch (IOException e) {
            System.out.println("Erreur lors de la création du fichier .dot : " + e.getMessage());
        }

        // Create the .png file from the .dot file
        generatePngFromDotFile(dotFilename, pngFilename);
    }
}

