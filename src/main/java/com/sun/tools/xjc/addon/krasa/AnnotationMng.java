package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JFieldVar;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AnnotationMng {
    private final JFieldVar field;
    private final Set<Class<? extends Annotation>> annotationSet = new HashSet<>();

    public AnnotationMng(JFieldVar field) {
        this.field = field;
    }

    Annotate annotate(Class<? extends Annotation> annotation) {
        return new Annotate((Annotate) null, annotation);
    }

    public class Annotate {
        private final Annotate parent;
        private final JAnnotationUse annotationUse;

        public Annotate(Annotate parent, Class<? extends Annotation> annotation) {
            this.parent = parent;
            if (!annotationSet.contains(annotation)) {
                annotationUse = field.annotate(annotation);
                annotationSet.add(annotation);
            } else {
                this.annotationUse = null;
            }
        }

        private Annotate(JAnnotationUse annotationUse) {
            this.parent = null;
            this.annotationUse = annotationUse;
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

        public Annotate param(String name, BigDecimal value) {
            if (annotationUse != null && value != null) {
                annotationUse.param(name, value.toString());
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

        public Annotate end() {
            return parent;
        }

        public MultipleAnnotation multiple(String paramName) {
            JAnnotationArrayMember array = annotationUse.paramArray(paramName);
            return new MultipleAnnotation(array);
        }

        public class MultipleAnnotation {
            private final JAnnotationArrayMember array;

            public MultipleAnnotation(JAnnotationArrayMember array) {
                this.array = array;
            }

            public Annotate annotate(Class<? extends Annotation> annotation) {
                JAnnotationUse annotationUse = array.annotate(annotation);
                return new Annotate(annotationUse);
            }

        }
    }

}
