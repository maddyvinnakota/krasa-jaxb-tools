package com.sun.tools.xjc.addon.krasa;

public class NillableBase extends AnnotationsMojoTestHelper {

    public NillableBase(ValidationAnnotation annotation) {
        super("nillable", annotation);
    }

    public void test() throws ClassNotFoundException {
        element("Nillable")
                .annotationSimpleName("NotNull")
                .attribute("notNullable")
                        .annotation("NotNull").assertNoValues()
                .end()
                .attribute("nullable")
                        .assertNoAnnotationsPresent();
    }

}
