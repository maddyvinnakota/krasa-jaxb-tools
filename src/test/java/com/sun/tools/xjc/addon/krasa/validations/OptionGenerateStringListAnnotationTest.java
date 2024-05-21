package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati
 */
public class OptionGenerateStringListAnnotationTest extends OptionBase {

    @Override
    public String getAnnotationFileName() {
        return "generateNotNullAnnotations-annotation.txt";
    }


    @Override
    public List<String> getArgs() {
        // overwrite whatever options set by RunXJC2MojoTestHelper so to keep default configuration
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, false)
                .add(ValidationsArgument.generateStringListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, null)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

}