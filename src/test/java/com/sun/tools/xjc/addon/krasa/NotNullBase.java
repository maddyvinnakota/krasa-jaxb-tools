package com.sun.tools.xjc.addon.krasa;

public class NotNullBase extends AnnotationsMojoTestHelper {

    public NotNullBase(ValidationAnnotation annotation) {
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
