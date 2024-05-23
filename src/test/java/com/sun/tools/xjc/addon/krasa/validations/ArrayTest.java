package com.sun.tools.xjc.addon.krasa.validations;

public class ArrayTest extends RunXJC2MojoTestHelper {

    public ArrayTest() {
        super("array", "a");
    }

    public void test() throws ClassNotFoundException {
        element("Array")
                .assertImportSimpleName("Size")
                .assertImportSimpleName("NotNull")
                .attribute("arrayOfBytes")
                        .annotation("Size").assertParam("max", 18).end()
                        .annotation("NotNull").assertNoValues();
    }

}
