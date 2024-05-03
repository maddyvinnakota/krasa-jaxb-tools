package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JFieldVar;
import static com.sun.tools.xjc.addon.krasa.JaxbValidationsAnnotator.getIntegerFacet;
import static com.sun.tools.xjc.addon.krasa.JaxbValidationsAnnotator.getStringFacet;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.ElementDecl;
import java.lang.annotation.Annotation;

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
        boolean required = property.isRequired();
        boolean nillable = element.isNillable();
        String propertyName = property.getName(false);

        JFieldVar field = classOutline.implClass.fields().get(propertyName);
        String className = classOutline.implClass.name();
        XSType elementType = element.getType();

        // using https://github.com/jirutka/validator-collection to annotate Lists of primitives
        final XSSimpleType simpleType;
        if (!(elementType instanceof XSSimpleType)) {
            // is a complex type, get the base type
            simpleType = elementType.getBaseType().asSimpleType();
        } else {
            // simple type
            simpleType = elementType.asSimpleType();
        }

        JaxbValidationsAnnotator annotator =
                new JaxbValidationsAnnotator(field, options.getAnnotationFactory());


        if (options.isNotNullAnnotations() && !nillable &&
                (minOccurs > 0 || required || property.isCollectionRequired()) ) {
            annotator.addNotNullAnnotation(notNullMessage(classOutline, field));
        }

        if (property.isCollection()) {
            // add @Valid to all collections
            annotator.addValidAnnotation();

            if (maxOccurs != 0 || minOccurs != 0) {
                // http://www.dimuthu.org/blog/2008/08/18/xml-schema-nillabletrue-vs-minoccurs0/comment-page-1/

                oldAnnotator.addSizeAnnotation(minOccurs, maxOccurs, null,
                        propertyName, classOutline.implClass.name(), field);
            }
        }

        // add @Valid to complext type elements with selected namespace
        String elemNs = elementType.getTargetNamespace();
        if ((options.getTargetNamespace() == null || elemNs.startsWith(options.getTargetNamespace())) &&
                (elementType.isComplexType() || Utils.isCustomType(field)) ) {
            annotator.addValidAnnotation();
        }

        if (simpleType == null) {
            return; // it's a complex type and we don't manage it here
        }


        if (options.isGenerateStringListAnnotations() && property.isCollection() && simpleType != null) {
            Integer minLength = getIntegerFacet(simpleType, XSFacet.FACET_MINLENGTH);
            Integer maxLength = getIntegerFacet(simpleType, XSFacet.FACET_MAXLENGTH);
            annotator.addEachSizeAnnotation(minLength, maxLength);

            Integer totalDigits = getIntegerFacet(simpleType, XSFacet.FACET_TOTALDIGITS);
            Integer fractionDigits = getIntegerFacet(simpleType, XSFacet.FACET_FRACTIONDIGITS);
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

    private String notNullMessage(ClassOutline classOutline, JFieldVar field) {
        final String className = classOutline.implClass.name();
        final Class<? extends Annotation> notNullClass =
                options.getAnnotationFactory().getNotNullClass();

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

        return message;
    }
}
