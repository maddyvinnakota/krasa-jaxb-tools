package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati
 */
public class OptionSinglePatternFalseTest extends OptionBase {

    @Override
    public String getAnnotationFileName() {
        return "singlePattern-false-annotation.txt";
    }


    @Override
    public List<String> getArgs() {
        // overwrite whatever options set by RunXJC2MojoTestHelper so to keep default configuration
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.singlePattern, false)
                .getOptionList();
    }

}