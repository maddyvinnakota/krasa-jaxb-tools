package com.sun.tools.xjc.addon.krasa.validations;

public class NillableTest extends RunXJC2MojoTestHelper {

    public NillableTest() {
        super("nillable", "a");
    }

    public void test() throws ClassNotFoundException {
        withElement("Nillable")
                .assertImportSimpleName("NotNull")
                .withField("notNullable")
                        .withAnnotation("NotNull").assertNoParameters()
                .end()
                .withField("nullable")
                        .assertNoAnnotationsPresent();
    }

}
