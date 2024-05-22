package com.sun.tools.xjc.addon.krasa.validations;

public class MultiplePatternTest extends RunXJC2MojoTestHelper {

    public MultiplePatternTest() {
        super("multiplePatterns", "a");
    }

    public void test() throws ClassNotFoundException {
        element("Multipattern")
                .annotationSimpleName("Pattern")
                .attribute("multiplePatterns")
                    .annotation("Pattern")
                        .assertParam("regexp", "([0-9])|([A-B])");
    }

}
