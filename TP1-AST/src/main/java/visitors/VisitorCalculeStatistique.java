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
	public int nbClasses = 0;
	public int nbMethods = 0;
	private ArrayList<String> packages = new ArrayList<String>();
	public int sumMethodsLines = 0;
	public int nbAttributes = 0;
	public HashMap<String,Integer> nbMethodsByClass = new HashMap<>();
	public HashMap<String,Integer> nbAttributesByClass = new HashMap<>();
    HashMap<String, List<String>> classesTop10PercentMethods = new HashMap<>();
    private int maxParams = 0;



	public boolean visit(TypeDeclaration node) { 
		
		int startLine = this.getCu().getLineNumber(node.getStartPosition());
        int endLine = this.getCu().getLineNumber(node.getStartPosition() + node.getLength());
        int loc = endLine - startLine + 1;
        System.out.println("Class : " + node.getName() + " | Nombre de lignes : " + loc);
        nbMethodsByClass.put(node.getName().toString(), node.getMethods().length);
		this.nbClasses ++;
        nbAttributesByClass.put(node.getName().toString(), node.getFields().length);
        HashMap<String, Integer> methods = new HashMap<>();
        for(MethodDeclaration m: node.getMethods()) {
        	methods.put(m.getName().toString(), m.getBody().getLength());
        }
        this.classesTop10PercentMethods.put(node.getName().toString(), this.getTop10PercentClasses(methods, 0.1));
        

		return true;
	}

	public boolean visit(MethodDeclaration node) { 
		int nbParams = node.parameters().size();
		if (nbParams > maxParams) maxParams = nbParams;
		sumMethodsLines += node.getBody().getLength();
		this.nbMethods ++;
		return true;
	}

	public void displayResult() {
		System.out.println("\nNombres de classes analysées: "+nbClasses);
		System.out.println("Nombres de methodes analysées: "+nbMethods);
		System.out.println("Nombres de packages dans l'application: "+packages.size());
		System.out.println("Nombre moyen de méthode par classe: "+((double) nbMethods/nbClasses));
		System.out.println("Nombre moyen de ligne par méthodes: "+((double) sumMethodsLines/nbMethods));
		System.out.println("Nombres d'attributs par classes: "+((double) nbAttributes/nbClasses));
		List<String> top10PercentMethod = this.getTop10PercentClasses(this.nbMethodsByClass, 0.1);
		List<String> top10PercentAttributs = this.getTop10PercentClasses(this.nbAttributesByClass, 0.1);

		System.out.println("\nTop 10% Classes avec le plus de méthodes: "+top10PercentMethod);
		System.out.println("Top 10% Classes avec le plus d'attributs: "+top10PercentAttributs);
		ArrayList<String> commons = top10PercentMethod.stream()
			    .filter(top10PercentAttributs::contains)
			    .collect(Collectors.toCollection(ArrayList::new));
		System.out.println("Top 10% Classes avec le plus d'attributs & de méthodes : "+commons);
		System.out.println("Top 10% des methods les plus longue (par classes) : "+this.classesTop10PercentMethods);
		System.out.println("\nMéthode avec le plus de paramètres de l'application : "+this.maxParams);
	}
	
	public boolean visit(FieldDeclaration node) { 
		nbAttributes++;
		return true;
	}

	public boolean visit(PackageDeclaration node) {
		String packageName = node.getName().toString();
		if(!packages.contains(packageName)) packages.add(packageName);
		return true;	
	}

	public List<String> getTop10PercentClasses(HashMap<String, Integer> nbByClasses, double percentile) {

        ArrayList<Integer> methodCounts = new ArrayList<>(nbByClasses.values());

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

}
