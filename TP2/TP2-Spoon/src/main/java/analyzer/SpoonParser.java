package analyzer;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.visitor.filter.TypeFilter;
import visitors.VisitorStatistique;

import java.util.List;

public class SpoonParser {
    private final String srcDir;
    private final Launcher launcher;
    private CtModel model;

    public SpoonParser(String srcDir) {
        this.srcDir = srcDir;
        this.launcher = new Launcher();
        this.launcher.addInputResource(srcDir);
        this.launcher.getEnvironment().setComplianceLevel(8);
        this.launcher.buildModel();
        this.model = launcher.getModel();
    }

    public CtModel getModel(){
        return model;
    }

    public void accept(VisitorStatistique visitor) {
        if (model == null) {
            System.out.println("Erreur : Modèle non initialisé.");
            return;
        }

        // Parcourir tous les packages et classes
        List<CtPackage> packages = model.getElements(new TypeFilter<>(CtPackage.class));
        for (CtPackage ctPackage : packages) {
            visitor.scan(ctPackage);
        }

        List<CtClass<?>> classes = model.getElements(new TypeFilter<>(CtClass.class));
        for (CtClass<?> ctClass : classes) {
            visitor.scan(ctClass);
        }
    }

}
