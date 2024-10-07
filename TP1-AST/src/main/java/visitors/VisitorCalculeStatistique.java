package visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class VisitorCalculeStatistique extends Visitor {
	private int nbClasses = 0;
	private int nbMethods = 0;
	private ArrayList<String> packages = new ArrayList<>();
	private int sumMethodsLines = 0;
	private int nbAttributes = 0;
	private HashMap<String,Integer> methodsByClass = new HashMap<>();
	private HashMap<String,Integer> attributesByClass = new HashMap<>();
    private HashMap<String, List<String>> classesTop10PercentMethods = new HashMap<>();
    private int maxParams = 0;
	private String methodsMaxParams = "";

	@Override
	public boolean visit(TypeDeclaration node) { 
		
		int startLine = this.getCu().getLineNumber(node.getStartPosition());
        int endLine = this.getCu().getLineNumber(node.getStartPosition() + node.getLength());
        int loc = endLine - startLine + 1;
        //System.out.println("Class : " + node.getName() + " | Nombre de lignes : " + loc);
        methodsByClass.put(node.getName().toString(), node.getMethods().length);
		this.nbClasses++;
        attributesByClass.put(node.getName().toString(), node.getFields().length);
        HashMap<String, Integer> methods = new HashMap<>();
        for(MethodDeclaration m: node.getMethods()) {
        	methods.put(m.getName().toString(), m.getBody().getLength());
        }
        this.classesTop10PercentMethods.put(node.getName().toString(), this.getTop10PercentClasses(methods, 0.1));
        

		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) { 
		int nbParams = node.parameters().size();
		if (nbParams > maxParams){
			maxParams = nbParams;
			methodsMaxParams = node.getName().toString();
		}
		sumMethodsLines += node.getBody().getLength();
		this.nbMethods ++;
		return true;
	}

	@Override
	public void displayResult() {
		System.out.println("\nNombres de classes analysées: " + nbClasses);
		System.out.println("Nombres de methodes analysées: " + nbMethods);
		System.out.println("Nombres de packages dans l'application: " + packages.size());
		System.out.println("Nombre moyen de méthode par classe: " + getAverageMethodsPerClass());
		System.out.println("Nombre moyen de ligne par méthodes: " + getAverageLinesPerMethod());
		System.out.println("Nombres d'attributs par classes: " + getAverageAttributesPerClass());
		List<String> top10PercentMethod = this.getTop10PercentClasses(this.methodsByClass, 0.1);
		List<String> top10PercentAttributs = this.getTop10PercentClasses(this.attributesByClass, 0.1);

		System.out.println("\nTop 10% Classes avec le plus de méthodes: "+top10PercentMethod);
		System.out.println("Top 10% Classes avec le plus d'attributs: "+top10PercentAttributs);
		ArrayList<String> commons = top10PercentMethod.stream()
			    .filter(top10PercentAttributs::contains)
			    .collect(Collectors.toCollection(ArrayList::new));
		System.out.println("Top 10% Classes avec le plus d'attributs & de méthodes : "+commons);
		System.out.println("Top 10% des methods les plus longue (par classes) : "+this.classesTop10PercentMethods);
		System.out.println("\nMéthode avec le plus de paramètres de l'application : "+this.maxParams);
	}
	
	@Override
	public boolean visit(FieldDeclaration node) { 
		nbAttributes++;
		return true;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		String packageName = node.getName().toString();
		if(!packages.contains(packageName)) packages.add(packageName);
		return true;	
	}

	public List<String> getTop10PercentClasses(HashMap<String, Integer> nbByClasses, double percentile) {

        ArrayList<Integer> methodCounts = new ArrayList<>(nbByClasses.values());

		if (methodCounts.isEmpty()){
			return new ArrayList<>();
		}

        methodCounts.sort(Collections.reverseOrder());

        int topIndex = (int) Math.ceil(methodCounts.size() * percentile);
        if (topIndex == 0) topIndex = 1; 

        int threshold = methodCounts.get(topIndex - 1);

        List<String> topClasses = new ArrayList<>();
        for (Entry<String, Integer> entry : nbByClasses.entrySet()) {
            if (entry.getValue() >= threshold) {
                topClasses.add(entry.getKey());
            }
        }

        return topClasses;
    }


	// Getters
	public int getNbClasses() {
		return nbClasses;
	}

	public int getNbMethods() {
		return nbMethods;
	}

	public int getNbPackages() {
		return packages.size();
	}

	public int getSumMethodsLines() {
		return sumMethodsLines;
	}

	public double getAverageMethodsPerClass() {
		return (double) nbMethods / nbClasses;
	}

	public double getAverageLinesPerMethod() {
		return (double) sumMethodsLines / nbMethods;
	}

	public double getAverageAttributesPerClass() {
		return (double) nbAttributes / nbClasses;
	}

	public HashMap<String, Integer> getMethodsByClass() {
		return methodsByClass;
	}

	public HashMap<String, Integer> getAttributesByClass() {
		return attributesByClass;
	}

	public HashMap<String, List<String>> getClassesTop10PercentMethods() {
		return classesTop10PercentMethods;
	}

	public int getMaxParams() {
		return maxParams;
	}

	public String getMethodsMaxParams() {
		return methodsMaxParams;
	}
}
