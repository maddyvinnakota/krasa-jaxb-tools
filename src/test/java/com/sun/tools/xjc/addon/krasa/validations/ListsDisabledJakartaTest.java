package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

public class ListsDisabledJakartaTest extends AnnotationsMojoTestHelper {

    public ListsDisabledJakartaTest() {
        super("lists", ValidationsAnnotation.JAKARTA);
    }

    @Override
    public String getAnnotationFileName() {
        return "disabled-list-annotation.txt";
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateStringListAnnotations, false)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
