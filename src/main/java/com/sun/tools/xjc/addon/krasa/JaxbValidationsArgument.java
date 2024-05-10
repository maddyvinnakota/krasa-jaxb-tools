package com.sun.tools.xjc.addon.krasa;

import com.sun.tools.xjc.BadCommandLineException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
enum JaxbValidationsArgument {
    targetNamespace(
            // type:
            String.class,
            // help message:
            "adds @Valid annotation to all elements with given namespace. " +
                    "NOTE that this not related to XSD targetNamespace option but with the 'name' " +
                    "defined as xmlns:name=...",
            // setter:
            (p, v) -> {
                if (v != null && !v.contains(" ")) {
                    p.targetNamespace(v);
                } else {
                    return "invalid namespace"; // error message
                }
                return null; // OK
            },
            // getter:
            (p) ->  p.getTargetNamespace()),
    singlePattern(
            Boolean.class,
            "uses a single javax validation @Pattern instead of @Pattern.List",
            (p, v) -> setBoolean(v, r -> p.singlePattern(r)),
            p -> p.isSinglePattern()),
    JSR_349(
            Boolean.class,
            "generates JSR349 compatible annotations for @DecimalMax and @DecimalMin parameters",
            (p,v) -> setBoolean(v, r -> p.jsr349(r)),
            (p) -> p.isJsr349()),
    generateNotNullAnnotations(
            Boolean.class,
            "adds a @NotNull when an element has minOccours not 0, is required or is not nillable",
            (p,v) -> setBoolean(v, r -> p.notNullAnnotations(r)),
            (p) -> p.isNotNullAnnotations()),
    notNullAnnotationsCustomMessages(
            String.class,
            "allowed values: true/false, 'FieldName', 'ClassName' or an actual message",
            (p,v) -> {
                Boolean b = toBoolean(v);

                if (b != null) {
                    p.notNullCustomMessage(b);
                } else {
                    if (CustomMessageType.Classname.equalsIgnoreCase(v)) {
                        p.notNullCustomMessage(true);
                        p.notNullPrefixFieldName(false);
                        p.notNullPrefixClassName(true);
                        p.notNullCustomMessageText(null);
                    } else if (CustomMessageType.Fieldname.equalsIgnoreCase(v)) {
                        p.notNullCustomMessage(true);
                        p.notNullPrefixFieldName(true);
                        p.notNullPrefixClassName(false);
                        p.notNullCustomMessageText(null);
                    } else if (v.equalsIgnoreCase("false")) {
                        p.notNullCustomMessage(false);
                        p.notNullPrefixFieldName(false);
                        p.notNullPrefixClassName(false);
                        p.notNullCustomMessageText(null);
                    } else {
                        p.notNullCustomMessage(false);
                        p.notNullPrefixFieldName(false);
                        p.notNullPrefixClassName(false);
                        p.notNullCustomMessageText(v);
                    }

                }
                return null;
            },
            (p) -> {
                if (p.isNotNullPrefixFieldName()) {
                    return "FieldName";
                } else if (p.isNotNullPrefixClassName()) {
                    return "ClassName";
                } else if (p.getNotNullCustomMessageText() != null) {
                    return p.getNotNullCustomMessageText();
                } else {
                    return p.isNotNullCustomMessage();
                }
            }),
    verbose(Boolean.class,
            "increases verbosity",
            (p,v) -> setBoolean(v, r -> p.verbose(r)),
            (p) -> p.isVerbose()),
    jpa(
            Boolean.class,
            "adds JPA @Column annotations for fields with multiplicity greater than 0",
            (p,v) -> setBoolean(v, r -> p.jpaAnnotations(r)),
            (p) -> p.isJpaAnnotations()),
    validationAnnotations(
            String.class,
            "selects which type of annotation to use: " + JaxbValidationsAnnotation.getValuesAsString(),
            (p,v) -> {
                JaxbValidationsAnnotation va;
                try {
                    va = JaxbValidationsAnnotation.valueOf(v.toUpperCase());
                } catch (IllegalArgumentException | NullPointerException ex) {
                    return "passed value is not allowed, use one of: " +
                            JaxbValidationsAnnotation.getValuesAsString();
                }
                p.annotationFactory(va);
                return null;
            },
            (p) -> p.getAnnotationFactory()),
    generateStringListAnnotations(
            Boolean.class,
            "generates github.com/jirutka/validator-collection annotations",
            (p,v) -> setBoolean(v, r -> p.generateStringListAnnotations(r)),
            (p) -> p.isGenerateStringListAnnotations()),
    generateServiceValidationAnnotations(
            Boolean.class,
            "-- deprecated, use generateStringListAnnotations instead --",
            (p,v) -> setBoolean(v, r -> p.generateStringListAnnotations(r)),
            (p) -> p.isGenerateStringListAnnotations());

    public static final String PLUGIN_NAME = "XJsr303Annotations";
    public static final String PLUGIN_OPTION_NAME = "-" + PLUGIN_NAME;
    public static final int PLUGIN_OPTION_NAME_LENGHT = PLUGIN_OPTION_NAME.length() + 1;

