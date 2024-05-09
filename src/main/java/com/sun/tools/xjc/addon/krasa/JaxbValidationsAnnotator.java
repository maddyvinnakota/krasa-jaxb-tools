package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSimpleType;
import cz.jirutka.validator.collection.constraints.EachDecimalMax;
import cz.jirutka.validator.collection.constraints.EachDecimalMin;
import cz.jirutka.validator.collection.constraints.EachDigits;
import cz.jirutka.validator.collection.constraints.EachSize;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsAnnotator {

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

    // use those in logger
    private final String className;
    private final String propertyName;
    private final JFieldVar field;

    private final JaxbValidationsAnnotation annotationFactory;
    private final AnnotationMng annotations;

    public JaxbValidationsAnnotator(String className, String propertyName, JFieldVar field,
            JaxbValidationsAnnotation annotationFactory) {
        this.className = className;
        this.propertyName = propertyName;
        this.field = field;
        this.annotationFactory = annotationFactory;
        this.annotations = new AnnotationMng(field);
    }

    boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
        return annotations.isAnnotatedWith(annotation);
    }

    void addEachSizeAnnotation(Integer minLength, Integer maxLength) {
        annotations.annotate(EachSize.class)
                .param(MIN, minLength)
                .param(MAX, maxLength);
    }

    void addEachDigitsAnnotation(Integer totalDigits, Integer fractionDigits) {
        annotations.annotate(EachDigits.class)
                .param(INTEGER, totalDigits, 0)
                .param(FRACTION, fractionDigits, 0);
    }

    void addEachDecimalMaxAnnotation(String maxInclusive, String maxExclusive) {
        if (maxInclusive != null || maxExclusive != null) {
            annotations.annotate(EachDecimalMax.class)
                    .param(VALUE, maxInclusive)
                    .param(VALUE, maxExclusive)
                    .param(INCLUSIVE, maxInclusive != null);
        }
    }

    void addEachDecimalMinAnnotation(String minInclusive, String minExclusive) {
        if (minExclusive != null || minInclusive != null) {
            annotations.annotate(EachDecimalMin.class)
                    .param(VALUE, minInclusive)
                    .param(VALUE, minExclusive)
                    .param(INCLUSIVE, minInclusive != null);
        }
    }

    void addNotNullAnnotation(ClassOutline classOutline, JFieldVar field, String message) {
        annotations.annotate(annotationFactory.getNotNullClass())
                .param(MESSAGE, message);
    }

    void addValidAnnotation() {
        annotations.annotate(annotationFactory.getValidClass());
    }

    void addSizeAnnotation(Integer minLength, Integer maxLength, Integer length) {
        if (isValidLength(minLength) || isValidLength(maxLength)) {
            annotations.annotate(annotationFactory.getSizeClass())
                    .paramIf(isValidLength(minLength), MIN, minLength)
                    .paramIf(isValidLength(maxLength), MAX, maxLength);

        } else if (isValidLength(length)) {
            annotations.annotate(annotationFactory.getSizeClass())
                    .param(MIN, length)
                    .param(MAX, length);
        }
    }

    void addJpaColumnAnnotation(Integer maxLength) {
        if (maxLength != null) {
            annotations.annotate(Column.class)
                    .param(LENGTH, maxLength);
        }
    }

    void addDecimalMinAnnotation(BigDecimal min, boolean exclusive) {
        if (min != null) {
            annotations.annotate(annotationFactory.getDecimalMinClass())
                    .param(VALUE, min.toString())
                    .param(INCLUSIVE, !exclusive);
        }
    }

    //TODO minExclusive=0, fractionDigits=2 wrong annotation https://github.com/krasa/krasa-jaxb-tools/issues/38
    void addDecimalMaxAnnotation(BigDecimal max, boolean exclusive) {
        if (max != null) {
            annotations.annotate(annotationFactory.getDecimalMaxClass())
                    .param(VALUE, max.toString())
                    .param(INCLUSIVE, (!exclusive));
        }
    }

    void addDigitsAnnotation(Integer totalDigits, Integer fractionDigits) {
        annotations.annotate(annotationFactory.getDigitsClass())
                .param(INTEGER, totalDigits)
                .param(FRACTION, fractionDigits);
    }

    void addJpaColumnStringAnnotation(Integer totalDigits, Integer fractionDigits) {
        annotations.annotate(Column.class)
                .param(PRECISION, totalDigits)
                .param(SCALE, fractionDigits);
    }

    /** The standard way is very buggy, so prefer this one. */
    void addAlternativePatternListAnnotation(List<String> patterns) {
        StringBuilder sb = new StringBuilder();
        for (String p : patterns) {
            sb.append("(").append(p).append(")|");
        }
        String regexp = sb.substring(0, sb.length() - 1);
        annotateSinglePattern(regexp);
    }

    @Deprecated // very buggy implementation
    void addPatternListAnnotation(List<String> patterns) {
        if (patterns != null && !patterns.isEmpty()) {
            AnnotationMng.Annotate.MultipleAnnotation multi = annotations
                    .annotate(annotationFactory.getPatternListClass())
                    .multiple(VALUE);

            for (String p : patterns) {
                if (p != null && !p.isEmpty()) {
                    multi.annotate(annotationFactory.getPatternClass())
                            .param("regexp", p);
                }
            }
        }
    }

    void annotateSinglePattern(String pattern) {
        annotations.annotate(annotationFactory.getPatternClass())
                .param("regexp", pattern);
    }

    /*
	 * \Q indicates begin of quoted regex text, \E indicates end of quoted regex text
     */
    static String escapeRegexp(String pattern) {
        return java.util.regex.Pattern.quote(pattern);
    }

    static boolean isSizeAnnotationApplicable(JFieldVar field) {
        if (field == null) {
            return false;
        }
        return field.type().name().equals("String") || field.type().isArray();
    }


    static boolean isValidLength(Integer length) {
        return length != null && length != -1;
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

    public static List<String> getMultipleStringFacets(final XSSimpleType simpleType, String param) {
        final List<XSFacet> facets = simpleType.getFacets(param);
        if (facets != null) {
            return facets.stream()
                    .map(facet -> facet.getValue().value)
                    .filter(v -> v != null && !v.isEmpty())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static String getStringFacet(final XSSimpleType simpleType, String param) {
        final XSFacet facet = simpleType.getFacet(param);
        return facet == null ? null : facet.getValue().value;
    }

    public static Integer getIntegerFacet(final XSSimpleType simpleType, String param) {
        final XSFacet facet = simpleType.getFacet(param);
        if (facet != null) {
            try {
                return Integer.parseInt(facet.getValue().value);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return null;
    }

    public static BigDecimal getDecimalFacet(final XSSimpleType simpleType, String param) {
        final XSFacet facet = simpleType.getFacet(param);
        if (facet != null) {
            final String str = facet.getValue().value;
            try {
                return new BigDecimal(str);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return null;
    }
}
