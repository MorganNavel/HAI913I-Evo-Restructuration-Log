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
//        Map<String, String> options = JavaCore.getOptions();
//        JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
//        parser.setCompilerOptions(options);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setStatementsRecovery(true);
//        String[] classPaths = new String[] {
//                "/home/morgan/Documents/M2/HAI913I-Evo-Restructuration-Log/TP1-AST/target/classes/",
//            };
//
//            String[] sourceDirectories = new String[] {
//                "/home/morgan/Documents/M2/HAI913I-Evo-Restructuration-Log/TP1-AST/src/main/java/"
//            };
//            parser.setEnvironment(classPaths, sourceDirectories, null, true);
	}

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

	public void accept(Visitor v) {
		this.v = v;
	}
	
	public int getCodeLen() {
		return this.codeLen;
	}
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
