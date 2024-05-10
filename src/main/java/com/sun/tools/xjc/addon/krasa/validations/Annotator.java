package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.outline.ClassOutline;
import cz.jirutka.validator.collection.constraints.EachDecimalMax;
import cz.jirutka.validator.collection.constraints.EachDecimalMin;
import cz.jirutka.validator.collection.constraints.EachDigits;
import cz.jirutka.validator.collection.constraints.EachSize;
import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.Column;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class Annotator {

    private static final String FRACTION = "fraction";
    private static final String INTEGER = "integer";
    private static final String MAX = "max";
    private static final String MIN = "min";
    private static final String INCLUSIVE = "inclusive";
    private static final String VALUE = "value";
    private static final String MESSAGE = "message";
    private static final String LENGTH = "length";
    private static final String SCALE = "scale";
    private static final String PRECISION = "precision";
    private static final String REGEXP = "regexp";

    private final JaxbValidationsAnnotation annotationFactory;
    private final AnnotationMng annotations;

    public Annotator(
            ValidationsLogger log,
            JFieldVar field,
            JaxbValidationsAnnotation annotationFactory) {
        this.annotationFactory = annotationFactory;
        this.annotations = new AnnotationMng(log, field);
    }

    void addEachSizeAnnotation(Integer minLength, Integer maxLength) {
        annotations.annotate(EachSize.class)
                .param(MIN, minLength)
                .param(MAX, maxLength)
                .log();
    }

    void addEachDigitsAnnotation(Integer totalDigits, Integer fractionDigits) {
        annotations.annotate(EachDigits.class)
                .param(INTEGER, totalDigits, 0)
                .param(FRACTION, fractionDigits, 0)
                .log();
    }

    void addEachDecimalMaxAnnotation(BigDecimal maxInclusive, BigDecimal maxExclusive) {
        if (maxInclusive != null || maxExclusive != null) {
            annotations.annotate(EachDecimalMax.class)
                    .param(VALUE, maxInclusive)
                    .param(VALUE, maxExclusive)
                    .param(INCLUSIVE, maxInclusive != null)
                    .log();
        }
    }

    void addEachDecimalMinAnnotation(BigDecimal minInclusive, BigDecimal minExclusive) {
        if (minExclusive != null || minInclusive != null) {
            annotations.annotate(EachDecimalMin.class)
                    .param(VALUE, minInclusive)
                    .param(VALUE, minExclusive)
                    .param(INCLUSIVE, minInclusive != null)
                    .log();
        }
    }

    void addNotNullAnnotation(ClassOutline classOutline, JFieldVar field, String message) {
        annotations.annotate(annotationFactory.getNotNullClass())
                .param(MESSAGE, message)
                .log();
    }

    void addValidAnnotation() {
        annotations.annotate(annotationFactory.getValidClass()).log();
    }

    void addSizeAnnotation(Integer minLength, Integer maxLength, Integer length) {
        if (isValidLength(minLength) || isValidLength(maxLength)) {
            annotations.annotate(annotationFactory.getSizeClass())
                    .paramIf(isValidLength(minLength), MIN, minLength)
                    .paramIf(isValidLength(maxLength), MAX, maxLength)
                    .log();

        } else if (isValidLength(length)) {
            annotations.annotate(annotationFactory.getSizeClass())
                    .param(MIN, length)
                    .param(MAX, length)
                    .log();
        }
    }

    void addJpaColumnAnnotation(Integer maxLength) {
        if (maxLength != null) {
            annotations.annotate(Column.class)
                    .param(LENGTH, maxLength)
                    .log();
        }
    }

    void addDecimalMinAnnotation(BigDecimal min, boolean exclusive) {
        if (min != null && isValidValue(min)) {
            annotations.annotate(annotationFactory.getDecimalMinClass())
                    .param(VALUE, min.toString())
                    .param(INCLUSIVE, !exclusive)
                    .log();
        }
    }

    //TODO minExclusive=0, fractionDigits=2 wrong annotation https://github.com/krasa/krasa-jaxb-tools/issues/38
    void addDecimalMaxAnnotation(BigDecimal max, boolean exclusive) {
        if (max != null && isValidValue(max)) {
            annotations.annotate(annotationFactory.getDecimalMaxClass())
                    .param(VALUE, max.toString())
                    .param(INCLUSIVE, (!exclusive))
                    .log();
        }
    }

    void addDigitsAnnotation(Integer totalDigits, Integer fractionDigits) {
        if (totalDigits != null) {
            annotations.annotate(annotationFactory.getDigitsClass())
                    .param(INTEGER, getValueOrZeroOnNull(totalDigits))
                    .param(FRACTION, getValueOrZeroOnNull(fractionDigits))
                    .log();
        }
    }

    void addJpaColumnStringAnnotation(Integer totalDigits, Integer fractionDigits) {
        annotations.annotate(Column.class)
                .param(PRECISION, getValueOrZeroOnNull(totalDigits))
                .param(SCALE, getValueOrZeroOnNull(fractionDigits))
                .log();
    }

    /** Adds all the patterns (A, B, C) as options in a single one (A|B|C). */
    void addAlternativePatternListAnnotation(Set<String> patterns) {
        StringBuilder sb = new StringBuilder();
        for (String p : patterns) {
            sb.append("(").append(p).append(")|");
        }
        String regexp = sb.substring(0, sb.length() - 1);
        addSinglePatternAnnotation(regexp);
    }

    /** Uses @Pattern.List to list all patterns. */
    void addPatternListAnnotation(Set<String> patterns) {
        if (patterns != null && !patterns.isEmpty()) {
            AnnotationMng.Annotate.MultipleAnnotation multi = annotations
                    .annotate(annotationFactory.getPatternListClass())
                    .multiple(VALUE);

            for (String p : patterns) {
                if (p != null && !p.isEmpty()) {
                    multi.annotate(annotationFactory.getPatternClass())
                            .param(REGEXP, p)
                            .log();
                }
            }

        }
    }

    void addSinglePatternAnnotation(String pattern) {
        annotations.annotate(annotationFactory.getPatternClass())
                .param(REGEXP, pattern)
                .log();
    }

    static boolean isValidLength(Integer length) {
        return length != null && length != -1;
    }

    static Integer getValueOrZeroOnNull(Integer value) {
        return value == null ? Integer.valueOf(0) : value;
    }

    /**
     * cxf-codegen puts max and min as value when there is not anything defined in wsdl.
     */
    static boolean isValidValue(BigDecimal value) {
        if (value == null) {
            return false;
        }
        long lvalue = value.longValue();
        return lvalue != Long.MIN_VALUE && lvalue != Long.MAX_VALUE;
    }

}
