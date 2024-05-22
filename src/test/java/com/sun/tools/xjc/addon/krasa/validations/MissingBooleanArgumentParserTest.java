package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class MissingBooleanArgumentParserTest extends RunXJC2MojoTestHelper {

    // using an existing parsed XSD
    public MissingBooleanArgumentParserTest() {
        super("array", "a");
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .addWithoutValue(ValidationsArgument.generateListAnnotations)
                .addWithoutValue(ValidationsArgument.generateNotNullAnnotations)
                .addWithoutValue(ValidationsArgument.verbose)
                .addWithoutValue(ValidationsArgument.generateServiceValidationAnnotations)
                .getOptionList();
    }


}
