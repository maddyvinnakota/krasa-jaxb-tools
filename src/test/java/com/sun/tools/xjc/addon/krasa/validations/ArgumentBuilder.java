package com.sun.tools.xjc.addon.krasa.validations;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class ArgumentBuilder {

    public static ArgumentBuilder builder() {
        return new ArgumentBuilder();
    }

    private final List<String> list = new ArrayList<>();

    public ArgumentBuilder() {
        list.add(JaxbValidationsArgument.PLUGIN_OPTION_NAME);
    }

    public ArgumentBuilder add(JaxbValidationsArgument argument, Object value) {
        list.add(argument.withValue(java.util.Objects.toString(value)));
        return this;
    }

    public ArgumentBuilder addWithoutValue(JaxbValidationsArgument argument) {
        list.add(argument.fullOptionName());
        return this;
    }

    public List<String> getOptionList() {
        return list;
    }

}
