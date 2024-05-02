package com.sun.tools.xjc.addon.krasa;

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
                .add(Argument.notNullAnnotationsCustomMessages, "ClassName")
                .add(Argument.JSR_349, true)
                .add(Argument.generateNotNullAnnotations, true)
                .add(Argument.generateStringListAnnotations, true)
                .add(Argument.targetNamespace, getNamespace())
                .add(Argument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
