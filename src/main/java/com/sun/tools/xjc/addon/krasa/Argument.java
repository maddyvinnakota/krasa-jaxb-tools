package com.sun.tools.xjc.addon.krasa;

import com.sun.tools.xjc.BadCommandLineException;
import static com.sun.tools.xjc.addon.krasa.Utils.setBoolean;
import static com.sun.tools.xjc.addon.krasa.Utils.toBoolean;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
enum Argument {
    targetNamespace(
            String.class,
            "adds @Valid annotation to all elements with given namespace. " +
                    "NOTE that this not related to XSD targetNamespace option but with the 'name' " +
                    "defined as xmlns:name=...",
            (p, v) -> {
                if (v != null && !v.contains(" ")) {
                    p.targetNamespace = v;
                } else {
                    return "invalid namespace"; // error message
                }
                return null; // OK
            },
            (p) ->  p.targetNamespace),
    JSR_349(
            Boolean.class,
            "generates JSR349 compatible annotations for @DecimalMax and @DecimalMin parameters",
            (p,v) -> setBoolean(v, r -> p.jsr349 = r),
            (p) -> p.jsr349),
    generateNotNullAnnotations(
            Boolean.class,
            "adds a @NotNull when an element has minOccours not 0, is required or is not nillable",
            (p,v) -> setBoolean(v, r -> p.notNullAnnotations = r),
            (p) -> p.notNullAnnotations),
    notNullAnnotationsCustomMessages(
            String.class,
            "allowed values: true/false, 'FieldName', 'ClassName' or an actual message",
            (p,v) -> {
                Boolean b = toBoolean(v);

                if (b != null) {
                    p.notNullCustomMessage = b;
                } else {
                    if (CustomMessageType.Classname.equalsIgnoreCase(v)) {
                        p.notNullCustomMessage = true;
                        p.notNullPrefixFieldName = false;
                        p.notNullPrefixClassName = true;
                        p.notNullCustomMessageText = null;
                    } else if (CustomMessageType.Fieldname.equalsIgnoreCase(v)) {
                        p.notNullCustomMessage = true;
                        p.notNullPrefixFieldName = true;
                        p.notNullPrefixClassName = false;
                        p.notNullCustomMessageText = null;
                    } else if (v.equalsIgnoreCase("false")) {
                        p.notNullCustomMessage = false;
                        p.notNullPrefixFieldName = false;
                        p.notNullPrefixClassName = false;
                        p.notNullCustomMessageText = null;
                    } else {
                        p.notNullCustomMessage = false;
                        p.notNullPrefixFieldName = false;
                        p.notNullPrefixClassName = false;
                        p.notNullCustomMessageText = v;
                    }

                }
                return null;
            },
            (p) -> {
                if (p.notNullPrefixFieldName) {
                    return "FieldName";
                } else if (p.notNullPrefixClassName) {
                    return "ClassName";
                } else if (p.notNullCustomMessageText != null) {
                    return p.notNullCustomMessageText;
                } else {
                    return p.notNullCustomMessage;
                }
            }),
    verbose(Boolean.class,
            "increases verbosity",
            (p,v) -> setBoolean(v, r -> p.verbose = r),
            (p) -> p.verbose),
    jpa(
            Boolean.class,
            "adds JPA @Column annotations for fields with multiplicity greater than 0",
            (p,v) -> setBoolean(v, r -> p.jpaAnnotations = r),
            (p) -> p.jpaAnnotations),
    validationAnnotations(
            String.class,
            "selects which type of annotation to use: " + ValidationAnnotation.getValuesAsString(),
            (p,v) -> {
                ValidationAnnotation va;
                try {
                    va = ValidationAnnotation.valueOf(v.toUpperCase());
                } catch (IllegalArgumentException | NullPointerException ex) {
                    return "passed value is not allowed, use one of: " +
                            ValidationAnnotation.getValuesAsString();
                }
                p.annotationFactory = va;
                return null;
            },
            (p) -> p.annotationFactory),
    generateStringListAnnotations(
            Boolean.class,
            "generates github.com/jirutka/validator-collection annotations",
            (p,v) -> setBoolean(v, r -> p.generateStringListAnnotations = r),
            (p) -> p.generateStringListAnnotations),
    generateServiceValidationAnnotations(
            Boolean.class,
            "-- deprecated, use generateStringListAnnotations instead --",
            (p,v) -> setBoolean(v, r -> p.generateStringListAnnotations = r),
            (p) -> p.generateStringListAnnotations);

    public static final String PLUGIN_NAME = "XJsr303Annotations";
    public static final String PLUGIN_OPTION_NAME = "-" + PLUGIN_NAME;
    public static final int PLUGIN_OPTION_NAME_LENGHT = PLUGIN_OPTION_NAME.length() + 1;

    // parameter type
    private Class<?> type;

    // help text
    private String help;

    // set the value and return null if ok or a text with the error
    private BiFunction<JaxbValidationsPlugin, String, String> setter;

    // get the value
    private Function<JaxbValidationsPlugin, Object> getter;

    Argument(
            Class<?> type,
            String help,
            BiFunction<JaxbValidationsPlugin, String, String> setter,
            Function<JaxbValidationsPlugin, Object> getter) {
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
    public static int parse(JaxbValidationsPlugin plugin, String option)
            throws BadCommandLineException {
        if (option.startsWith(PLUGIN_OPTION_NAME)) {
            int idx = option.indexOf("=");
            if (idx != -1) {
                final String name = option.substring(PLUGIN_OPTION_NAME_LENGHT, idx);
                final String value = option.substring(idx + 1);
                Argument argument = parseArgument(name);
                setValueToPlugin(plugin, argument, value);
            } else if (option.length() > PLUGIN_OPTION_NAME_LENGHT) {
                final String name = option.substring(PLUGIN_OPTION_NAME_LENGHT);
                Argument argument = parseArgument(name);
                setValueToPlugin(plugin, argument, "true");
            }
            return 1;
        }
        return 0;
    }

    private static void setValueToPlugin(
            JaxbValidationsPlugin plugin, Argument argument, final String value)
            throws BadCommandLineException {
        try {
            String error = argument.setter.apply(plugin, value);
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

    private static Argument parseArgument(final String name) throws
            BadCommandLineException {
        Argument argument = Argument.valueOf(name);
        if (argument == null) {
            throw new BadCommandLineException(PLUGIN_NAME +
                    " unrecognized option " + name + ", usage:\n" + Argument.helpMessageWithPrefix(""));
        }
        return argument;
    }

    /** @return a multi line string containing an help for each option. */
    public static String helpMessageWithPrefix(String linePrefix) {
        StringBuilder buf = new StringBuilder();
        for (Argument a : values()) {
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
    public static String actualOptionValuesString(JaxbValidationsPlugin plugin, String linePrefix) {
        StringBuilder buf = new StringBuilder();
        buf
                .append(linePrefix)
                .append(PLUGIN_NAME)
                .append(" options:")
                .append(System.lineSeparator());

        for (Argument a : values()) {
            buf
                    .append(linePrefix)
                    .append(a.name())
                    .append(": ")
                    .append(Objects.toString(a.getter.apply(plugin)))
                    .append(System.lineSeparator());
        }

        return buf.toString();
    }

}
