package com.sun.tools.xjc.addon.krasa;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
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
public class JaxbValidationsArgumentTest {

    private static final String NAMESPACE = "xyz";
    private static final String ANNOTATION = "javax";
    private static final String MESSAGE = "custom message";

    @Test
    public void shouldParseArguments() throws BadCommandLineException, IOException {
        JaxbValidationsPlugin plugin = new JaxbValidationsPlugin();

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
        Options opt = new Options();

        for (int i=0; i<args.length; i++) {
            plugin.parseArgument(opt, args, i);
        }

        assertEquals(NAMESPACE, plugin.options.targetNamespace);
        assertEquals(JaxbValidationsAnnotation.JAVAX, plugin.options.annotationFactory);
        assertTrue(plugin.options.jsr349);
        assertTrue(plugin.options.notNullAnnotations);
        assertTrue(plugin.options.generateStringListAnnotations);
        assertEquals(plugin.options.notNullCustomMessageText, MESSAGE);
        assertTrue(plugin.options.jpaAnnotations);
        assertTrue(plugin.options.verbose);

    }

    @Test
    public void shouldNotNullFlagsBeFalseByDefault() {
        JaxbValidationsPlugin plugin = new JaxbValidationsPlugin();

        assertFalse(plugin.options.notNullCustomMessage);
        assertFalse(plugin.options.notNullPrefixFieldName);
        assertFalse(plugin.options.notNullPrefixClassName);
    }

    @Test
    public void shouldSetNotNullAnnotationsCustomMessagesOnClassName()
            throws BadCommandLineException, IOException {

        String option = CustomMessageType.Classname.value();

        JaxbValidationsPlugin plugin = setupNotNullMessage(option);

        assertTrue(plugin.options.notNullCustomMessage);
        assertFalse(plugin.options.notNullPrefixFieldName);
        assertTrue(plugin.options.notNullPrefixClassName);
    }

    @Test
    public void shouldSetNotNullAnnotationsCustomMessagesOnFieldName()
            throws BadCommandLineException, IOException {

        String option = CustomMessageType.Fieldname.value();

        JaxbValidationsPlugin plugin = setupNotNullMessage(option);

        assertTrue(plugin.options.notNullCustomMessage);
        assertTrue(plugin.options.notNullPrefixFieldName);
        assertFalse(plugin.options.notNullPrefixClassName);
    }

    private JaxbValidationsPlugin setupNotNullMessage(String option)
            throws IOException, BadCommandLineException {
        JaxbValidationsPlugin plugin = new JaxbValidationsPlugin();

        List<String> arguments = ArgumentBuilder.builder()
                .add(Argument.notNullAnnotationsCustomMessages, option)
                .getOptionList();

        String[] args = arguments.toArray(new String[arguments.size()]);
        Options opt = new Options();

        for (int i=0; i<args.length; i++) {
            plugin.parseArgument(opt, args, i);
        }

        return plugin;
    }
}
