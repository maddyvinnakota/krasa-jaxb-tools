package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

public class NotNullBase extends AnnotationsMojoTestHelper {
    private Object notNullAnnotationsCustomMessage = false; // default

    public NotNullBase(ValidationsAnnotation annotation, Object notNullAnnotationsCustomMessage) {
        super("notNull", annotation);
        this.notNullAnnotationsCustomMessage = notNullAnnotationsCustomMessage;
    }

    @Override
    public String getAnnotationFileName() {
        return getClass().getSimpleName()
                .replace("Test", "")
                + "-annotation.txt";
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.notNullAnnotationsCustomMessages, notNullAnnotationsCustomMessage)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

}
