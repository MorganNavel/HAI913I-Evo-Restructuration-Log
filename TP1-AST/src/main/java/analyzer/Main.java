package analyzer;

import java.io.IOException;

import visitors.VisitorCalculeStatistique;
import visitors.VisitorMethodsOfClasses;

public class Main {
	public static void main(String[] args) {
		ClassAnalyzer ca = new ClassAnalyzer("TP1-AST/src/");
		VisitorCalculeStatistique v = new VisitorCalculeStatistique();
		VisitorMethodsOfClasses vm = new VisitorMethodsOfClasses();

		ca.accept(v);
		try {
			System.out.println("Analyse de la codebase en cours...");
			System.out.println("\nAffichage des classes et de leurs nombres de lignes :");
			ca.run();
			v.displayResult();
			System.out.println("Nombre de ligne de la codebase: "+ca.getCodeLen()+" lignes");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
