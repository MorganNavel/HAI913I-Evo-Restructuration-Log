package visitors;


import org.eclipse.jdt.core.dom.TypeDeclaration;

public class VisitorNbClass extends Visitor {
	private int nbClasses = 0;
	
	@Override
	public boolean visit(TypeDeclaration node) { 

		this.nbClasses ++;

		return true;
	}

	@Override
	public void displayResult() {
		System.out.println("Nombres de classes analyser: "+nbClasses);
	}
}