    // parameter type
    private final Class<?> type;

    // help text
    private final String help;

    // set the value and return null if ok or a text with the error
    private final BiFunction<JaxbValidationsOptions.Builder, String, String> setter;

    // get the value
    private final Function<JaxbValidationsOptions, Object> getter;

    JaxbValidationsArgument(
            Class<?> type,
            String help,
            BiFunction<JaxbValidationsOptions.Builder, String, String> setter,
            Function<JaxbValidationsOptions, Object> getter) {
        this.type = type;
        this.help = help;
        this.setter = setter;
        this.getter = getter;
    }

    String withValue(String value) {
        return PLUGIN_OPTION_NAME + ":" + name() + "=" + value;
    }

    String fullOptionName() {
        return PLUGIN_OPTION_NAME + ":" + name();
    }

    String fullName() {
        return PLUGIN_NAME + ":" + name();
    }

    /** @return 1 if the argument is referring to this plugin, 0 otherwise. */
    public static int parse(JaxbValidationsOptions.Builder options, String option)
            throws BadCommandLineException {
        if (option.startsWith(PLUGIN_OPTION_NAME)) {
            int idx = option.indexOf("=");
            if (idx != -1) {
                final String name = option.substring(PLUGIN_OPTION_NAME_LENGHT, idx);
                final String value = option.substring(idx + 1);
                JaxbValidationsArgument argument = parseJaxbValidationsArgument(name);
                setValueToPlugin(options, argument, value);
            } else if (option.length() > PLUGIN_OPTION_NAME_LENGHT) {
                final String name = option.substring(PLUGIN_OPTION_NAME_LENGHT);
                JaxbValidationsArgument argument = parseJaxbValidationsArgument(name);
                setValueToPlugin(options, argument, "true");
            }
            return 1;
        }
        return 0;
    }

    static void setValueToPlugin(
            JaxbValidationsOptions.Builder options, JaxbValidationsArgument argument, final String value)
            throws BadCommandLineException {
        try {
            String error = argument.setter.apply(options, value);
            if (error != null) {
                throw new BadCommandLineException(
                        "option " + argument.name() + ": " +
                        (error.length() > 0 ? error + ", " : "") +
                        "cannot accept '" + value + "' as a " + argument.type.getSimpleName());
            }
        } catch (NullPointerException ex) {
            throw new BadCommandLineException(argument.errorMessage(value));
        }
    }

    static JaxbValidationsArgument parseJaxbValidationsArgument(final String name) throws
            BadCommandLineException {
        JaxbValidationsArgument argument = JaxbValidationsArgument.valueOf(name);
        if (argument == null) {
            throw new BadCommandLineException(PLUGIN_NAME +
                    " unrecognized option " + name + ", usage:\n" + JaxbValidationsArgument.helpMessageWithPrefix(""));
        }
        return argument;
    }

    /** @return a multi line string containing an help for each option. */
    public static String helpMessageWithPrefix(String linePrefix) {
        StringBuilder buf = new StringBuilder();
        for (JaxbValidationsArgument a : values()) {
            buf
                    .append(linePrefix)
                    .append(a.name())
                    .append(": ")
                    .append("(")
                    .append(a.type.getSimpleName())
                    .append(") ")
                    .append(a.help)
                    .append(System.lineSeparator());
        }
        return buf.toString();
    }

    public String errorMessage(String wrongValue) {
        return fullName() + " option expected a value of type " + type.getSimpleName() +
                " but got '" + Objects.toString(wrongValue) + "'";
    }

    /** @return a multi line string containing the value for each option. */
    public static String getActualOptionValuesAsString(
            JaxbValidationsOptions options,
            String linePrefix) {
        StringBuilder buf = new StringBuilder();
        buf
                .append(linePrefix)
                .append(PLUGIN_NAME)
                .append(" options:")
                .append(System.lineSeparator());

        for (JaxbValidationsArgument a : values()) {
            buf
                    .append(linePrefix)
                    .append(a.name())
                    .append(": ")
                    .append(Objects.toString(a.getter.apply(options)))
                    .append(System.lineSeparator());
        }

        return buf.toString();
    }

    static String setBoolean(String value, Consumer<Boolean> setter) {
        if (value == null || "".equals(value.trim())) {
            setter.accept(true);
            return null;
        }
        Boolean bool = toBoolean(value);
        if (bool == null) {
            return "argument not valid, must be 'true' or 'false'";
        }
        setter.accept(bool);
        return null;
    }

    /**
     * @return the boolean value of v (no case sensitive) or null otherwise.
     */
    static boolean toBoolean(String v, Boolean defaultIfNull) {
        if (v != null) {
            return toBoolean(v);
        }
        return defaultIfNull != null ? defaultIfNull : false;
    }

    /**
     * @return the boolean value of v (no case sensitive) or null otherwise.
     */
    static Boolean toBoolean(String v) {
        if (v != null) {
            String lc = v.toLowerCase().trim();
            if ("true".equals(lc)) {
                return Boolean.TRUE;
            } else if ("false".equals(lc)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

}
