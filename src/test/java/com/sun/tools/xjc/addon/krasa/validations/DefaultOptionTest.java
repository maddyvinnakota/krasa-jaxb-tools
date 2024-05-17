package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DefaultOptionTest extends RunXJC2MojoTestHelper {

    public DefaultOptionTest() {
        super(ValidationsAnnotation.JAVAX);
    }

    @Override
    public String getFolderName() {
        return "abase";
    }

    public String getAnnotationFileName() {
        return "ABaseDefaultTest-annotation.txt";
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder().getOptionList();
    }

}