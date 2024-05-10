package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;
import java.util.stream.Collectors;

public class MultiplePatternsWithBase extends AnnotationsMojoTestHelper {

    public MultiplePatternsWithBase(ValidationsAnnotation annotation) {
        super("multiplePatternsWithBase", annotation);
    }

    public void test() throws ClassNotFoundException {
        List<String> annotations = element("MultiPatternWithBase")
                .annotationSimpleName("Pattern")
                .getAnnotations("multiplePatternsWithBase");

        assertFalse(annotations.isEmpty());

        String text = annotations.stream()
                .map(s -> s.trim())
                .collect(Collectors.joining("\n"));

        String expected = "@Pattern(regexp = \"([0-9])|([A-B])|([Y-Z])\")";
        assertEquals(expected, text);
    }

}