package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

public class NotNullBase extends RunXJC2MojoTestHelper {
    private Object notNullAnnotationsCustomMessage = false; // default

    public NotNullBase(Object notNullAnnotationsCustomMessage) {
        super("notNull", "a", true);
        this.notNullAnnotationsCustomMessage = notNullAnnotationsCustomMessage;
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
