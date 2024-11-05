package analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import visitors.Visitor;

public class ClassAnalyzer {
    private final ASTParser parser;
    private String srcDir = ".";
    private Visitor v;
    private int codeLen = 0;

    public ClassAnalyzer(String srcDir) {
        this.srcDir = srcDir;
        this.parser = ASTParser.newParser(AST.JLS4);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setStatementsRecovery(true);
	}

    // Method to run the analyzer
    public void run() throws IOException {
        if (!this.srcDir.isEmpty()) {
            Set<String> paths = this.listFilesFromDir(srcDir);
            for (String path : paths) {
                String source = this.readFile(path);
                this.analyze(source);
            }
        }
        //v.displayResult();
    }

    // Method to analyze the source code
    private void analyze(String source) {
        parser.setSource(source.toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        if (cu.getAST() != null && cu.getAST().hasBindingsRecovery()) {
            System.out.println("Bindings recovered successfully.");
        } else {
            // System.out.println("Binding recovery failed or not enabled.");
        }
        v.setCu(cu);
        cu.accept(v);
    }

    // Method to list all files in a directory
	private Set<String> listFilesFromDir(String dir) throws IOException {
	    File rootDir = new File(dir);

	    File[] files = rootDir.listFiles();

	    Set<String> recursiveFoundFiles = new HashSet<>();
	    Set<String> foundFiles = new HashSet<>();

	    if (files != null) {
	        for (File file : files) {
	            if (file.isDirectory()) {
	                recursiveFoundFiles.addAll(listFilesFromDir(file.getPath()));
	            }
	            if (file.isFile()) {
	                String path = file.getPath();
	                if(path.endsWith(".java"))	foundFiles.add(path);
	            }
	        }
	    }
	    foundFiles.addAll(recursiveFoundFiles);
	    return foundFiles;
	}

    // Method to accept a visitor
	public void accept(Visitor v) {
		this.v = v;
	}

    // Method to get the number of lines of code
	public int getCodeLen() {
		return this.codeLen;
	}

    // Method to read the content of a file
    private String readFile(String path) throws IOException {
        StringBuilder contenu = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                this.codeLen++;
                contenu.append(ligne).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        return contenu.toString();
    }
}
