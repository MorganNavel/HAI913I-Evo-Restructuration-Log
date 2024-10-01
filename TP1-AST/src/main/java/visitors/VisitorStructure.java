package visitors;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class VisitorStructure extends Visitor {

	public boolean visit(MethodDeclaration node) {
		String encapsulation = getEncapsulation(node.getModifiers());
	    String methodName = node.getName().getIdentifier();
	    System.out.println("\tNom de la méthode: " + methodName+", Encapsulation "+ encapsulation);
	    return false;  
		
	}
	public boolean visit(FieldDeclaration node) {
		node.getModifiers();
    	
	    String encapsulation = getEncapsulation(node.getModifiers());
	    
	    for (Object fragment : node.fragments()) {
	        VariableDeclarationFragment var = (VariableDeclarationFragment) fragment;
	        String fieldName = var.getName().getIdentifier();
	        System.out.println("\tNom de l'attribut: " + fieldName + ", Encapsulation: " + encapsulation);
	    }
	    
	    return false;
	}
	public boolean visit(TypeDeclaration node) {
		String className = node.getName().getIdentifier();
	    System.out.println("Nom de la classe: " + className);
	
	    if (node.getSuperclassType() != null) {
	        System.out.println("\tSuper classe: " + node.getSuperclassType().toString());
	    }
	
	    return true;
	}
}
