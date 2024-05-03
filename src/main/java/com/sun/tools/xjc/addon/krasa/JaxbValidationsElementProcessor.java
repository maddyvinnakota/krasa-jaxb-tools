package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JFieldVar;
import static com.sun.tools.xjc.addon.krasa.JaxbValidationsAnnotator.getStringFacet;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.ElementDecl;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsElementProcessor {
    private final JaxbValidationsOptions options;
    private final JaxbValidationsOldAnnotator oldAnnotator;
    private final JaxbValidationsAttributeProcessor attributeProcessor;
    private final JaxbValidationsLogger logger;

    public JaxbValidationsElementProcessor(JaxbValidationsOptions options,
            JaxbValidationsOldAnnotator oldAnnotator, JaxbValidationsAttributeProcessor attributeProcessor,
            JaxbValidationsLogger logger) {
        this.options = options;
        this.oldAnnotator = oldAnnotator;
        this.attributeProcessor = attributeProcessor;
        this.logger = logger;
    }

    /**
     * XS:Element
     */
    public void processElement(CElementPropertyInfo property,
            ClassOutline classOutline, Outline model) {

        XSParticle particle = (XSParticle) property.getSchemaComponent();
        ElementDecl element = (ElementDecl) particle.getTerm();

        int minOccurs = particle.getMinOccurs().intValue();
        int maxOccurs = particle.getMaxOccurs().intValue();
        boolean nillable = element.isNillable();
        boolean required = property.isRequired();
        String propertyName = property.getName(false);

        JFieldVar field = classOutline.implClass.fields().get(propertyName);

        if (options.isNotNullAnnotations() &&
                !(minOccurs == 0 || !required || nillable) &&
                !oldAnnotator.hasAnnotation(field, "NotNull")) {

            oldAnnotator.addNotNullAnnotation(classOutline, field);
        }

        if (property.isCollection()) {
            oldAnnotator.addValidAnnotation(propertyName, classOutline.implClass.name(), field);

            // http://www.dimuthu.org/blog/2008/08/18/xml-schema-nillabletrue-vs-minoccurs0/comment-page-1/
            if (!oldAnnotator.hasAnnotation(field, "Size") &&
                    (maxOccurs != 0 || minOccurs != 0)) {

                if (property.isCollectionRequired()) {
                    oldAnnotator.addNotNullAnnotation(classOutline, field);
                }

                oldAnnotator.addSizeAnnotation(minOccurs, maxOccurs, null,
                        propertyName, classOutline.implClass.name(), field);
            }
        }

        String className = classOutline.implClass.name();
        XSType elementType = element.getType();

        oldAnnotator.addValidAnnotation(elementType, field, propertyName, className);

        // using https://github.com/jirutka/validator-collection to annotate Lists of primitives
        final XSSimpleType simpleType;
        if (!(elementType instanceof XSSimpleType)) {
            // is a complex type, get the base type
            simpleType = elementType.getBaseType().asSimpleType();
        } else {
            // simple type
            simpleType = elementType.asSimpleType();
        }

        JaxbValidationsAnnotator annotator = new JaxbValidationsAnnotator(field);

        if (options.isGenerateStringListAnnotations() && property.isCollection() && simpleType != null) {
            String minLength = getStringFacet(simpleType, XSFacet.FACET_MINLENGTH);
            String maxLength = getStringFacet(simpleType, XSFacet.FACET_MAXLENGTH);
            annotator.addEachSizeAnnotation(minLength, maxLength);

            String totalDigits = getStringFacet(simpleType, XSFacet.FACET_TOTALDIGITS);
            String fractionDigits = getStringFacet(simpleType, XSFacet.FACET_FRACTIONDIGITS);
            annotator.addEachDigitsAnnotation(totalDigits, fractionDigits);

            String minInclusive = getStringFacet(simpleType, XSFacet.FACET_MININCLUSIVE);
            String minExclusive = getStringFacet(simpleType, XSFacet.FACET_MINEXCLUSIVE);
            annotator.addEachDecimalMinAnnotation(minInclusive, minExclusive);

            String maxInclusive = getStringFacet(simpleType, XSFacet.FACET_MAXINCLUSIVE);
            String maxExclusive = getStringFacet(simpleType, XSFacet.FACET_MAXEXCLUSIVE);
            annotator.addEachDecimalMaxAnnotation(maxInclusive, maxExclusive);
        }

        attributeProcessor.processType(simpleType, field, propertyName, className);
    }

}
