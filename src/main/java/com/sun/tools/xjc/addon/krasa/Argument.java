package com.sun.tools.xjc.addon.krasa;

import static com.sun.tools.xjc.addon.krasa.Utils.toBoolean;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
enum Argument {
    targetNamespace(
            String.class,
            "adds @Valid annotation to all elements with given namespace",
            (p, v) -> p.targetNamespace = v,
            (p) ->  Objects.toString(p.targetNamespace)),
    JSR_349(
            Boolean.class,
            "generates JSR349 compatible annotations for @DecimalMax and @DecimalMin inclusive parameter",
            (p,v) -> p.jsr349 = toBoolean(v, p.jsr349),
            (p) -> Objects.toString(p.jsr349)),
    generateNotNullAnnotations(
            Boolean.class,
            "adds a @NotNull annotation if an element has minOccours not 0, is required or is not nillable",
            (p,v) ->
                p.notNullAnnotations = toBoolean(v, p.notNullAnnotations),
            (p) -> Objects.toString(p.notNullAnnotations)),
    notNullAnnotationsCustomMessages(
            String.class,
            "values are 'true', 'FieldName', 'ClassName', or an actual message",
            (p,v) -> {
                Boolean b = toBoolean(v);

                if (b != null) {
                    p.notNullCustomMessage = b;
                } else {
                    if (CustomMessageType.Classname.equalsIgnoreCase(v)) {
                        p.notNullCustomMessage = true;
                        p.notNullPrefixFieldName = false;
                        p.notNullPrefixClassName = true;
                    } else if (CustomMessageType.Fieldname.equalsIgnoreCase(v)) {
                        p.notNullCustomMessage = true;
                        p.notNullPrefixFieldName = true;
                        p.notNullPrefixClassName = false;
                    } else if (v.length() != 0 &&
                            !v.equalsIgnoreCase("false")) {
                        p.notNullCustomMessageText = v;
                    }
                }
            },
            (p) -> ""),
    verbose(Boolean.class,
            "increases verbosity",
            (p,v) -> p.verbose = toBoolean(v, p.verbose),
            (p) -> Objects.toString(p.verbose)),
    jpa(
            Boolean.class,
            "adds JPA @Column annotations for fields with multiplicity greater than 0",
            (p,v) -> p.jpaAnnotations = toBoolean(v, p.jpaAnnotations),
            (p) -> Objects.toString(p.jpaAnnotations)),
    generateStringListAnnotations(
            Boolean.class,
            "generates github.com/jirutka/validator-collection annotations",
            (p,v) ->
                p.generateStringListAnnotations = toBoolean(v, p.generateStringListAnnotations),
            (p) -> Objects.toString(p.generateStringListAnnotations)),
    validationAnnotations(
            String.class,
            "selects which type of annotation to use: JAVAX (default) or JAKARTA",
            (p,v) ->
                p.annotationFactory = ValidationAnnotation.valueOf(v.toUpperCase()),
            (p) -> Objects.toString(p.annotationFactory)),
    generateServiceValidationAnnotations(
            Boolean.class,
            "",
            (p,v) ->
                p.generateStringListAnnotations = toBoolean(v, p.generateStringListAnnotations),
            (p) -> Objects.toString(p.generateStringListAnnotations));

    public static final String PLUGIN_NAME = "XJsr303Annotations";
    public static final String PLUGIN_OPTION_NAME = "-" + PLUGIN_NAME;
    public static final int PLUGIN_OPTION_NAME_LENGHT = PLUGIN_OPTION_NAME.length() + 1;

    private Class<?> type;
    private String help;
    private BiConsumer<JaxbValidationsPlugin, String> setter;
    private Function<JaxbValidationsPlugin, String> getter;

    Argument(
            Class<?> type,
            String help,
            BiConsumer<JaxbValidationsPlugin, String> setter,
            Function<JaxbValidationsPlugin, String> getter) {
        this.type = type;
        this.help = help;
        this.setter = setter;
        this.getter = getter;
    }

    public String getOptionName() {
        return PLUGIN_OPTION_NAME + ":" + name();
    }

    public String getOptionNameAndValue(Object value) {
        return PLUGIN_OPTION_NAME + ":" + name() + "=" + value;
    }

    /** @return 1 if the argument is referring to this plugin, 0 otherwise. */
    public static int parse(JaxbValidationsPlugin plugin, String option) {
        if (option.startsWith(PLUGIN_OPTION_NAME)) {
            int idx = option.indexOf("=");
            if (idx != -1) {
                final String name = option.substring(PLUGIN_OPTION_NAME_LENGHT, idx);
                final String value = option.substring(idx + 1);
                Argument argument = Argument.valueOf(name);
                argument.setter.accept(plugin, value);
            } else if (option.length() > PLUGIN_OPTION_NAME_LENGHT) {
                final String name = option.substring(PLUGIN_OPTION_NAME_LENGHT);
                Argument argument = Argument.valueOf(name);
                argument.setter.accept(plugin, "true");
            }
            return 1;
        }
        return 0;
    }

    /** @return a multi line string containing an help for each option. */
    public static String help(String linePrefix) {
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

    /** @return a multi line string containing the value for each option. */
    public static String options(JaxbValidationsPlugin plugin, String linePrefix) {
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
                    .append(a.getter.apply(plugin))
                    .append(System.lineSeparator());
        }

        return buf.toString();
    }

}
