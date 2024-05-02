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
                .add(Argument.JSR_349, "")
                .add(Argument.generateStringListAnnotations, "")
                .add(Argument.generateNotNullAnnotations, "")
                .add(Argument.verbose, "")
                .add(Argument.jpa, "")
                .add(Argument.generateServiceValidationAnnotations, "")
                .getOptionList();
    }


}
