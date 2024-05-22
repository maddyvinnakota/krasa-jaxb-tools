package com.sun.tools.xjc.addon.krasa.validations;

public class ChoiceTest extends RunXJC2MojoTestHelper {

    public ChoiceTest() {
        super("choices", "a");
    }

    public void test() {
        element("Choices")
                .attribute("tea")
                        .annotation("XmlElement")
                                .assertParam("name", "Tea")
                        .end()
                        // a member of a <xsd:choice> cannot be @NotNull
                        .assertAnnotationNotPresent("NotNull")
                .end()
                .attribute("coffee")
                        .annotation("XmlElement")
                                .assertParam("name", "Coffee")
                        .end()
                        // a member of a <xsd:choice> cannot be @NotNull
                        .assertAnnotationNotPresent("NotNull")
                .end();
    }

}
