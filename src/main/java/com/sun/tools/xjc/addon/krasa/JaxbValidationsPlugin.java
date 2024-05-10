package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.AttributeUseImpl;
import com.sun.xml.xsom.impl.ElementDecl;
import com.sun.xml.xsom.impl.SimpleTypeImpl;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.xml.sax.ErrorHandler;

/**
 *
 * NOTE: fractionDigits fixed attribute cannot be translated into a meaningful Validation.
 *
 * @author Francesco Illuminati
 * @author Vojtěch Krása
 * @author cocorossello
 */
public class JaxbValidationsPlugin extends Plugin {

    static final String NAMESPACE = "http://jaxb.dev.java.net/plugin/code-injector";

    JaxbValidationsOptions.Builder pluginOptionsBuilder = JaxbValidationsOptions.builder();
    JaxbValidationsOptions options;

    @Override
    public String getOptionName() {
        return JaxbValidationsArgument.PLUGIN_NAME;
    }

    @Override
    public int parseArgument(Options opt, String[] args, int index)
            throws BadCommandLineException, IOException {
        return JaxbValidationsArgument.parse(pluginOptionsBuilder, args[index]);
    }

    @Override
    public List<String> getCustomizationURIs() {
        return Collections.singletonList(NAMESPACE);
    }

    @Override
    public boolean isCustomizationTagName(String nsUri, String localName) {
        return nsUri.equals(NAMESPACE) && localName.equals("code");
    }

    @Override
    public String getUsage() {
        return new StringBuilder()
                .append("  -")
                .append(JaxbValidationsArgument.PLUGIN_OPTION_NAME)
                .append("      :  ")
                .append("inject Bean validation annotations (JSR 303)")
                .append(System.lineSeparator())
                .append("   Options:")
                .append(JaxbValidationsArgument.helpMessageWithPrefix("     "))
                .append(System.lineSeparator())
                .toString();
    }

    @Override
    public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
        pluginOptionsBuilder.verbose(opt.verbose);

        buildOptions();

        if (opt.verbose) {
            JaxbValidationsLogger.log(JaxbValidationsArgument.actualOptionValuesString(options, "    "));
        }

