package com.sun.tools.xjc.addon.krasa.validations;

public class MultiplePatternBase extends AnnotationsMojoTestHelper {

    public MultiplePatternBase(ValidationsAnnotation annotation) {
        super("multiplePatterns", annotation);
    }

    public void test() throws ClassNotFoundException {
        element("Multipattern")
                .annotationSimpleName("Pattern")
                .attribute("multiplePatterns")
                    .annotation("Pattern")
                        .assertParam("regexp", "([0-9])|([A-B])");
    }

}
