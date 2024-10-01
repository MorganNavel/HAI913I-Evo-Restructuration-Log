package visitors;

import java.util.ArrayList;
import org.eclipse.jdt.core.dom.PackageDeclaration;

public class VisitorPackage extends Visitor {
	private ArrayList<String> packages = new ArrayList<>();

	@Override
	public boolean visit(PackageDeclaration node) {
		String packageName = node.getName().toString();
		if(!packages.contains(packageName)) packages.add(packageName);
		return true;	
	}

	@Override
	public void displayResult() {
		System.out.println("Il y a "+ packages.size() +" package(s) dans l'application");
	}

}
