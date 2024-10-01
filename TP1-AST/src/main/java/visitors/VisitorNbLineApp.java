package visitors;

import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;


public class VisitorNbLineApp extends Visitor {
	private int nbLignes = 0;


	public boolean visit(TypeDeclaration node) {
		int startLine = this.getCu().getLineNumber(node.getStartPosition());
        int endLine = this.getCu().getLineNumber(node.getStartPosition() + node.getLength());
        int loc = endLine - startLine + 1;
        nbLignes += loc;
        return true;
    }
	public boolean visit(ImportDeclaration node) {
		int startLine = this.getCu().getLineNumber(node.getStartPosition());
        int endLine = this.getCu().getLineNumber(node.getStartPosition() + node.getLength());
        int loc = endLine - startLine + 1;
        nbLignes += loc;
        return true;
    }
	public boolean visit(PackageDeclaration node) {
		int startLine = this.getCu().getLineNumber(node.getStartPosition());
        int endLine = this.getCu().getLineNumber(node.getStartPosition() + node.getLength());
        int loc = endLine - startLine + 1;
        nbLignes += loc;
        return true;
    }
	
	public void displayResult() {
		System.out.println("Nombres de lignes de l'application: "+ nbLignes);
	}
}
