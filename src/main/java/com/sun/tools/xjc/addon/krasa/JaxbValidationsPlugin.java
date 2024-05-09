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
import com.sun.tools.xjc.outline.FieldOutline;
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

    JaxbValidationsOptions.Builder optionsBuilder = JaxbValidationsOptions.builder();
    JaxbValidationsOptions options;

    @Override
    public String getOptionName() {
        return JaxbValidationsArgument.PLUGIN_NAME;
    }

    @Override
    public int parseArgument(Options opt, String[] args, int index)
            throws BadCommandLineException, IOException {
        return JaxbValidationsArgument.parse(optionsBuilder, args[index]);
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
        if (opt.verbose) {
            optionsBuilder.verbose(true);
        }

        buildOptions();

        JaxbValidationsLogger logger = new JaxbValidationsLogger(options.isVerbose());

        logger.log(JaxbValidationsArgument.actualOptionValuesString(options, "    "));


        for (ClassOutline co : model.getClasses()) {
            List<CPropertyInfo> properties = co.target.getProperties();

            for (CPropertyInfo property : properties) {
                if (property instanceof CElementPropertyInfo) {
                    processElement((CElementPropertyInfo) property, co, model);

                } else if (property instanceof CAttributePropertyInfo) {
                    processAttribute((CAttributePropertyInfo) property, co, model);

                } else if (property instanceof CValuePropertyInfo) {
                    processAttribute((CValuePropertyInfo) property, co, model);

                }
            }
        }
        return true;
    }

    // must be a separate method to help testing
    void buildOptions() {
        options = optionsBuilder.build();
        optionsBuilder = null;
    }

    /**
     * XS:Element
     */
    public void processElement(CElementPropertyInfo property,
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
                new JaxbValidationsAnnotator(field,
                        options.getAnnotationFactory());

        if (options.isNotNullAnnotations() && !nillable &&
                (minOccurs > 0 || required || property.isCollectionRequired()) ) {
            String message = notNullMessage(classOutline, field);
            annotator.addNotNullAnnotation(classOutline, field, message);
        }

        Facet facet = new Facet(simpleType);

        if (property.isCollection()) {
            // add @Valid to all collections
            annotator.addValidAnnotation();

            if (maxOccurs != 0 || minOccurs != 0) {
                // http://www.dimuthu.org/blog/2008/08/18/xml-schema-nillabletrue-vs-minoccurs0/comment-page-1/
                annotator.addSizeAnnotation(minOccurs, maxOccurs, null);
            }
        }

        if (simpleType != null) {
            if ((options.isGenerateStringListAnnotations() && property.isCollection()) ) {
                annotator.addEachSizeAnnotation(facet.minLength(), facet.maxLength());
                annotator.addEachDigitsAnnotation(facet.totalDigits(), facet.fractionDigits());
                annotator.addEachDecimalMinAnnotation(facet.minInclusive(), facet.minExclusive());
                annotator.addEachDecimalMaxAnnotation(facet.maxInclusive(), facet.maxExclusive());
            }

            processType(simpleType, field, annotator);
        }
    }

    /**
     * Attribute from parent declaration
     */
    void processAttribute(CValuePropertyInfo property,
            ClassOutline clase, Outline model) {
        FieldOutline field = model.getField(property);
        String propertyName = property.getName(false);
        String className = clase.implClass.name();

//        logger.log("Attribute " + propertyName + " added to class " + className);
        XSComponent definition = property.getSchemaComponent();
        SimpleTypeImpl particle = (SimpleTypeImpl) definition;
        XSSimpleType type = particle.asSimpleType();
        JFieldVar var = clase.implClass.fields().get(propertyName);

        if (var != null) {
            JaxbValidationsAnnotator annotator =
                    new JaxbValidationsAnnotator(var, options.getAnnotationFactory());

            processType(type, var, annotator);
        }
    }

    void processAttribute(CAttributePropertyInfo property,
            ClassOutline clase, Outline model) {
        FieldOutline field = model.getField(property);
        String propertyName = property.getName(false);
        String className = clase.implClass.name();

//        logger.log("Attribute " + propertyName + " added to class " + className);
        XSComponent definition = property.getSchemaComponent();
        AttributeUseImpl particle = (AttributeUseImpl) definition;
        XSSimpleType type = particle.getDecl().getType();
        JFieldVar var = clase.implClass.fields().get(propertyName);

        if (var != null) {
            JaxbValidationsAnnotator annotator =
                    new JaxbValidationsAnnotator(var, options.getAnnotationFactory());

            if (particle.isRequired()) {
                String message = notNullMessage(clase, var);
                annotator.addNotNullAnnotation(clase, var, message);
            }

            processType(type, var, annotator);
        }
    }


    void processType(XSSimpleType simpleType, JFieldVar field, JaxbValidationsAnnotator annotator) {

        Facet facet = new Facet(simpleType);

        // add @Valid to complext types or custom elements with selected namespace
        String elemNs = simpleType.getTargetNamespace();
        if ((options.getTargetNamespace() != null && elemNs.startsWith(options.getTargetNamespace())) &&
                ((simpleType.isComplexType() || Utils.isCustomType(field))) ) {
            annotator.addValidAnnotation();
        }

        // TODO put this check in Field mng class
        if (field.type().name().equals("String") || field.type().isArray()) {
            annotator.addSizeAnnotation(facet.minLength(), facet.maxLength(), facet.length());

            // TODO put this check in Field mng class
            if (options.isJpaAnnotations()) {
                annotator.addJpaColumnAnnotation(facet.maxLength());
            }
        }


        if (Utils.isNumberField(field)) {

            if (!annotator.isAnnotatedWith(
                    options.getAnnotationFactory().getDecimalMinClass())) {
                annotator.addDecimalMinAnnotation(facet.minInclusive(), false);
                annotator.addDecimalMinAnnotation(facet.minExclusive(), true);
            }

            if (!annotator.isAnnotatedWith(
                    options.getAnnotationFactory().getDecimalMaxClass())) {
                annotator.addDecimalMaxAnnotation(facet.maxInclusive(), false);
                annotator.addDecimalMaxAnnotation(facet.maxExclusive(), true);
            }

            annotator.addDigitsAnnotation(facet.totalDigits(), facet.fractionDigits());
            if (options.isJpaAnnotations()) {
                annotator.addJpaColumnStringAnnotation(facet.totalDigits(), facet.fractionDigits());
            }
        }

        final String fieldName = field.type().name();
        if ("String".equals(fieldName)) {

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
    private static String escapeRegexp(String pattern) {
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

    private String notNullMessage(ClassOutline classOutline, JFieldVar field) {
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
