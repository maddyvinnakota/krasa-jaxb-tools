package com.sun.tools.xjc.addon.krasa.validations;

public abstract class ArrayBase extends AnnotationsMojoTestHelper {

    public ArrayBase(JaxbValidationsAnnotation annotation) {
        super("array", annotation);
    }

    public void test() throws ClassNotFoundException {
        element("Array")
                .annotationSimpleName("Size")
                .annotationSimpleName("NotNull")
                .attribute("arrayOfBytes")
                        .annotation("Size").assertParam("max", 18).end()
                        .annotation("NotNull").assertNoValues();
    }

}
