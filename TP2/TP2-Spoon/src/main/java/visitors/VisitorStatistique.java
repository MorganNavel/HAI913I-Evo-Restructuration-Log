package visitors;

import analyzer.SpoonParser;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.*;
import java.util.stream.Collectors;

public class VisitorStatistique extends CtScanner {
    public ArrayList<String> classes = new ArrayList<>();
    public ArrayList<String> packages = new ArrayList<>();
    private int methodCount = 0;
    private int totalLineCount = 0;
    private int totalAttributesCount = 0;
    private int maxParameters = 0;
    private final List<String> methodsWithMaxParams = new ArrayList<>();
    private final Map<String, Integer> methodsByClass = new HashMap<>();
    private final Map<String, Integer> attributesByClass = new HashMap<>();
    private final Map<String, Integer> lineCountByMethod = new HashMap<>();

    // Method to launch the analysis of the application
    public void launchAnalysis(CtModel model) {
        if (model == null) {
            System.out.println("Erreur : Modèle non initialisé.");
            return;
        }

        List<CtPackage> packages = model.getElements(new TypeFilter<>(CtPackage.class));
        for (CtPackage ctPackage : packages) {
            scan(ctPackage);
        }

        List<CtClass<?>> classes = model.getElements(new TypeFilter<>(CtClass.class));
        for (CtClass<?> ctClass : classes) {
            scan(ctClass);
        }
    }

    // Method to scan the classes of the application
    public void scan(CtClass<?> ctClass) {
        String className = ctClass.getQualifiedName();
        classes.add(className);

        // Count methods and attributes per class
        int methodCountForClass = ctClass.getMethods().size();
        int attributeCountForClass = ctClass.getFields().size();
        methodsByClass.put(className, methodCountForClass);
        attributesByClass.put(className, attributeCountForClass);

        methodCount += methodCountForClass;
        totalAttributesCount += attributeCountForClass;

        // Browse methods for counting lines and parameters
        for (CtMethod<?> method : ctClass.getMethods()) {
            int lineCount = 0;
            if (method.getBody() != null) {
                lineCount = method.getBody().getStatements().size();
            }
            totalLineCount += lineCount;
            lineCountByMethod.put(method.getSignature(), lineCount);

            int paramCount = method.getParameters().size();
            if (paramCount > maxParameters) {
                maxParameters = paramCount;
                methodsWithMaxParams.clear();
                methodsWithMaxParams.add(method.getSignature());
            } else if (paramCount == maxParameters) {
                methodsWithMaxParams.add(method.getSignature());
            }
        }

        super.visitCtClass(ctClass);
    }

    // Method to scan the packages of the application
    @Override
    public void visitCtPackage(CtPackage ctPackage) {
        if (!packages.contains(ctPackage.getQualifiedName()) && !ctPackage.getQualifiedName().isEmpty()) {
            packages.add(ctPackage.getQualifiedName());
        }
        super.visitCtPackage(ctPackage);
    }

    // Getters for classes and packages
    public List<String> getClasses() {
        return classes;
    }

    public List<String> getPackages() {
        return packages;
    }

    // Getters for statistics
    public int getClassCount() {
        return classes.size();
    }

    public int getMethodCount() {
        return methodCount;
    }

    public int getPackageCount() {
        return packages.size();
    }

    public int getLineCount() {
        return totalLineCount;
    }

    public double getAverageMethodsPerClass() {
        if (getClassCount() > 0) {
            return (double) methodCount / getClassCount();
        } else {
            return 0;
        }
    }

    public double getAverageLinesPerMethod() {
        if (methodCount > 0) {
            return (double) totalLineCount / methodCount;
        } else {
            return 0;
        }
    }

    public double getAverageAttributesPerClass() {
        if (getClassCount() > 0) {
            return (double) totalAttributesCount / getClassCount();
        } else {
            return 0;
        }
    }

    public int getMaxParameters() {
        return maxParameters;
    }

    public List<String> getMethodWithMaxParameters() {
        return methodsWithMaxParams;
    }

    // Method to get the top 10% of classes with the most methods
    public List<String> getTopClassesByMethods() {
        return getTop10Percent(methodsByClass);
    }

    // Method to get the top 10% of classes with the most attributes
    public List<String> getTopClassesByAttributes() {
        return getTop10Percent(attributesByClass);
    }

    // Method to get the top 10% of methods with the most lines of code
    public List<String> getTop10PercentMethodsByLineCount() {
        return getTop10Percent(lineCountByMethod);
    }

    // Method to get classes with more than X methods
    public Map<String, Integer> getClassesWithMoreThanXMethods(int x) {
        return methodsByClass.entrySet().stream()
                .filter(entry -> entry.getValue() > x)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Method to get the top 10% of a given metric
    private List<String> getTop10Percent(Map<String, Integer> metricMap) {
        int threshold = (int) Math.ceil(metricMap.size() * 0.1);
        return metricMap.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(threshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
