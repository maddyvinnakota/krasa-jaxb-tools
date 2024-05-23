package com.sun.tools.xjc.addon.krasa.validations;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati 
 */
public class AnnotationTester {

    private final AttributeTester parent;
    private final String line;
    private final String annotation;
    private final Map<String, String> valueMap = new HashMap<>();

    public AnnotationTester(AttributeTester parent, String line,
            String annotationName) {
        this.parent = parent;
        this.line = line;
        this.annotation = annotationName;
        parseAnnotationValues(line);
    }

    private void parseAnnotationValues(String line) {
        String values = "";
        if (line.contains("(")) {
            int start = line.indexOf("(");
            values = line.substring(start + 1, line.length() - 1);
        }
        if (!values.trim().isEmpty()) {
            if (line.contains("=")) {
                String[] pairs = values.split(",");
                for (String p : pairs) {
                    String[] kv = p.split("=");
                    valueMap.put(kv[0].trim(), kv[1].trim());
                }
            } else {
                valueMap.put("value", values);
            }
        }
    }

    public AttributeTester end() {
        return parent;
    }

    public AttributeTester assertNoValues() {
        if (!valueMap.isEmpty()) {
            throw new AssertionError("annotation " + annotation +
                    " of attribute " + parent.attributeName +
                    " in " + parent.filename + " not empty: " + valueMap);
        }
        return parent;
    }

    public AttributeTester assertValue(Object value) {
        assertParam("value", value);
        return parent;
    }

    public AnnotationTester assertParam(String name, Object value) {
        Objects.requireNonNull(value, "parameter " + name + " value cannot be null");
        String v = valueMap.get(name);
        if (v == null) {
            throw new AssertionError("annotation " + annotation +
                    " of attribute " + parent.attributeName +
                    " in " + parent.filename + " not found: " + valueMap);
        }
        while (v.startsWith("\"")) {
            v = v.substring(1, v.length() - 1);
        }
        if (!v.equals(value.toString())) {
            throw new AssertionError("annotation " + annotation +
                    " of attribute " + parent.attributeName +
                    " in " + parent.filename +
                    " mismatched value, expected: " + value + " found " + v);
        }
        return this;
    }

}
