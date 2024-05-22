package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

public class ClassNameTest extends RunXJC2MojoTestHelper {

    public ClassNameTest() {
        super("notNull", "a", true);
    }

    @Override
    public void checkJavax() {
        element("NotNullType")
                .attribute("notNullString")
                .annotation("NotNull")
                .assertParam("message",
                        "NotNullType.notNullString {javax.validation.constraints.NotNull.message}");
    }

    @Override
    public void checkJakarta() {
        element("NotNullType")
                .attribute("notNullString")
                .annotation("NotNull")
                .assertParam("message",
                        "NotNullType.notNullString {jakarta.validation.constraints.NotNull.message}");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.notNullAnnotationsCustomMessages, "ClassName")
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
