package com.sun.tools.xjc.addon.krasa.validations;

public class NillableTest extends RunXJC2MojoTestHelper {

    public NillableTest() {
        super("nillable", "a");
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
