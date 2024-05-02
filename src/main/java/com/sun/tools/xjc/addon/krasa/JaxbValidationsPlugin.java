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

        JaxbValidationsAnnotator annotator = new JaxbValidationsAnnotator(options, logger);

        JaxbValidationsAttributeProcessor attributeProcessor =
                new JaxbValidationsAttributeProcessor(options, annotator, logger);

        JaxbValidationsElementProcessor elementProcessor =
                new JaxbValidationsElementProcessor(options, annotator, attributeProcessor, logger);

        logger.log(JaxbValidationsArgument.actualOptionValuesString(options, "    "));

        for (ClassOutline co : model.getClasses()) {
            List<CPropertyInfo> properties = co.target.getProperties();

            for (CPropertyInfo property : properties) {
                if (property instanceof CElementPropertyInfo) {
                    elementProcessor.processElement((CElementPropertyInfo) property, co, model);

                } else if (property instanceof CAttributePropertyInfo) {
                    attributeProcessor.processAttribute((CAttributePropertyInfo) property, co, model);

                } else if (property instanceof CValuePropertyInfo) {
                    attributeProcessor.processAttribute((CValuePropertyInfo) property, co, model);

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
}
