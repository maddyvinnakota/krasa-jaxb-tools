package com.sun.tools.xjc.addon.krasa;

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
        list.add(Argument.PLUGIN_OPTION_NAME);
    }

    public ArgumentBuilder add(Argument argument, Object value) {
        list.add(argument.getOptionNameAndValue(value));
        return this;
    }

    public List<String> getOptionList() {
        return list;
    }

}
