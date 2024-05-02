package com.sun.tools.xjc.addon.krasa;

public class MultiplePatternBase extends AnnotationsMojoTestHelper {

    public MultiplePatternBase(JaxbValidationsAnnotation annotation) {
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
