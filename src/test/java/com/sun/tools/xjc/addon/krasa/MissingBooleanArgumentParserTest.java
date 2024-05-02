package com.sun.tools.xjc.addon.krasa;

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
                .addWithoutValue(Argument.JSR_349)
                .addWithoutValue(Argument.generateStringListAnnotations)
                .addWithoutValue(Argument.generateNotNullAnnotations)
                .addWithoutValue(Argument.verbose)
                .addWithoutValue(Argument.jpa)
                .addWithoutValue(Argument.generateServiceValidationAnnotations)
                .getOptionList();
    }


}
