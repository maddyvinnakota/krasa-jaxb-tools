package com.sun.tools.xjc.addon.krasa;

/**
 *
 * @author Francesco Illuminati
 */
public class StringsBase extends AnnotationsMojoTestHelper {

    public StringsBase(ValidationAnnotation annotation) {
        super("strings", annotation);
    }

    public void test() throws ClassNotFoundException {
        element("Strings")
                .annotationSimpleName("Size")
                .annotationSimpleName("NotNull")
                .attribute("address")
                        .annotation("Size")
                                .assertParam("min", "21")
                                .assertParam("max", "43")
                        .end()
                        .annotation("NotNull").assertNoValues();
    }

}
