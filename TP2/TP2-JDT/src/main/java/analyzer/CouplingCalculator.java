package analyzer;

import java.util.List;
import java.util.Map;

public class CouplingCalculator {
    private Map<String, Map<String, List<String>>> callGraph;

    public CouplingCalculator(Map<String, Map<String, List<String>>> callGraph) {
        this.callGraph = callGraph;
    }
    

    public double calculatingCoupling() {
        int relationCount = 0;
        int totalRelationCount = 0;



        return Double.NaN;
    }
}
