package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class EmptyBooleanArgumentParserTest extends RunXJC2MojoTestHelper {

    // using an existing parsed XSD
    public EmptyBooleanArgumentParserTest() {
        super("array", "a");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateListAnnotations, "")
                .add(ValidationsArgument.generateNotNullAnnotations, "")
                .add(ValidationsArgument.verbose, "")
                .add(ValidationsArgument.generateServiceValidationAnnotations, "")
                .getOptionList();
    }


}
