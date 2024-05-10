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
                .addWithoutValue(JaxbValidationsArgument.JSR_349)
                .addWithoutValue(JaxbValidationsArgument.generateStringListAnnotations)
                .addWithoutValue(JaxbValidationsArgument.generateNotNullAnnotations)
                .addWithoutValue(JaxbValidationsArgument.verbose)
                .addWithoutValue(JaxbValidationsArgument.jpa)
                .addWithoutValue(JaxbValidationsArgument.generateServiceValidationAnnotations)
                .getOptionList();
    }


}
