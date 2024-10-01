package myclasses;

import java.io.IOException;

import visitors.VisitorCalculeStatistique;
import visitors.VisitorMethodsOfClasses;
import visitors.VisitorNbLineApp;
import visitors.VisitorPackage;
import visitors.VisitorStructure;
import visitors.VisitorXMethodsClasses;

public class Main {
	public static void main(String[] args) {
		ClassAnalyzer ca = new ClassAnalyzer("TP1-AST/src/");
//		VisitorStructure v = new VisitorStructure();
		VisitorCalculeStatistique v = new VisitorCalculeStatistique();
//		VisitorXMethodsClasses v = new VisitorXMethodsClasses(2);

//		VisitorNbLineApp v = new VisitorNbLineApp();
//		VisitorPackage v = new VisitorPackage();
//		VisitorMethodsOfClasses v = new VisitorMethodsOfClasses();

		ca.accept(v);
		try {
			ca.run();
			v.displayResult();
			System.out.println("Nombre de ligne de la codebase: "+ca.getCodeLen()+" lignes");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
