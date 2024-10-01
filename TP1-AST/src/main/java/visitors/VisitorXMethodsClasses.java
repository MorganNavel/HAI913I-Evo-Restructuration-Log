package visitors;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.TypeDeclaration;

public class VisitorXMethodsClasses extends Visitor {
	private int threshold;
	private ArrayList<String> classesSupToThreshold = new ArrayList<>();

	public VisitorXMethodsClasses(int threshold) {
		this.threshold = threshold;
	}
	
	public boolean visit(TypeDeclaration node) {
		if(node.getMethods().length > threshold) {
			classesSupToThreshold.add(node.getName().toString());
		}
		return true;
	}
	
	public void displayResult() {
		System.out.println("Classes supérieurs à "+threshold+" méthode(s) : "+classesSupToThreshold);

	}
}
