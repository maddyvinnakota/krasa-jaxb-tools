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
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsAnnotator {
    private final JFieldVar field;
    private final Set<Class<? extends Annotation>> annotations = new HashSet<>();

    public JaxbValidationsAnnotator(JFieldVar field) {
        this.field = field;
    }

    void addEachSizeAnnotation(String minLength, String maxLength) {
        annotate(EachSize.class)
                .param("min", minLength)
                .param("max", maxLength);
    }

    void addEachDigitsAnnotation(String totalDigits, String fractionDigits) {
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

    public static String getStringFacet(final XSSimpleType simpleType, String param) {
        final XSFacet facet = simpleType.getFacet(param);
        return facet == null ? null : facet.getValue().value;
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

        public Annotate param(String name, Object value) {
            if (annotationUse != null && value != null) {
                annotationUse.param(name, value.toString());
            }
            return this;
        }

        public Annotate param(String name, Object value, Object defaultValue) {
            if (annotationUse != null) {
                annotationUse.param(name, Objects.toString(value == null ? defaultValue : value));
            }
            return this;
        }

    }
}
