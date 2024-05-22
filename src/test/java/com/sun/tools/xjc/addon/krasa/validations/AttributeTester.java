package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AttributeTester {

    final ArtifactTester parent;
    final String filename;
    final String attributeName;
    final String definition;
    final List<String> annotationList;

    public AttributeTester(ArtifactTester parent, String filename,
            String attributeName, String definition, List<String> annotationList) {
        this.parent = parent;
        this.filename = filename;
        this.attributeName = attributeName;
        this.definition = definition;
        this.annotationList = annotationList;
    }

    public ArtifactTester end() {
        return parent;
    }

    public AttributeTester assertClass(
            Class<?> clazz) {
        String className = clazz.getSimpleName();
        if (!definition.contains(className + " ")) {
            throw new AssertionError("attribute " + attributeName + " in " + filename + " expected of class " + clazz.getName() + " but is: " + definition);
        }
        return this;
    }

    public AttributeTester assertAnnotationNotPresent(String annotation) {
        long counter = annotationList.stream()
                .filter(l -> l.trim().startsWith("@" + annotation)).count();
        if (counter != 0) {
            throw new AssertionError("annotation " + annotation + " of attribute " + attributeName + " in " + filename + " found");
        }
        return this;
    }

    public AnnotationTester annotation(String annotation) {
        String line = annotationList.stream()
                .filter(l -> l.trim().startsWith("@" + annotation)).findFirst()
                .orElseThrow(() -> new AssertionError("annotation " + annotation + " of attribute " + attributeName + " in " + filename + " not found "));
        return new AnnotationTester(this, line, annotation);
    }

    public AttributeTester assertNoAnnotationsPresent() {
        if (!annotationList.isEmpty()) {
            throw new AssertionError("attribute " + attributeName + " in " + filename + " contains annotations: " + annotationList.toString());
        }
        return this;
    }

}
