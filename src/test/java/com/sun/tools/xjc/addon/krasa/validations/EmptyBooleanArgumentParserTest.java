package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EmptyBooleanArgumentParserTest extends AnnotationsMojoTestHelper {

    // using an existing parsed XSD
    public EmptyBooleanArgumentParserTest() {
        super("array", JaxbValidationsAnnotation.JAVAX);
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.JSR_349, "")
                .add(ValidationsArgument.generateStringListAnnotations, "")
                .add(ValidationsArgument.generateNotNullAnnotations, "")
                .add(ValidationsArgument.verbose, "")
                .add(ValidationsArgument.jpa, "")
                .add(ValidationsArgument.generateServiceValidationAnnotations, "")
                .getOptionList();
    }


}
