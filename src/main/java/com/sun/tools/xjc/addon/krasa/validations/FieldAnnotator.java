package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.outline.ClassOutline;
import cz.jirutka.validator.collection.constraints.EachDecimalMax;
import cz.jirutka.validator.collection.constraints.EachDecimalMin;
import cz.jirutka.validator.collection.constraints.EachDigits;
import cz.jirutka.validator.collection.constraints.EachPattern;
import cz.jirutka.validator.collection.constraints.EachSize;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati
 */
class FieldAnnotator {

    private static final String FRACTION = "fraction";
    private static final String INTEGER = "integer";
    private static final String MAX = "max";
    private static final String MIN = "min";
    private static final String INCLUSIVE = "inclusive";
    private static final String VALUE = "value";
    private static final String MESSAGE = "message";
    private static final String REGEXP = "regexp";

    private final ValidationsAnnotation annotationFactory;
    private final XjcAnnotator xjcAnnotator;

    public FieldAnnotator(
            JFieldVar field, ValidationsAnnotation annotationFactory, ValidationsLogger log) {
        this.annotationFactory = annotationFactory;
        this.xjcAnnotator = new XjcAnnotator(field, log);
    }

    void addEachSizeAnnotation(Integer minLength, Integer maxLength) {
        if ((minLength != null && minLength != 0) ||
                (maxLength != null && maxLength != 0)) {
            xjcAnnotator.annotate(EachSize.class)
                    .param(MIN, minLength)
                    .param(MAX, maxLength)
                    .log();
        }
    }

    void addEachDigitsAnnotation(Integer totalDigits, Integer fractionDigits) {
        if ((totalDigits != null && totalDigits != 0) ||
                (fractionDigits != null && fractionDigits != 0)) {
            xjcAnnotator.annotate(EachDigits.class)
                    .param(INTEGER, totalDigits, 0)
                    .param(FRACTION, fractionDigits, 0)
                    .log();
        }
    }

    void addEachDecimalMaxAnnotation(BigDecimal maxInclusive, BigDecimal maxExclusive) {
        if (maxInclusive != null || maxExclusive != null) {
            xjcAnnotator.annotate(EachDecimalMax.class)
                    .param(VALUE, maxInclusive)
                    .param(VALUE, maxExclusive)
                    .param(INCLUSIVE, maxInclusive != null)
                    .log();
        }
    }

    void addEachDecimalMinAnnotation(BigDecimal minInclusive, BigDecimal minExclusive) {
        if (minExclusive != null || minInclusive != null) {
            xjcAnnotator.annotate(EachDecimalMin.class)
                    .param(VALUE, minInclusive)
                    .param(VALUE, minExclusive)
                    .param(INCLUSIVE, minInclusive != null)
                    .log();
        }
    }

    void addNotNullAnnotation(ClassOutline classOutline, JFieldVar field, String message) {
        xjcAnnotator.annotate(annotationFactory.getNotNullClass())
                .param(MESSAGE, message)
                .log();
    }

    void addValidAnnotation() {
        xjcAnnotator.annotate(annotationFactory.getValidClass()).log();
    }

    void addSizeAnnotation(Integer minLength, Integer maxLength, Integer length) {
        if (isValidLength(minLength) || isValidLength(maxLength)) {
            xjcAnnotator.annotate(annotationFactory.getSizeClass())
                    .paramIf(isValidLength(minLength), MIN, minLength)
                    .paramIf(isValidLength(maxLength), MAX, maxLength)
                    .log();

        } else if (isValidLength(length)) {
            xjcAnnotator.annotate(annotationFactory.getSizeClass())
                    .param(MIN, length)
                    .param(MAX, length)
                    .log();
        }
    }

    void addDecimalMinAnnotationExclusive(BigDecimal min) {
        addDecimalMinAnnotation(min, true);
    }

    void addDecimalMinAnnotationInclusive(BigDecimal min) {
        addDecimalMinAnnotation(min, false);
    }

    private void addDecimalMinAnnotation(BigDecimal min, boolean exclusive) {
        if (min != null) {
            xjcAnnotator.annotate(annotationFactory.getDecimalMinClass())
                    .param(VALUE, min.toString())
                    .param(INCLUSIVE, !exclusive)
                    .log();
        }
    }

    void addDecimalMaxAnnotationExclusive(BigDecimal max) {
        addDecimalMaxAnnotation(max, true);
    }

    void addDecimalMaxAnnotationInclusive(BigDecimal max) {
        addDecimalMaxAnnotation(max, false);
    }

    private void addDecimalMaxAnnotation(BigDecimal max, boolean exclusive) {
        if (max != null) {
            xjcAnnotator.annotate(annotationFactory.getDecimalMaxClass())
                    .param(VALUE, max.toString())
                    .param(INCLUSIVE, (!exclusive))
                    .log();
        }
    }

    void addDigitsAnnotation(Integer totalDigits, Integer fractionDigits) {
        if (totalDigits != null) {
            xjcAnnotator.annotate(annotationFactory.getDigitsClass())
                    .param(INTEGER, getValueOrZeroOnNull(totalDigits))
                    .param(FRACTION, getValueOrZeroOnNull(fractionDigits))
                    .log();
        }
    }

    void addPatterns(Set<String> patternSet) {
        addPatternAnnotations(annotationFactory.getPatternClass(), patternSet);
    }
    void addPatternList(Set<String> patternSetList) {
        switch (patternSetList.size()) {
            case 0:
                // do nothing at all
                break;
            case 1:
                addPatterns(patternSetList);
                break;
            default:
                //first add PatternList
                XjcAnnotator.Annotate patternListAnnotation = xjcAnnotator.annotate(annotationFactory.getPatternListClass());
                JAnnotationArrayMember listValue = patternListAnnotation.paramArray("value");
                patternSetList.forEach(p ->
                    listValue.annotate(annotationFactory.getPatternClass()).param("regexp", p));
                patternListAnnotation.log();
        }
    }

    void addEachPatterns(Set<String> patternSet) {
        addPatternAnnotations(EachPattern.class, patternSet);
    }

    private void addPatternAnnotations(Class<? extends Annotation> annotation, Set<String> patternSet) {
        switch (patternSet.size()) {
            case 0:
                // do nothing at all
                break;
            case 1:
                addSinglePatternAnnotation(annotation, patternSet.iterator().next());
                break;
            default:
                addPatternAnnotation(annotation, patternSet);
        }
    }

    /** Add all the patterns (A, B, C) as options in a single one (A|B|C). */
    private void addPatternAnnotation(Class<? extends Annotation> annotation, Set<String> patterns) {
        String regexp = joinPatternsInOR(patterns);
        addSinglePatternAnnotation(annotation, regexp);
    }

    public static String joinPatternsInOR(Set<String> patterns) {
        if(patterns.size() == 1){
            return patterns.iterator().next();
        }
        StringBuilder sb = new StringBuilder();
        for (String p : patterns) {
            sb.append("(").append(p).append(")|");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private void addSinglePatternAnnotation(Class<? extends Annotation> annotation, String pattern) {
        xjcAnnotator.annotate(annotation)
                .param(REGEXP, pattern)
                .log();
    }

    static boolean isValidLength(Integer length) {
        return length != null && length != -1;
    }

    static Integer getValueOrZeroOnNull(Integer value) {
        return value == null ? Integer.valueOf(0) : value;
    }

}
