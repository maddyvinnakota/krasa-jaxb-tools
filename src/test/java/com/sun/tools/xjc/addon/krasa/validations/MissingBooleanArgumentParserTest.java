package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MissingBooleanArgumentParserTest extends AnnotationsMojoTestHelper {

    // using an existing parsed XSD
    public MissingBooleanArgumentParserTest() {
        super("array", JaxbValidationsAnnotation.JAVAX);
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .addWithoutValue(ValidationsArgument.JSR_349)
                .addWithoutValue(ValidationsArgument.generateStringListAnnotations)
                .addWithoutValue(ValidationsArgument.generateNotNullAnnotations)
                .addWithoutValue(ValidationsArgument.verbose)
                .addWithoutValue(ValidationsArgument.jpa)
                .addWithoutValue(ValidationsArgument.generateServiceValidationAnnotations)
                .getOptionList();
    }


}
