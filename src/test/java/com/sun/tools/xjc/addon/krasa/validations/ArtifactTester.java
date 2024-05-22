package com.sun.tools.xjc.addon.krasa.validations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArtifactTester {

    private final String filename;
    private final List<String> lines;
    private final RunXJC2MojoTestHelper outer;

    ArtifactTester(String filename, List<String> lines, final RunXJC2MojoTestHelper outer) {
        this.outer = outer;
        this.filename = filename;
        this.lines = lines;
    }

    public ArtifactTester annotationSimpleName(String simpleName) throws ClassNotFoundException {
        String canonicalName = outer.getAnnotation().getCanonicalClassName(simpleName);
        return annotationCanonicalName(canonicalName);
    }

    public ArtifactTester annotationCanonicalName(String canonicalName) {
        Objects.requireNonNull(canonicalName);
        lines.stream()
                .filter(s -> s.contains(canonicalName)).findAny()
                .orElseThrow(() -> new AssertionError("annotation not found: " + canonicalName));
        return this;
    }

    public AttributeTester classAnnotations() {
        final String clazzName = filename.replace(".java", "");
        int line = getLineForClass(clazzName);
        List<String> annotationList = getAnnotations(clazzName, line);
        String definition = lines.get(line);
        return new AttributeTester(this, filename, clazzName, definition, annotationList);
    }

    public AttributeTester attribute(String attributeName) {
        int line = getLineForAttribute(attributeName);
        List<String> annotationList = getAnnotations(attributeName, line);
        String definition = lines.get(line);
        return new AttributeTester(this, filename, attributeName, definition, annotationList);
    }

    public List<String> getAnnotations(String attributeName) {
        int line = getLineForAttribute(attributeName);
        return getAnnotations(attributeName, line);
    }

    private List<String> getAnnotations(String attributeName, int line) {
        int prevAttribute = prevAttributeLine(attributeName, line);
        List<String> annotationList = lines.subList(prevAttribute, line);
        return annotationList;
    }

    public RunXJC2MojoTestHelper end() {
        return outer;
    }

    private int getLineForClass(String className) {
        for (int i = 0, l = lines.size(); i < l; i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("public class " + className)) {
                return i;
            }
        }
        throw new AssertionError("attribute " + className + " not found in file " + filename);
    }

    List<String> getAllAttributes() {
        List<String> list = new ArrayList<>();
        for (int i = 0, l = lines.size(); i < l; i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("protected ") && line.endsWith(";")) {
                int idx = line.lastIndexOf(' ') + 1;
                String attrName = line.substring(idx, line.length() - 1);
                list.add(attrName);
            }
        }
        return list;
    }

    private int getLineForAttribute(String attributeName) {
        for (int i = 0, l = lines.size(); i < l; i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("protected ") && line.endsWith(attributeName + ";")) {
                return i;
            }
        }
        throw new AssertionError("attribute " + attributeName + " not found in file " + filename);
    }

    private int prevAttributeLine(String attributeName, int attributeLine) {
        for (int i = attributeLine - 1; i >= 0; i--) {
            String line = lines.get(i).trim();
            if (line.trim().isEmpty() || line.startsWith("public ") || line.startsWith("protected ")) {
                return i + 1;
            }
        }
        throw new AssertionError("cannot extract validatitions for " + attributeName + " in file " + filename);
    }

}
