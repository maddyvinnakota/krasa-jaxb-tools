package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati
 */
public class OptionDefaultTest extends OptionBase {

    @Override
    public String getAnnotationFileName() {
        return "default-annotation.txt";
    }


    @Override
    public List<String> getArgs() {
        // overwrite whatever options set by RunXJC2MojoTestHelper so to keep default configuration
        return ArgumentBuilder.builder().getOptionList();
    }

}