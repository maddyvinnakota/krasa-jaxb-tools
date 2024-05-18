package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OptionSinglePatternTrueTest extends OptionBase {

    @Override
    public String getAnnotationFileName() {
        return "singlePattern-true-annotation.txt";
    }


    @Override
    public List<String> getArgs() {
        // overwrite whatever options set by RunXJC2MojoTestHelper so to keep default configuration
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.singlePattern, true)
                .getOptionList();
    }

}