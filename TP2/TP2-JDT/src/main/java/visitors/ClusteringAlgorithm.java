package visitors;
import java.util.ArrayList;
import java.util.List;

public class ClusteringAlgorithm extends VisitorMethodsOfClasses {
    private int maxModules;
    private double couplingThreshold;

    
    public List<List<String>> initializeClusters() {
        List<List<String>> clusters = new ArrayList<>();
        for (String className : classes) {
            List<String> cluster = new ArrayList<>();
            cluster.add(className);
            clusters.add(cluster);
        }
        return clusters;
    }




    // Méthode principale pour effectuer le clustering hiérarchique avec les contraintes
    public List<List<String>> performClustering(int M, double CP) {
        this.maxModules = M == 1 || M == 0 ? 1 : M / 2;
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

        // Filtrer les clusters pour respecter le seuil de couplage moyen
        List<List<String>> filteredClusters = filterClustersByCoupling(clusters);
        
        System.out.println("Clusters finaux après filtrage:");
        displayClusters(filteredClusters);

        return filteredClusters;
    }

    // Filtre les clusters dont le couplage moyen est supérieur à CP
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

    // Calcule le couplage moyen d'un cluster
    private double calculateAverageCoupling(List<String> cluster) {
        double totalCoupling = 0.0;
        int pairCount = 0;

        for (int i = 0; i < cluster.size(); i++) {
            for (int j = i + 1; j < cluster.size(); j++) {
                totalCoupling += calculateCoupling(cluster.get(i), cluster.get(j));
                pairCount++;
            }
        }

        return pairCount == 0 ? 0 : totalCoupling / pairCount;
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
    // Fusionne deux clusters avec le plus grand couplage
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

        // Vérifie si les indices de fusion sont valides
        if (indexA != -1 && indexB != -1) {
            List<String> mergedCluster = new ArrayList<>(clusters.get(indexA));
            mergedCluster.addAll(clusters.get(indexB));
            clusters.remove(indexB);
            clusters.set(indexA, mergedCluster);
        }

        return clusters;
    }

    // Affiche les clusters
    private void displayClusters(List<List<String>> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println("Cluster " + (i + 1) + ": " + clusters.get(i));
        }
        System.out.println();
    }
}
