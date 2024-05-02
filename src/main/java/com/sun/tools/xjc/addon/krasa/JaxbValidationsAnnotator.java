package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import cz.jirutka.validator.collection.constraints.EachDecimalMax;
import cz.jirutka.validator.collection.constraints.EachDecimalMin;
import cz.jirutka.validator.collection.constraints.EachDigits;
import cz.jirutka.validator.collection.constraints.EachSize;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsAnnotator {
    private final JaxbValidationsOptions options;
    private final JaxbValidationsLogger logger;

    public JaxbValidationsAnnotator(JaxbValidationsOptions options, JaxbValidationsLogger logger) {
        this.options = options;
        this.logger = logger;
    }

    void addEachSizeAnnotation(final XSSimpleType simpleType, final JFieldVar field)
            throws NumberFormatException {
        String minLength = getStringFacet(simpleType, "minLength");
        String maxLength = getStringFacet(simpleType, "maxLength");
        if (minLength != null || maxLength != null) {
            JAnnotationUse annotation = field.annotate(EachSize.class);
            if (minLength != null) {
                annotation.param("min", Integer.parseInt(minLength));
            }
            if (maxLength != null) {
                annotation.param("max", Integer.parseInt(maxLength));
            }
        }
    }

    void addEachDigitsAnnotation(final XSSimpleType simpleType, final JFieldVar field)
            throws NumberFormatException {
        String totalDigits = getStringFacet(simpleType, "totalDigits");
        String fractionDigits = getStringFacet(simpleType, "fractionDigits");
        if (totalDigits != null || fractionDigits != null) {
            JAnnotationUse annotation = field.annotate(EachDigits.class);
            if (totalDigits != null || fractionDigits != null) {
                if (totalDigits != null) {
                    annotation.param("integer", Integer.parseInt(totalDigits));
                } else {
                    annotation.param("integer", 0);
                }
                if (fractionDigits != null) {
                    annotation.param("fraction", Integer.parseInt(fractionDigits));
                } else {
                    annotation.param("fraction", 0);
                }
            }
        }
    }

    void addEachDecimalMaxAnnotation(final XSSimpleType simpleType, final JFieldVar field)
            throws NumberFormatException {
        String maxInclusive = getStringFacet(simpleType, "maxInclusive");
        String maxExclusive = getStringFacet(simpleType, "maxExclusive");
        if (maxExclusive != null || maxInclusive != null) {
            JAnnotationUse annotation = field.annotate(EachDecimalMax.class);
            if (maxInclusive != null) {
                annotation.param("value", maxInclusive)
                        .param("inclusive", true);
            }
            if (maxExclusive != null) {
                annotation.param("value", maxExclusive)
                        .param("inclusive", false);
            }
        }
    }

    void addEachDecimalMinAnnotation(final XSSimpleType simpleType, JFieldVar field)
            throws NumberFormatException {
        String minInclusive = getStringFacet(simpleType, "minInclusive");
        String minExclusive = getStringFacet(simpleType, "minExclusive");
        if (minExclusive != null || minInclusive != null) {
            JAnnotationUse annotation = field.annotate(EachDecimalMin.class);
            if (minInclusive != null) {
                annotation.param("value", minInclusive)
                        .param("inclusive", true);
            }
            if (minExclusive != null) {
                annotation.param("value", minExclusive)
                        .param("inclusive", false);
            }
        }
    }

    void addNotNullAnnotation(ClassOutline co, JFieldVar field) {
        final String className = co.implClass.name();
        final Class<? extends Annotation> notNullClass = options.getAnnotationFactory().getNotNullClass();

        String message = null;
        if (options.isNotNullPrefixClassName()) {
            message = String.format("%s.%s {%s.message}",
                    className, field.name(),
                    notNullClass.getName());

        } else if (options.isNotNullPrefixFieldName()) {
            message = String.format("%s {%s.message}",
                    field.name(),
                    notNullClass.getName());

        } else if (options.isNotNullCustomMessage()) {
            message = String.format("{%s.message}",
                    notNullClass.getName());

        } else if (options.getNotNullCustomMessageText() != null) {
            message = options.getNotNullCustomMessageText()
                    .replace("{ClassName}", className)
                    .replace("{FieldName}", field.name());

        }

        log("@NotNull: " + field.name() + " added to class " + className);

        final JAnnotationUse annotation = field.annotate(notNullClass);
        if (message != null) {
            annotation.param("message", message);
        }

    }

    void addValidAnnotation(String propertyName, String className, JFieldVar field) {
        if (!field.annotations().contains(options.getAnnotationFactory().getValidClass())) {
            logger.log("@Valid: " + propertyName + " added to class " + className);
            field.annotate(options.getAnnotationFactory().getValidClass());
        }
    }

    void addValidAnnotation(XSType elementType, JFieldVar field, String propertyName,
            String className) {

        if (!field.annotations().contains(options.getAnnotationFactory().getValidClass())) {
            String elemNs = elementType.getTargetNamespace();

            if ((options.getTargetNamespace() == null || elemNs.startsWith(options.getTargetNamespace())) &&
                    (elementType.isComplexType() || Utils.isCustomType(field)) &&
                    !hasAnnotation(field, "Valid")) {

                log("@Valid: " + propertyName + " added to class " + className);
                field.annotate(options.getAnnotationFactory().getValidClass());
            }
        }
    }

    void addSizeAnnotation(XSSimpleType simpleType, String propertyName, String className,
            JFieldVar field) {

        Integer maxLength = getIntegerFacet(simpleType, "maxLength");
        Integer minLength = getIntegerFacet(simpleType, "minLength");
        Integer length = getIntegerFacet(simpleType, "length");

        addSizeAnnotation(minLength, maxLength, length, propertyName, className, field);
    }

    void addSizeAnnotation(Integer minLength, Integer maxLength, Integer length,
            String propertyName, String className, JFieldVar field) {

        if (isValidLength(minLength) || isValidLength(maxLength)) {
            log("@Size(" + minLength + "," + maxLength + "): " +
                    propertyName + " added to class " + className);

            final JAnnotationUse annotate = field.annotate(options.getAnnotationFactory().getSizeClass());
            if (isValidLength(minLength)) {
                annotate.param("min", minLength);
            }
            if (isValidLength(maxLength)) {
                annotate.param("max", maxLength);
            }

        } else if (isValidLength(length)) {
            log("@Size(" + length + "," + length + "): " + propertyName +
                    " added to class " + className);

            field.annotate(options.getAnnotationFactory().getSizeClass())
                    .param("min", length)
                    .param("max", length);
        }
    }

    private static boolean isValidLength(Integer length) {
        return length != null && length != -1;
    }

    void addJpaColumnAnnotation(XSSimpleType simpleType, String propertyName,
            String className, JFieldVar field) {
        Integer maxLength = getIntegerFacet(simpleType, "maxLength");
        if (maxLength != null) {
            log("@Column(null, " + maxLength + "): " + propertyName +
                    " added to class " + className);
            field.annotate(Column.class).param("length", maxLength);
        }
    }

    void addDigitAndJpaColumnAnnotation(XSSimpleType simpleType, JFieldVar field,
            String propertyName, String className) {

        Integer totalDigits = getIntegerFacet(simpleType, "totalDigits");
        Integer fractionDigits = getIntegerFacet(simpleType, "fractionDigits");
        if (totalDigits == null) {
            totalDigits = 0;
        }
        if (fractionDigits == null) {
            fractionDigits = 0;
        }

        if (!hasAnnotation(field, "Digits")) {
            log("@Digits(" + totalDigits + "," + fractionDigits + "): " + propertyName +
                    " added to class " + className);
            field.annotate(options.getAnnotationFactory().getDigitsClass())
                    .param("integer", totalDigits)
                    .param("fraction", fractionDigits);
        }
        if (options.isJpaAnnotations()) {
            field.annotate(Column.class)
                    .param("precision", totalDigits)
                    .param("scale", fractionDigits);
        }
    }

    void addDecimalMinAnnotation(JFieldVar field, XSFacet minFacet,
            String propertyName, String className, boolean exclusive) {

        BigDecimal min = parseIntegerXsFacet(minFacet);
        if (min == null) {
            return;
        }

        JAnnotationUse annotate = field.annotate(options.getAnnotationFactory().getDecimalMinClass());

        if (options.isJsr349()) {
            log("@DecimalMin(value = " + min + ", inclusive = " + (!exclusive) + "): " +
                    propertyName + " added to class " + className);

            annotate.param("value", min.toString())
                    .param("inclusive", !exclusive);

        } else {
            if (exclusive) {
                min = min.add(BigDecimal.ONE);
            }

            log("@DecimalMin(" + min + "): " + propertyName + " added to class " + className);

            annotate.param("value", min.toString());
        }
    }

    //TODO minExclusive=0, fractionDigits=2 wrong annotation https://github.com/krasa/krasa-jaxb-tools/issues/38
    void addDecimalMaxAnnotation(JFieldVar field, XSFacet maxFacet,
            String propertyName, String className, boolean exclusive) {

        BigDecimal max = parseIntegerXsFacet(maxFacet);
        if (max == null) {
            return;
        }

        JAnnotationUse annotate = field.annotate(options.getAnnotationFactory().getDecimalMaxClass());

        if (options.isJsr349()) {
            log("@DecimalMax(value = " + max + ", inclusive = " + (!exclusive) + "): " +
                    propertyName + " added to class " + className);

            annotate.param("value", max.toString())
                    .param("inclusive", (!exclusive));

        } else {
            if (exclusive) {
                max = max.subtract(BigDecimal.ONE);
            }

            log("@DecimalMax(" + max + "): " + propertyName + " added to class " + className);

            annotate.param("value", max.toString());
        }
    }

    void addPatternEmptyAnnotation(XSSimpleType simpleType, String propertyName,
            String className, JFieldVar field) {

        final List<XSFacet> enumerationList = simpleType.getFacets("enumeration");
        final XSFacet patternFacet = simpleType.getFacet("enumeration");

        if (enumerationList.size() > 1) { // More than one pattern

            log("@Pattern: " + propertyName + " added to class " + className);
            final JAnnotationUse annotation = field.annotate(options.getAnnotationFactory().getPatternClass());
            annotateMultiplePattern(enumerationList, annotation, true);

        } else if (patternFacet != null) {
            final String pattern = patternFacet.getValue().value;
            annotateSinglePattern(pattern, propertyName, className, field, true);

        }
    }

    void addSinlgePatternAnnotation(XSSimpleType simpleType, String propertyName,
            String className, JFieldVar field, String pattern) {

        if (simpleType.getBaseType() instanceof XSSimpleType &&
                ((XSSimpleType) simpleType.getBaseType()).getFacet("pattern") != null) {

            final XSSimpleType baseType = (XSSimpleType) simpleType.getBaseType();

            log("@Pattern.List: " + propertyName + " added to class " + className);

            JAnnotationUse patternListAnnotation = field.annotate(options.getAnnotationFactory().getPatternListClass());
            JAnnotationArrayMember listValue = patternListAnnotation.paramArray("value");
            final XSFacet facet = baseType.getFacet("pattern");
            String basePattern = facet.getValue().value;

            listValue.annotate(options.getAnnotationFactory().getPatternClass())
                    .param("regexp", replaceRegexp(basePattern));

            annotateSinglePattern(basePattern, propertyName, className, listValue,
                    false);

        } else {

            annotateSinglePattern(pattern, propertyName, className, field, false);
        }
    }

    void addPatternListAnnotation(XSSimpleType simpleType, String propertyName,
            String className, JFieldVar field, List<XSFacet> patternList) {

        if (simpleType.getBaseType() instanceof XSSimpleType &&
                ((XSSimpleType) simpleType.getBaseType()).getFacet("pattern") != null) {

            log("@Pattern.List: " + propertyName + " added to class " + className);

            JAnnotationUse patternListAnnotation = field.annotate(options.getAnnotationFactory().getPatternListClass());
            JAnnotationArrayMember listValue = patternListAnnotation.paramArray("value");

            String basePattern = ((XSSimpleType) simpleType.getBaseType()).getFacet("pattern").getValue().value;
            listValue.annotate(options.getAnnotationFactory().getPatternClass()).param("regexp", replaceRegexp(basePattern));

            log("@Pattern: " + propertyName + " added to class " + className);
            final JAnnotationUse patternAnnotation = listValue.annotate(options.getAnnotationFactory().getPatternClass());
            annotateMultiplePattern(patternList, patternAnnotation, false);

        } else {

            log("@Pattern: " + propertyName + " added to class " + className);
            final JAnnotationUse patternAnnotation = field.annotate(options.getAnnotationFactory().getPatternClass());
            annotateMultiplePattern(patternList, patternAnnotation, false);
        }
    }

    void annotateSinglePattern(String pattern, String propertyName, String className,
            JAnnotatable annotable, boolean literal) {
        // cxf-codegen fix
        if (!"\\c+".equals(pattern)) {
            log("@Pattern(" + pattern + "): " + propertyName + " added to class " + className);
            String replaceRegexp = replaceRegexp(pattern);
            if (literal) {
                replaceRegexp = escapeRegexp(replaceRegexp);
            }
            annotable.annotate(options.getAnnotationFactory().getPatternClass())
                    .param("regexp", replaceRegexp);
        }
    }

    void annotateMultiplePattern(
            final List<XSFacet> patternList,
            final JAnnotationUse patternAnnotation,
            final boolean literal) {

        StringBuilder sb = new StringBuilder();
        for (XSFacet xsFacet : patternList) {
            final String value = xsFacet.getValue().value;
            // cxf-codegen fix
            if (!"\\c+".equals(value)) {
                String regexp = replaceRegexp(value);
                if (literal) {
                    regexp = escapeRegexp(regexp);
                }
                sb.append("(").append(regexp).append(")|");
            }
        }
        patternAnnotation.param("regexp", sb.substring(0, sb.length() - 1));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    boolean hasAnnotation(JFieldVar var, String annotationClassSimpleName) {
        List<JAnnotationUse> list =
                (List<JAnnotationUse>) Utils.getFieldValue("annotations", var);
        if (list != null) {
            for (JAnnotationUse annotationUse : list) {
                if (((Class) Utils.getFieldValue("clazz._class", annotationUse)).
                        getSimpleName().equals(annotationClassSimpleName)) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean isSizeAnnotationApplicable(JFieldVar field) {
        if (field == null) {
            return false;
        }
        return field.type().name().equals("String") || field.type().isArray();
    }

    boolean isValidValue(XSFacet facet) {
        if (facet == null) {
            return false;
        }
        String value = facet.getValue().value;
        // cxf-codegen puts max and min as value when there is not anything defined in wsdl.
        return value != null && !Utils.isMax(value) && !Utils.isMin(value);
    }

    private static String replaceRegexp(String pattern) {
        return pattern
                .replace("\\i", "[_:A-Za-z]")
                .replace("\\c", "[-._:A-Za-z0-9]");
    }

    /*
	 * \Q indicates begin of quoted regex text, \E indicates end of quoted regex text
     */
    private static String escapeRegexp(String pattern) {
        return java.util.regex.Pattern.quote(pattern);
    }

    private Integer getIntegerFacet(XSSimpleType simpleType, String name) {
        final XSFacet facet = simpleType.getFacet(name);
        if (facet == null) {
            return null;
        }
        final String value = facet.getValue().value;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseIntegerXsFacet(XSFacet facet) {
        final String str = facet.getValue().value;
        if (str == null || str.trim().isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String getStringFacet(final XSSimpleType simpleType, String param) {
        final XSFacet facet = simpleType.getFacet(param);
        return facet == null ? null : facet.getValue().value;
    }

    // TODO use jul
    private void log(String log) {
        if (options.isVerbose()) {
            System.out.println(log);
        }
    }

}
