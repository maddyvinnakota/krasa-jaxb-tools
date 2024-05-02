package com.sun.tools.xjc.addon.krasa;

/**
 *
 * @author Francesco Illuminati
 */
public class ValidBase extends AnnotationsMojoTestHelper {

    public ValidBase(JaxbValidationsAnnotation annotation) {
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
