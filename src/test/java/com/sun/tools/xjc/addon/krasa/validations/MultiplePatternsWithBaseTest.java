package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;
import java.util.stream.Collectors;

public class MultiplePatternsWithBaseTest extends RunXJC2MojoTestHelper {

    public MultiplePatternsWithBaseTest() {
        super("multiplePatternsWithBase", "a");
    }

    public void test() throws ClassNotFoundException {
        List<String> annotations = withElement("MultiPatternWithBase")
                .assertImportSimpleName("Pattern")
                .getFieldAnnotations("multiplePatternsWithBase");

        assertFalse(annotations.isEmpty());

        String text = annotations.stream()
                .map(s -> s.trim())
                .collect(Collectors.joining("\n"));

        String expected = "@Pattern(regexp = \"([0-9])|([A-B])|([Y-Z])\")";
        assertEquals(expected, text);
    }

}