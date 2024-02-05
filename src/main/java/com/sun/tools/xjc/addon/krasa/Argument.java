package com.sun.tools.xjc.addon.krasa;

import java.util.function.BiConsumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
enum Argument {
    targetNamespace((p, v) -> p.targetNamespace = v),
    JSR_349((p,v) -> p.jsr349 = toBoolean(v)),
    generateNotNullAnnotations((p,v) -> p.notNullAnnotations = toBoolean(v)),
    notNullAnnotationsCustomMessages((p,value) -> {
        p.notNullCustomMessages = toBoolean(value);

        if (!p.notNullCustomMessages) {
            if (value.equalsIgnoreCase("classname")) {
                p.notNullCustomMessages = true;
                p.notNullPrefixFieldName = true;
                p.notNullPrefixClassName = true;
            } else if (value.equalsIgnoreCase("fieldname")) {
                p.notNullCustomMessages = true;
                p.notNullPrefixFieldName = true;
            } else if (value.length() != 0 &&
                    !value.equalsIgnoreCase("false")) {
                p.notNullCustomMessage = value;
            }
        }
    }),
    verbose((p,v) -> p.verbose = toBoolean(v)),
    jpa((p,v) -> p.jpaAnnotations = toBoolean(v)),
    generateStringListAnnotations((p,v) -> p.generateStringListAnnotations = toBoolean(v)),
    validationAnnotations((p,v) -> p.annotationFactory = ValidationAnnotation.valueOf(v.toUpperCase())),
    generateServiceValidationAnnotations((p,v) -> p.generateStringListAnnotations = toBoolean(v));

    public static final String PLUGIN_NAME = "XJsr303Annotations";
    public static final String PLUGIN_OPTION_NAME = "-" + PLUGIN_NAME;
    public static final int PONL = PLUGIN_OPTION_NAME.length() + 1;

    private static boolean toBoolean(String v) {
        return v != null && "true".equals(v.toLowerCase());
    }

    private BiConsumer<JaxbValidationsPlugins, String> setter;

    Argument(BiConsumer<JaxbValidationsPlugins, String> setter) {
        this.setter = setter;
    }

    public String getOption() {
        return PLUGIN_OPTION_NAME + ":" + name();
    }

    public String getOptionValue(Object value) {
        return PLUGIN_OPTION_NAME + ":" + name() + "=" + value;
    }

    /** @return 1 if the argument is referring to this plugin, 0 otherwise. */
    public static int parse(JaxbValidationsPlugins plugin, String option) {
        if (option.startsWith(PLUGIN_OPTION_NAME)) {
            int idx = option.indexOf("=");
            if (idx != -1) {
                final String name = option.substring(PONL, idx);
                final String value = option.substring(idx + 1);
                Argument argument = Argument.valueOf(name);
                argument.setter.accept(plugin, value);
            } else if (option.length() > PONL) {
                final String name = option.substring(PONL);
                Argument argument = Argument.valueOf(name);
                argument.setter.accept(plugin, "true");
            }
            return 1;
        }
        return 0;
    }

}
