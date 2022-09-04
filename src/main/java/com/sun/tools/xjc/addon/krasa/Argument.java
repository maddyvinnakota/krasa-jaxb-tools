package com.sun.tools.xjc.addon.krasa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum Argument {
    targetNamespace(),
    JSR_349,
    generateNotNullAnnotations,
    notNullAnnotationsCustomMessages,
    verbose,
    jpa,
    generateStringListAnnotations,
    validationAnnotations,
    generateServiceValidationAnnotations;

    public String getOption() {
        return PLUGIN_OPTION_NAME + ":" + name();
    }

    public String getOptionValue(Object value) {
        return PLUGIN_OPTION_NAME + ":" + name() + "=" + value;
    }

    public static String PLUGIN_NAME = "XJsr303Annotations";
    public static String PLUGIN_OPTION_NAME = "-" + PLUGIN_NAME;

    public static int returnOneIfOwnArgument(String arg) {
        return arg.startsWith(PLUGIN_OPTION_NAME) ? 1 : 0;
    }

    public static Parser parse(String[] args) {
        return new Parser(args);
    }

    public static class Parser {
        private final Map<Argument,String> map;

        public Parser(String[] args) {
            this.map = new HashMap<>(args.length);
            final int ponl = PLUGIN_OPTION_NAME.length() + 1;
            for (String a : args) {
                if (a.startsWith(PLUGIN_OPTION_NAME)) {
                    int idx = a.indexOf("=");
                    if (idx != -1) {
                        final String name = a.substring(ponl, idx);
                        final String value = a.substring(idx + 1);
                        map.put(Argument.valueOf(name), value);
                    }
                }
            }
        }

        public Parser stringArgument(Argument argument, Consumer<String> consumer) {
            String value = map.get(argument);
            if (value != null) {
                consumer.accept(value);
            }
            return this;
        }

        public Parser booleanArgument(Argument argument, Consumer<Boolean> consumer) {
            String value = map.get(argument);
            if (value != null) {
                final boolean result = "true".equals(value.toLowerCase());
                consumer.accept(result);
            }
            return this;
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> list = new ArrayList<>();

        public Builder() {
            list.add(Argument.PLUGIN_OPTION_NAME);
        }

        public Builder add(Argument argument, Object value) {
            list.add(Argument.PLUGIN_OPTION_NAME + ":" + argument.name() + "=" + value);
            return this;
        }

        public List<String> getList() {
            return list;
        }
    }
}
