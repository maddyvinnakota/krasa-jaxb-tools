package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TargetBase extends AnnotationsMojoTestHelper {
    private final String targetNamespace;

    public TargetBase(ValidationsAnnotation annotation, String targetNamespace) {
        super("target", annotation);
        this.targetNamespace = targetNamespace;
    }

    @Override
    public String getNamespace() {
        return "a,b";
    }

    public String getAnnotationFileName() {
        return getClass().getSimpleName()
                .replace("Test", "")
                .replace("Javax", "")
                .replace("Jakarta", "")
                + "-annotation.txt";
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, false)
                .add(ValidationsArgument.generateStringListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, targetNamespace)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
