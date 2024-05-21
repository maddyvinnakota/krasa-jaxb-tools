package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

public class ClassNameBase extends AnnotationsMojoTestHelper {

    public ClassNameBase(ValidationsAnnotation validation) {
        super("notNull", validation);
    }

    @Override
    public String getNamespace() {
        return "a";
    }

    public void test() {
        element("NotNullType")
                .attribute("notNullString")
                .annotation("NotNull")
                .assertParam("message",
                        "NotNullType.notNullString {" + getAnnotationLibraryName() + ".validation.constraints.NotNull.message}");
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
