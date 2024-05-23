package com.sun.tools.xjc.addon.krasa.validations;

/**
 *
 * @author Francesco Illuminati
 */
public class StringsTest extends RunXJC2MojoTestHelper {

    public StringsTest() {
        super("strings", "a");
    }

    public void test() throws ClassNotFoundException {
        withElement("Strings")
                .assertImportSimpleName("Size")
                .assertImportSimpleName("NotNull")
                .withField("address")
                        .withAnnotation("Size")
                                .assertParam("min", "21")
                                .assertParam("max", "43")
                        .end()
                        .withAnnotation("NotNull").assertNoParameters();
    }

}
