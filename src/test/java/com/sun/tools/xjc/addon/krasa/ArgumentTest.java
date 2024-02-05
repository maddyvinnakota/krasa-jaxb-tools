package com.sun.tools.xjc.addon.krasa;

import com.sun.tools.xjc.BadCommandLineException;
import java.io.IOException;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArgumentTest {

    private static final String NAMESPACE = "xyz";
    private static final String ANNOTATION = "javax";
    private static final String MESSAGE = "custom message";

    @Test
    public void shouldParseArguments() throws BadCommandLineException, IOException {
        JaxbValidationsPlugins plugin = new JaxbValidationsPlugins();

        List<String> arguments = ArgumentBuilder.builder()
                .add(Argument.targetNamespace, NAMESPACE)
                .add(Argument.JSR_349, true)
                .add(Argument.generateStringListAnnotations, true)
                .add(Argument.validationAnnotations, ANNOTATION)
                .add(Argument.generateNotNullAnnotations, true)
                .add(Argument.generateServiceValidationAnnotations, true)
                .add(Argument.notNullAnnotationsCustomMessages, MESSAGE)
                .add(Argument.jpa, true)
                .add(Argument.verbose, true)
                .getOptionList();

        String[] args = arguments.toArray(new String[arguments.size()]);

        for (int i=0; i<args.length; i++) {
            plugin.parseArgument(null, args, i);
        }

        assertEquals(NAMESPACE, plugin.targetNamespace);
        assertEquals(ValidationAnnotation.JAVAX, plugin.annotationFactory);
        assertTrue(plugin.jsr349);
        assertTrue(plugin.notNullAnnotations);
        assertTrue(plugin.generateStringListAnnotations);
        assertEquals(plugin.notNullCustomMessageText, MESSAGE);
        assertTrue(plugin.jpaAnnotations);
        assertTrue(plugin.verbose);

    }

    @Test
    public void shouldNotNullFlagsBeFalseByDefault() {
        JaxbValidationsPlugins plugin = new JaxbValidationsPlugins();

        assertFalse(plugin.notNullCustomMessage);
        assertFalse(plugin.notNullPrefixFieldName);
        assertFalse(plugin.notNullPrefixClassName);
    }

    @Test
    public void shouldSetNotNullAnnotationsCustomMessagesOnClassName()
            throws BadCommandLineException, IOException {

        String option = CustomMessageType.Classname.value();

        JaxbValidationsPlugins plugin = setupNotNullMessage(option);

        assertTrue(plugin.notNullCustomMessage);
        assertFalse(plugin.notNullPrefixFieldName);
        assertTrue(plugin.notNullPrefixClassName);
    }

    @Test
    public void shouldSetNotNullAnnotationsCustomMessagesOnFieldName()
            throws BadCommandLineException, IOException {

        String option = CustomMessageType.Fieldname.value();

        JaxbValidationsPlugins plugin = setupNotNullMessage(option);

        assertTrue(plugin.notNullCustomMessage);
        assertTrue(plugin.notNullPrefixFieldName);
        assertFalse(plugin.notNullPrefixClassName);
    }

    private JaxbValidationsPlugins setupNotNullMessage(String option)
            throws IOException, BadCommandLineException {
        JaxbValidationsPlugins plugin = new JaxbValidationsPlugins();

        List<String> arguments = ArgumentBuilder.builder()
                .add(Argument.notNullAnnotationsCustomMessages, option)
                .getOptionList();

        String[] args = arguments.toArray(new String[arguments.size()]);

        for (int i=0; i<args.length; i++) {
            plugin.parseArgument(null, args, i);
        }

        return plugin;
    }
}
