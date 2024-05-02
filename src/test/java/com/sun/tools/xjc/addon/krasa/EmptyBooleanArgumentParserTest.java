package com.sun.tools.xjc.addon.krasa;

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
                .add(JaxbValidationsArgument.JSR_349, "")
                .add(JaxbValidationsArgument.generateStringListAnnotations, "")
                .add(JaxbValidationsArgument.generateNotNullAnnotations, "")
                .add(JaxbValidationsArgument.verbose, "")
                .add(JaxbValidationsArgument.jpa, "")
                .add(JaxbValidationsArgument.generateServiceValidationAnnotations, "")
                .getOptionList();
    }


}