        for (ClassOutline classOutline : model.getClasses()) {
            List<CPropertyInfo> properties = classOutline.target.getProperties();

            for (CPropertyInfo property : properties) {

                String propertyName = property.getName(false);
                String className = classOutline.implClass.name();

                JaxbValidationsLogger logger =
                        new JaxbValidationsLogger(options.isVerbose(), className, propertyName);

                if (property instanceof CElementPropertyInfo) {
                    processElement(logger, (CElementPropertyInfo) property, classOutline, model);

                } else if (property instanceof CAttributePropertyInfo) {
                    processAttribute(logger, (CAttributePropertyInfo) property, classOutline, model);

                } else if (property instanceof CValuePropertyInfo) {
                    processAttribute(logger, (CValuePropertyInfo) property, classOutline, model);

                }
            }
        }
        return true;
    }

    // separate method to help testing
    void buildOptions() {
        options = pluginOptionsBuilder.build();
        pluginOptionsBuilder = null;
    }

    /**
     * XS:Element
     */
    void processElement(JaxbValidationsLogger logger, CElementPropertyInfo property,
            ClassOutline classOutline, Outline model) {

        XSParticle particle = (XSParticle) property.getSchemaComponent();
        ElementDecl element = (ElementDecl) particle.getTerm();

        String propertyName = property.getName(false);

        int minOccurs = particle.getMinOccurs().intValue();
        int maxOccurs = particle.getMaxOccurs().intValue();
        boolean required = property.isRequired();
        boolean nillable = element.isNillable();

        JFieldVar field = classOutline.implClass.fields().get(propertyName);
        XSType elementType = element.getType();

        JaxbValidationsAnnotator annotator =
                new JaxbValidationsAnnotator(
                        logger,
                        field,
                        options.getAnnotationFactory());

        if (options.isNotNullAnnotations() && !nillable &&
                (minOccurs > 0 || required || property.isCollectionRequired()) ) {
            String message = notNullMessage(classOutline, field);
            annotator.addNotNullAnnotation(classOutline, field, message);
        }

        if (property.isCollection()) {
            // add @Valid to all collections
            annotator.addValidAnnotation();

            if (maxOccurs != 0 || minOccurs != 0) {
                // http://www.dimuthu.org/blog/2008/08/18/xml-schema-nillabletrue-vs-minoccurs0/comment-page-1/
                annotator.addSizeAnnotation(minOccurs, maxOccurs, null);
            }
        }

        // using https://github.com/jirutka/validator-collection to annotate Lists of primitives
        final XSSimpleType simpleType;
        if (!(elementType instanceof XSSimpleType)) {
            // is a complex type, get the base type
            simpleType = elementType.getBaseType().asSimpleType();
        } else {
            // simple type
            simpleType = elementType.asSimpleType();
        }

        if (simpleType != null) {
            Facet facet = new Facet(simpleType);

            if ((options.isGenerateStringListAnnotations() && property.isCollection()) ) {
                annotator.addEachSizeAnnotation(facet.minLength(), facet.maxLength());
                annotator.addEachDigitsAnnotation(facet.totalDigits(), facet.fractionDigits());
                annotator.addEachDecimalMinAnnotation(facet.minInclusive(), facet.minExclusive());
                annotator.addEachDecimalMaxAnnotation(facet.maxInclusive(), facet.maxExclusive());
            }

            processType(logger, simpleType, field, annotator);
        }
    }

    /**
     * Attribute from parent declaration
     */
    void processAttribute(JaxbValidationsLogger logger, CValuePropertyInfo property,
            ClassOutline classOutline, Outline model) {
        String propertyName = property.getName(false);
        XSComponent definition = property.getSchemaComponent();
        SimpleTypeImpl particle = (SimpleTypeImpl) definition;
        XSSimpleType simpleType = particle.asSimpleType();
        JFieldVar field = classOutline.implClass.fields().get(propertyName);

        if (field != null) {
            JaxbValidationsAnnotator annotator =
                    new JaxbValidationsAnnotator(logger, field, options.getAnnotationFactory());

            processType(logger, simpleType, field, annotator);
        }
    }

    void processAttribute(JaxbValidationsLogger logger, CAttributePropertyInfo property,
            ClassOutline classOutline, Outline model) {
        String propertyName = property.getName(false);

        XSComponent definition = property.getSchemaComponent();
        AttributeUseImpl particle = (AttributeUseImpl) definition;
        XSSimpleType type = particle.getDecl().getType();
        JFieldVar var = classOutline.implClass.fields().get(propertyName);

        if (var != null) {
            JaxbValidationsAnnotator annotator =
                    new JaxbValidationsAnnotator(logger, var, options.getAnnotationFactory());

            if (particle.isRequired()) {
                String message = notNullMessage(classOutline, var);
                annotator.addNotNullAnnotation(classOutline, var, message);
            }

            processType(logger, type, var, annotator);
        }
    }


    void processType(JaxbValidationsLogger logger,
            XSSimpleType simpleType, JFieldVar field, JaxbValidationsAnnotator annotator) {

        Facet facet = new Facet(simpleType);
        FieldHelper fieldHelper = new FieldHelper(field);

        // add @Valid to complex types or custom elements with selected namespace
        if ((facet.targetNamespaceEquals(options.getTargetNamespace())) &&
                ((simpleType.isComplexType() || fieldHelper.isCustomType())) ) {
            annotator.addValidAnnotation();
        }

        if (fieldHelper.isString() || fieldHelper.isArray()) {
            annotator.addSizeAnnotation(facet.minLength(), facet.maxLength(), facet.length());

            if (options.isJpaAnnotations()) {
                annotator.addJpaColumnAnnotation(facet.maxLength());
            }
        }

        if (fieldHelper.isNumber()) {
            annotator.addDecimalMinAnnotation(facet.minInclusive(), false);
            annotator.addDecimalMinAnnotation(facet.minExclusive(), true);

            annotator.addDecimalMaxAnnotation(facet.maxInclusive(), false);
            annotator.addDecimalMaxAnnotation(facet.maxExclusive(), true);

            annotator.addDigitsAnnotation(facet.totalDigits(), facet.fractionDigits());

            if (options.isJpaAnnotations()) {
                annotator.addJpaColumnStringAnnotation(facet.totalDigits(), facet.fractionDigits());
            }
        }

        if (fieldHelper.isString()) {

            final List<String> patternList = facet.patternList();
            patternList.add(facet.pattern());

            final List<String> enumerationList = facet.enumerationList();
            enumerationList.add(facet.enumeration());

            XSSimpleType baseType = simpleType;
            while ((baseType = baseType.getSimpleBaseType()) != null) {
                if (baseType instanceof XSRestrictionSimpleType) {
                    Facet baseFacet = new Facet((XSRestrictionSimpleType) baseType);

                    patternList.add(baseFacet.pattern());
                    patternList.addAll(baseFacet.patternList());

                    enumerationList.add(baseFacet.enumeration());
                    enumerationList.addAll(baseFacet.enumerationList());
                }
            }

            List<String> adjustedPatterns = patternList.stream()
                    .filter(p -> isValidRegexp(p))
                    .map(p -> replaceRegexp(p))
                    .distinct()
                    .collect(Collectors.toList());

            // escaped enumuerations can be treated as patterns
            List<String> adjustedEnumerations = enumerationList.stream()
                    .filter(p -> p != null && !p.isEmpty())
                    .map(p -> escapeRegexp(p))
                    .distinct()
                    .collect(Collectors.toList());

            adjustedPatterns.addAll(adjustedEnumerations);

            LinkedHashSet<String> patternSet = new LinkedHashSet<>(adjustedPatterns);

            switch (patternSet.size()) {
                case 0:
                    // do nothing at all
                    break;
                case 1:
                    annotator.addSinglePatternAnnotation(patternSet.iterator().next());
                    break;
                default:
                    if (options.isSinglePattern()) {
                        annotator.addAlternativePatternListAnnotation(patternSet);
                    } else {
                        annotator.addPatternListAnnotation(patternSet);
                    }
            }
        }
    }

    /*
	 * \Q indicates begin of quoted regex text, \E indicates end of quoted regex text
     */
    static String escapeRegexp(String pattern) {
        return java.util.regex.Pattern.quote(pattern);
    }

    // cxf-codegen fix
    static boolean isValidRegexp(String pattern) {
        return pattern != null && !"\\c+".equals(pattern);
    }

    static String replaceRegexp(String pattern) {
        return pattern
                .replace("\\i", "[_:A-Za-z]")
                .replace("\\c", "[-._:A-Za-z0-9]");
    }

    String notNullMessage(ClassOutline classOutline, JFieldVar field) {
        final String className = classOutline.implClass.name();
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

        return message;
    }


}
