package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EachDisabledBase extends AnnotationsMojoTestHelper {

    public EachDisabledBase(ValidationsAnnotation annotation) {
        super("each", annotation);
    }

    @Override
    public String getAnnotationFileName() {
        return "disabled-annotation.txt";
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateStringListAnnotations, false)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

}
