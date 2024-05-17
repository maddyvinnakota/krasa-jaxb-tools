package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;
import java.util.stream.Collectors;

public class MultiplePatternListBase extends AnnotationsMojoTestHelper {

    public MultiplePatternListBase(ValidationsAnnotation annotation) {
        super("multiplePatterns", annotation);
    }

    @Override
    public String getAnnotationFileName() {
        return "MultiplePatternListBase-annotation.txt";
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.singlePattern, false)
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateStringListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.JSR_349, true)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

    public void test() throws ClassNotFoundException {

        List<String> annotations = element("Multipattern")
                .annotationSimpleName("Pattern")
                .getAnnotations("multiplePatterns");

        assertFalse(annotations.isEmpty());

        String text = annotations.stream()
                .map(s -> s.trim())
                .collect(Collectors.joining("\n"));

        String expected = "@Pattern.List({\n" +
            "@Pattern(regexp = \"[0-9]\"),\n" +
            "@Pattern(regexp = \"[A-B]\")\n" +
            "})";

        assertEquals(expected, text);
    }

}
