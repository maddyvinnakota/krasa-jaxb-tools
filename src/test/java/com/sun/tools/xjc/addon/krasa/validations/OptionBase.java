package com.sun.tools.xjc.addon.krasa.validations;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class OptionBase extends RunXJC2MojoTestHelper {

    public OptionBase() {
        super(ValidationsAnnotation.JAVAX);
    }

    @Override
    public String getFolderName() {
        return "options";
    }

    @Override
    public String getNamespace() {
        return "a";
    }

}
