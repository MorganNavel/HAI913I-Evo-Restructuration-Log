package visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public abstract class Visitor extends ASTVisitor {
	private CompilationUnit cu;
	public boolean visit(MethodDeclaration node) { return true;	}
	public boolean visit(FieldDeclaration node) { return true;	}
	public boolean visit(TypeDeclaration node) {return true;}
	public boolean visit(VariableDeclarationStatement node) { return true; }
	public boolean visit(Initializer node) { return true; }
	public boolean visit(ImportDeclaration node) { return true; }

	String getEncapsulation(int mod) {			
		if (Modifier.isPublic(mod)) return "public";
		if (Modifier.isPrivate(mod)) return "private";
		if (Modifier.isProtected(mod)) return "protected";
		return "default"; 
	}

	public void displayResult() {}
	
	public CompilationUnit getCu() {
		return cu;
	}
	
	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}
}
