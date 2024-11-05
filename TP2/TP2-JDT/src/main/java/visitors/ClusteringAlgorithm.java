package visitors;

import java.util.ArrayList;
import java.util.List;

public class ClusteringAlgorithm extends CouplingCalculator {
    private double couplingThreshold;

    // Method to initialize the clusters
    public List<List<String>> initializeClusters() {
        List<List<String>> clusters = new ArrayList<>();
        for (String className : classes) {
            List<String> cluster = new ArrayList<>();
            cluster.add(className);
            clusters.add(cluster);
        }
        return clusters;
    }

    // Method to perform hierarchical clustering with constraints
    public List<List<String>> performClustering(int M, double CP) {
        int maxModules;
        if (M == 1  || M == 0){
            maxModules = 1;
        } else {
            maxModules = M / 2;
        }
        this.couplingThreshold = CP;
        List<List<String>> clusters = initializeClusters();
        int i = 0;
        while (clusters.size() > maxModules) {
        	System.out.println("Itération n°"+i);
        	displayClusters(clusters);
            clusters = mergeClusters(clusters);
            i++;
        }
        System.out.println("Clusters finaux");

        // Filter clusters to respect the average coupling threshold
        List<List<String>> filteredClusters = filterClustersByCoupling(clusters);
        
        System.out.println("Clusters finaux après filtrage:");
        System.out.println(filteredClusters);
        //displayClusters(filteredClusters);

        return filteredClusters;
    }

    // Method to filter clusters with average coupling greater than CP
    private List<List<String>> filterClustersByCoupling(List<List<String>> clusters) {
    	displayClusters(clusters);
        List<List<String>> validClusters = new ArrayList<>();

        for (List<String> cluster : clusters) {
            double avgCoupling = calculateAverageCoupling(cluster);
            if (avgCoupling > couplingThreshold) {
                validClusters.add(cluster);
            }
        }

        return validClusters;
    }

    // Method to calculate the average coupling of a cluster
    private double calculateAverageCoupling(List<String> cluster) {
        double totalCoupling = 0.0;
        int pairCount = 0;


        for (int i = 0; i < cluster.size(); i++) {
            for (int j = i + 1; j < cluster.size(); j++) {
                totalCoupling += calculateCoupling(cluster.get(i), cluster.get(j));
                pairCount++;
            }
        }
        if (pairCount == 0) {
            return 0;
        }
        return totalCoupling / pairCount;

    }
    public double calculateDistance(List<String> clusterA, List<String> clusterB) {
        double totalDistance = 0.0;
        for (String classA : clusterA) {
            for (String classB : clusterB) {
                totalDistance += calculateCoupling(classA, classB);
            }
        }
        return totalDistance / (clusterA.size() * clusterB.size());
    }

    // Method to merge two clusters with the highest coupling
    public List<List<String>> mergeClusters(List<List<String>> clusters) {
        double maxCoupling = -1;
        int indexA = -1, indexB = -1;

        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double coupling = calculateDistance(clusters.get(i), clusters.get(j));
                if (coupling > maxCoupling) {
                    maxCoupling = coupling;
                    indexA = i;
                    indexB = j;
                }
            }
        }

        // Verify if the merge indices are valid
        if (indexA != -1 && indexB != -1) {
            List<String> mergedCluster = new ArrayList<>(clusters.get(indexA));
            mergedCluster.addAll(clusters.get(indexB));
            clusters.remove(indexB);
            clusters.set(indexA, mergedCluster);
        }

        return clusters;
    }

    // Method to display the clusters
    private void displayClusters(List<List<String>> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println("Cluster " + (i + 1) + ": " + clusters.get(i) + "\n");
        }
    }
}
