package com.sun.tools.xjc.addon.krasa;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsOptions {
    // sets default values in Builder not here
    private final String targetNamespace;
    private final boolean singlePattern;
    private final boolean jsr349;
    private final boolean verbose;
    private final boolean notNullAnnotations;
    private final boolean notNullCustomMessage;
    private final boolean notNullPrefixFieldName;
    private final boolean notNullPrefixClassName;
    private final String notNullCustomMessageText;
    private final boolean jpaAnnotations;
    private final boolean generateStringListAnnotations;
    private final JaxbValidationsAnnotation annotationFactory;


    public String getTargetNamespace() {
        return targetNamespace;
    }

    public boolean isSinglePattern() {
        return singlePattern;
    }

    public boolean isJsr349() {
        return jsr349;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isNotNullAnnotations() {
        return notNullAnnotations;
    }

    public boolean isNotNullCustomMessage() {
        return notNullCustomMessage;
    }

    public boolean isNotNullPrefixFieldName() {
        return notNullPrefixFieldName;
    }

    public boolean isNotNullPrefixClassName() {
        return notNullPrefixClassName;
    }

    public String getNotNullCustomMessageText() {
        return notNullCustomMessageText;
    }

    public boolean isJpaAnnotations() {
        return jpaAnnotations;
    }

    public boolean isGenerateStringListAnnotations() {
        return generateStringListAnnotations;
    }

    public JaxbValidationsAnnotation getAnnotationFactory() {
        return annotationFactory;
    }

    public static class Builder {
        private String targetNamespace = null;
        private boolean singlePattern = true;
        private boolean jsr349 = false;
        private boolean verbose = false;
        private boolean notNullAnnotations = true;
        private boolean notNullCustomMessage = false;
        private boolean notNullPrefixFieldName = false;
        private boolean notNullPrefixClassName = false;
        private String notNullCustomMessageText = null;
        private boolean jpaAnnotations = false;
        private boolean generateStringListAnnotations;
        private JaxbValidationsAnnotation annotationFactory = JaxbValidationsAnnotation.JAVAX;

        private Builder() {
        }

        public Builder targetNamespace(final String value) {
            this.targetNamespace = value;
            return this;
        }

        public Builder singlePattern(final boolean value) {
            this.singlePattern = value;
            return this;
        }

        public Builder jsr349(final boolean value) {
            this.jsr349 = value;
            return this;
        }

        public Builder verbose(final boolean value) {
            this.verbose = value;
            return this;
        }

        public Builder notNullAnnotations(final boolean value) {
            this.notNullAnnotations = value;
            return this;
        }

        public Builder notNullCustomMessage(final boolean value) {
            this.notNullCustomMessage = value;
            return this;
        }

        public Builder notNullPrefixFieldName(final boolean value) {
            this.notNullPrefixFieldName = value;
            return this;
        }

        public Builder notNullPrefixClassName(final boolean value) {
            this.notNullPrefixClassName = value;
            return this;
        }

        public Builder notNullCustomMessageText(final String value) {
            this.notNullCustomMessageText = value;
            return this;
        }

        public Builder jpaAnnotations(final boolean value) {
            this.jpaAnnotations = value;
            return this;
        }

        public Builder generateStringListAnnotations(final boolean value) {
            this.generateStringListAnnotations = value;
            return this;
        }

        public Builder annotationFactory(final JaxbValidationsAnnotation value) {
            this.annotationFactory = value;
            return this;
        }

        public JaxbValidationsOptions build() {
            return new com.sun.tools.xjc.addon.krasa.JaxbValidationsOptions(targetNamespace, singlePattern, jsr349,
                    verbose, notNullAnnotations, notNullCustomMessage, notNullPrefixFieldName,
                    notNullPrefixClassName, notNullCustomMessageText, jpaAnnotations,
                    generateStringListAnnotations, annotationFactory);
        }
    }

    public static JaxbValidationsOptions.Builder builder() {
        return new JaxbValidationsOptions.Builder();
    }

    private JaxbValidationsOptions(final String targetNamespace,
            final boolean singlePattern, final boolean jsr349,
            final boolean verbose, final boolean notNullAnnotations,
            final boolean notNullCustomMessage, final boolean notNullPrefixFieldName,
            final boolean notNullPrefixClassName, final String notNullCustomMessageText,
            final boolean jpaAnnotations, final boolean generateStringListAnnotations,
            final JaxbValidationsAnnotation annotationFactory) {
        this.targetNamespace = targetNamespace;
        this.singlePattern = singlePattern;
        this.jsr349 = jsr349;
        this.verbose = verbose;
        this.notNullAnnotations = notNullAnnotations;
        this.notNullCustomMessage = notNullCustomMessage;
        this.notNullPrefixFieldName = notNullPrefixFieldName;
        this.notNullPrefixClassName = notNullPrefixClassName;
        this.notNullCustomMessageText = notNullCustomMessageText;
        this.jpaAnnotations = jpaAnnotations;
        this.generateStringListAnnotations = generateStringListAnnotations;
        this.annotationFactory = annotationFactory;
    }

}
