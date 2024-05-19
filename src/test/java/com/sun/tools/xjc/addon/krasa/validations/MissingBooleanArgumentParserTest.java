package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class MissingBooleanArgumentParserTest extends AnnotationsMojoTestHelper {

    // using an existing parsed XSD
    public MissingBooleanArgumentParserTest() {
        super("array", ValidationsAnnotation.JAVAX);
    }


    @Override
    public String getAnnotationFileName() {
        return "MissingBooleanArgumentParserTest-annotation.txt";
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .addWithoutValue(ValidationsArgument.generateStringListAnnotations)
                .addWithoutValue(ValidationsArgument.generateNotNullAnnotations)
                .addWithoutValue(ValidationsArgument.verbose)
                .addWithoutValue(ValidationsArgument.generateServiceValidationAnnotations)
                .getOptionList();
    }


}
