package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.ElementDecl;
import com.sun.xml.xsom.impl.parser.DelayedRef;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsElementProcessor {
    private final JaxbValidationsOptions options;
    private final JaxbValidationsAnnotator annotator;
    private final JaxbValidationsAttributeProcessor attributeProcessor;
    private final JaxbValidationsLogger logger;

    public JaxbValidationsElementProcessor(JaxbValidationsOptions options,
            JaxbValidationsAnnotator annotator, JaxbValidationsAttributeProcessor attributeProcessor,
            JaxbValidationsLogger logger) {
        this.options = options;
        this.annotator = annotator;
        this.attributeProcessor = attributeProcessor;
        this.logger = logger;
    }

    /**
     * XS:Element
     */
    public void processElement(CElementPropertyInfo property,
            ClassOutline classOutline, Outline model) {

        XSComponent schemaComponent = property.getSchemaComponent();
        XSParticle particle = (XSParticle) schemaComponent;
        XSTerm term = particle.getTerm();

        int minOccurs = particle.getMinOccurs().intValue();
        int maxOccurs = particle.getMaxOccurs().intValue();
        boolean nillable = false;
        if (term.isElementDecl()) {
            nillable = ((XSElementDecl) term).isNillable();
        }
        boolean required = property.isRequired();
        String propertyName = propertyName(property);

        JFieldVar field = classOutline.implClass.fields().get(propertyName);

        if (options.isNotNullAnnotations() &&
                !(minOccurs == 0 || !required || nillable) &&
                !annotator.hasAnnotation(field, "NotNull")) {

            annotator.addNotNullAnnotation(classOutline, field);
        }

        if (property.isCollection()) {
            annotator.addValidAnnotation(propertyName, classOutline.implClass.name(), field);

            // http://www.dimuthu.org/blog/2008/08/18/xml-schema-nillabletrue-vs-minoccurs0/comment-page-1/
            if (!annotator.hasAnnotation(field, "Size") &&
                    (maxOccurs != 0 || minOccurs != 0)) {

                if (property.isCollectionRequired()) {
                    annotator.addNotNullAnnotation(classOutline, field);
                }

                annotator.addSizeAnnotation(minOccurs, maxOccurs, null,
                        propertyName, classOutline.implClass.name(), field);
            }
        }

        if (term instanceof ElementDecl) {
            processElement(property, classOutline, field, (ElementDecl) term);

        } else if (term instanceof DelayedRef.Element) {

            XSElementDecl xsElementDecl = ((DelayedRef.Element) term).get();
            processElement(property, classOutline, field, (ElementDecl) xsElementDecl);
        }

    }

    private void processElement(CElementPropertyInfo property,
            ClassOutline classOutline, JFieldVar field, ElementDecl element) {
        String propertyName = propertyName(property);
        String className = classOutline.implClass.name();
        XSType elementType = element.getType();

        annotator.addValidAnnotation(elementType, field, propertyName, className);

        // using https://github.com/jirutka/validator-collection to annotate Lists of primitives
        final XSSimpleType simpleType = elementType.asSimpleType();
        if (options.isGenerateStringListAnnotations() && property.isCollection() && simpleType != null) {
            annotator.addEachSizeAnnotation(simpleType, field);
            annotator.addEachDigitsAnnotation(simpleType, field);
            annotator.addEachDecimalMinAnnotation(simpleType, field);
            annotator.addEachDecimalMaxAnnotation(simpleType, field);
        }

        if (elementType instanceof XSSimpleType) {
            attributeProcessor.processType((XSSimpleType) elementType, field, propertyName, className);

        } else if (elementType.getBaseType() instanceof XSSimpleType) {
            final XSSimpleType baseType = (XSSimpleType) elementType.getBaseType();
            attributeProcessor.processType(baseType, field, propertyName, className);
        }
    }

    private String propertyName(CElementPropertyInfo property) {
        return property.getName(false);
    }

}
