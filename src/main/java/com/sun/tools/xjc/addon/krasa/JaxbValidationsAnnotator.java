package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JFieldVar;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSimpleType;
import cz.jirutka.validator.collection.constraints.EachDecimalMax;
import cz.jirutka.validator.collection.constraints.EachDecimalMin;
import cz.jirutka.validator.collection.constraints.EachDigits;
import cz.jirutka.validator.collection.constraints.EachSize;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsAnnotator {
    private final JFieldVar field;
    private final JaxbValidationsAnnotation annotationFactory;
    private final Set<Class<? extends Annotation>> annotations = new HashSet<>();

    public JaxbValidationsAnnotator(JFieldVar field, JaxbValidationsAnnotation annotationFactory) {
        this.field = field;
        this.annotationFactory = annotationFactory;
    }

    void addEachSizeAnnotation(Integer minLength, Integer maxLength) {
        annotate(EachSize.class)
                .param("min", minLength)
                .param("max", maxLength);
    }

    void addEachDigitsAnnotation(Integer totalDigits, Integer fractionDigits) {
        annotate(EachDigits.class)
                .param("integer", totalDigits, 0)
                .param("fraction", fractionDigits, 0);
    }

    void addEachDecimalMaxAnnotation(String maxInclusive, String maxExclusive) {
        if (maxInclusive != null || maxExclusive != null) {
            annotate(EachDecimalMax.class)
                    .param("value", maxInclusive)
                    .param("value", maxExclusive)
                    .param("inclusive", maxInclusive != null);
        }
    }

    void addEachDecimalMinAnnotation(String minInclusive, String minExclusive) {
        if (minExclusive != null || minInclusive != null) {
            annotate(EachDecimalMin.class)
                    .param("value", minInclusive)
                    .param("value", minExclusive)
                    .param("inclusive", minInclusive != null);
        }
    }

    void addNotNullAnnotation(String message) {
        annotate(annotationFactory.getNotNullClass())
                .param("message", message);
    }

    void addValidAnnotation() {
        annotate(annotationFactory.getValidClass());
    }

    void addSizeAnnotation(Integer minLength, Integer maxLength, Integer length) {
        if (isValidLength(minLength) || isValidLength(maxLength)) {
            annotate(annotationFactory.getSizeClass())
                    .paramIf(isValidLength(minLength), "min", minLength)
                    .paramIf(isValidLength(maxLength), "max", maxLength);

        } else if (isValidLength(length)) {
            annotate(annotationFactory.getSizeClass())
                    .param("min", length)
                    .param("max", length);
        }
    }

    private static boolean isValidLength(Integer length) {
        return length != null && length != -1;
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
                return null;
            }
        }
        return null;
    }

    private Annotate annotate(Class<? extends Annotation> annotation) {
        return new Annotate(annotation);
    }

    public class Annotate {
        private final JAnnotationUse annotationUse;

        public Annotate(Class<? extends Annotation> annotation) {
            if (!annotations.contains(annotation)) {
                annotationUse = field.annotate(annotation);
                annotations.add(annotation);
            } else {
                this.annotationUse = null;
            }
        }

        public Annotate paramIf(boolean condition, String name, Integer value) {
            if (condition && annotationUse != null && value != null) {
                annotationUse.param(name, value);
            }
            return this;
        }

        public Annotate param(String name, Integer value) {
            if (annotationUse != null && value != null) {
                annotationUse.param(name, value);
            }
            return this;
        }

        public Annotate param(String name, Boolean value) {
            if (annotationUse != null && value != null) {
                annotationUse.param(name, value);
            }
            return this;
        }

        public Annotate param(String name, String value) {
            if (annotationUse != null && value != null) {
                annotationUse.param(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, String value, String defaultValue) {
            if (annotationUse != null) {
                annotationUse.param(name, value == null ? defaultValue : value);
            }
            return this;
        }

        public Annotate param(String name, Integer value, Integer defaultValue) {
            if (annotationUse != null) {
                annotationUse.param(name, value == null ? defaultValue : value);
            }
            return this;
        }

    }
}
