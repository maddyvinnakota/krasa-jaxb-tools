package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.impl.AttributeUseImpl;
import com.sun.xml.xsom.impl.SimpleTypeImpl;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsAttributeProcessor {

    private final JaxbValidationsOptions options;
    private final JaxbValidationsAnnotator annotator;
    private final JaxbValidationsLogger logger;

    public JaxbValidationsAttributeProcessor(JaxbValidationsOptions options,
            JaxbValidationsAnnotator annotator, JaxbValidationsLogger logger) {
        this.options = options;
        this.annotator = annotator;
        this.logger = logger;
    }

    /**
     * Attribute from parent declaration
     */
    void processAttribute(CValuePropertyInfo property,
            ClassOutline clase, Outline model) {
        FieldOutline field = model.getField(property);
        String propertyName = property.getName(false);
        String className = clase.implClass.name();

        logger.log("Attribute " + propertyName + " added to class " + className);
        XSComponent definition = property.getSchemaComponent();
        SimpleTypeImpl particle = (SimpleTypeImpl) definition;
        XSSimpleType type = particle.asSimpleType();
        JFieldVar var = clase.implClass.fields().get(propertyName);

        annotator.addValidAnnotation(type, var, propertyName, className);
        processType(type, var, propertyName, className);
    }

    void processAttribute(CAttributePropertyInfo property,
            ClassOutline clase, Outline model) {
        FieldOutline field = model.getField(property);
        String propertyName = property.getName(false);
        String className = clase.implClass.name();

        logger.log("Attribute " + propertyName + " added to class " + className);
        XSComponent definition = property.getSchemaComponent();
        AttributeUseImpl particle = (AttributeUseImpl) definition;
        XSSimpleType type = particle.getDecl().getType();

        JFieldVar var = clase.implClass.fields().get(propertyName);
        if (particle.isRequired()) {
            if (!annotator.hasAnnotation(var, "NotNull")) {
                annotator.addNotNullAnnotation(clase, var);
            }
        }

        annotator.addValidAnnotation(type, var, propertyName, className);
        processType(type, var, propertyName, className);
    }

    void processType(XSSimpleType simpleType, JFieldVar field,
            String propertyName, String className) {

        if (field == null) {
            return;
        }

        if (!annotator.hasAnnotation(field, "Size") &&
                annotator.isSizeAnnotationApplicable(field)) {
            annotator.addSizeAnnotation(simpleType, propertyName, className, field);
        }

        if (options.isJpaAnnotations() && annotator.isSizeAnnotationApplicable(field)) {
            annotator.addJpaColumnAnnotation(simpleType, propertyName, className, field);
        }

        if (Utils.isNumberField(field)) {

            if (!annotator.hasAnnotation(field, "DecimalMin")) {

                XSFacet minInclusive = simpleType.getFacet("minInclusive");
                if (annotator.isValidValue(minInclusive)) {
                    annotator.addDecimalMinAnnotation(field, minInclusive, propertyName, className,
                            false);
                }

                XSFacet minExclusive = simpleType.getFacet("minExclusive");
                if (annotator.isValidValue(minExclusive)) {
                    annotator.addDecimalMinAnnotation(field, minExclusive, propertyName, className,
                            true);
                }
            }

            if (!annotator.hasAnnotation(field, "DecimalMax")) {

                XSFacet maxInclusive = simpleType.getFacet("maxInclusive");
                if (annotator.isValidValue(maxInclusive)) {
                    annotator.addDecimalMaxAnnotation(field, maxInclusive, propertyName, className,
                            false);
                }

                XSFacet maxExclusive = simpleType.getFacet("maxExclusive");
                if (annotator.isValidValue(maxExclusive)) {
                    annotator.addDecimalMaxAnnotation(field, maxExclusive, propertyName, className,
                            true);
                }
            }

            if (simpleType.getFacet("totalDigits") != null) {
                annotator.addDigitAndJpaColumnAnnotation(simpleType, field, propertyName, className);
            }
        }

        final String fieldName = field.type().name();
        if ("String".equals(fieldName) &&
                !annotator.hasAnnotation(field, "Pattern")) {

            final XSFacet patternFacet = simpleType.getFacet("pattern");
            final List<XSFacet> patternList = simpleType.getFacets("pattern");

            if (patternList.size() > 1) { // More than one pattern
                annotator.addPatternListAnnotation(simpleType, propertyName, className, field,
                        patternList);

            } else if (patternFacet != null) {

                String pattern = patternFacet.getValue().value;
                annotator.addSinlgePatternAnnotation(simpleType, propertyName, className, field,
                        pattern);

            } else {

                annotator.addPatternEmptyAnnotation(simpleType, propertyName, className, field);
            }
        }
    }

}
