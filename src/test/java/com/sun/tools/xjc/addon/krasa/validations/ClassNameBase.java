package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

public class ClassNameBase extends AnnotationsMojoTestHelper {

    public ClassNameBase(JaxbValidationsAnnotation validation) {
        super("notNull", validation);
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
                .add(JaxbValidationsArgument.notNullAnnotationsCustomMessages, "ClassName")
                .add(JaxbValidationsArgument.JSR_349, true)
                .add(JaxbValidationsArgument.generateNotNullAnnotations, true)
                .add(JaxbValidationsArgument.generateStringListAnnotations, true)
                .add(JaxbValidationsArgument.targetNamespace, getNamespace())
                .add(JaxbValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
