package com.sun.tools.xjc.addon.krasa.validations;

public class ArrayTest extends RunXJC2MojoTestHelper {

    public ArrayTest() {
        super("array", "a");
    }

    public void test() throws ClassNotFoundException {
        withElement("Array")
                .assertImportSimpleName("Size")
                .assertImportSimpleName("NotNull")
                .withField("arrayOfBytes")
                        .withAnnotation("Size").assertParam("max", 18).end()
                        .withAnnotation("NotNull").assertNoParameters();
    }

}
