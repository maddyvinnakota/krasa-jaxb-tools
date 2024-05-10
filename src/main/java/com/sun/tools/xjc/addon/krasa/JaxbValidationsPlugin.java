package com.sun.tools.xjc.addon.krasa;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
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

        JaxbValidationsOptions options = buildOptions();
        if (opt.verbose) {
            JaxbValidationsLogger.log(
                    JaxbValidationsArgument.getActualOptionValuesAsString(options, "    "));
        }

        JaxbValidationsProcessor processor = new JaxbValidationsProcessor(options);

        for (ClassOutline classOutline : model.getClasses()) {
            List<CPropertyInfo> properties = classOutline.target.getProperties();

            for (CPropertyInfo property : properties) {

                String propertyName = property.getName(false);
                String className = classOutline.implClass.name();

                JaxbValidationsLogger logger =
                        new JaxbValidationsLogger(options.isVerbose(), className, propertyName);

                if (property instanceof CElementPropertyInfo) {
                    processor.processElement(logger, (CElementPropertyInfo) property, classOutline, model);

                } else if (property instanceof CAttributePropertyInfo) {
                    processor.processAttribute(logger, (CAttributePropertyInfo) property, classOutline, model);

                } else if (property instanceof CValuePropertyInfo) {
                    processor.processAttribute(logger, (CValuePropertyInfo) property, classOutline, model);

                }
            }
        }
        return true;
    }

    /** used in tests */
    JaxbValidationsOptions buildOptions() {
        return pluginOptionsBuilder.build();
    }

}
