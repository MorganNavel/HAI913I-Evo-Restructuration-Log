package analyzer;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import visitors.VisitorMethods;
import visitors.VisitorStatistique;

import java.util.List;

public class SpoonParser {
    private final String srcDir;
    private final Launcher launcher;
    private final CtModel model;

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

}
