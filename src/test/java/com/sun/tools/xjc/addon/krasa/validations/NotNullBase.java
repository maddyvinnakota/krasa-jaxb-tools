package com.sun.tools.xjc.addon.krasa.validations;

public class NotNullBase extends AnnotationsMojoTestHelper {

    public NotNullBase(ValidationsAnnotation annotation) {
        super("notNull", annotation);
    }

    public void test() throws ClassNotFoundException {
        element("NotNullType")
            .annotationSimpleName("NotNull")
            .attribute("notNullString")
                .annotation("NotNull")
                    .assertNoValues();
    }

}
