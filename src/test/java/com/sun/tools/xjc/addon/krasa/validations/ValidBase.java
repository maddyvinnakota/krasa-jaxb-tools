package com.sun.tools.xjc.addon.krasa.validations;

/**
 *
 * @author Francesco Illuminati
 */
public class ValidBase extends AnnotationsMojoTestHelper {

    public ValidBase(ValidationsAnnotation annotation) {
        super("valid", annotation);
    }

    public void test() throws ClassNotFoundException {
        element("Redacted4")
                .attribute("redacted7")
                        .annotation("Size")
                                .assertParam("min", "0")
                                .assertParam("max", "200")
                        .end()
                        .annotation("Valid").assertNoValues();
    }

}
