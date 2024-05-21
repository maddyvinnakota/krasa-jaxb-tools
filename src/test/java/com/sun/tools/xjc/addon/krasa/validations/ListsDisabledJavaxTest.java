package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

public class ListsDisabledJavaxTest extends AnnotationsMojoTestHelper {

    public ListsDisabledJavaxTest() {
        super("lists", ValidationsAnnotation.JAVAX);
    }

    @Override
    public String getAnnotationFileName() {
        return "disabled-list-annotation.txt";
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateListAnnotations, false)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
