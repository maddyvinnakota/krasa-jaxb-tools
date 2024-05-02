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
                .add(JaxbValidationsArgument.targetNamespace, NAMESPACE)
                .add(JaxbValidationsArgument.JSR_349, true)
                .add(JaxbValidationsArgument.generateStringListAnnotations, true)
                .add(JaxbValidationsArgument.validationAnnotations, ANNOTATION)
                .add(JaxbValidationsArgument.generateNotNullAnnotations, true)
                .add(JaxbValidationsArgument.generateServiceValidationAnnotations, true)
                .add(JaxbValidationsArgument.notNullAnnotationsCustomMessages, MESSAGE)
                .add(JaxbValidationsArgument.jpa, true)
                .add(JaxbValidationsArgument.verbose, true)
                .getOptionList();

        String[] args = arguments.toArray(new String[arguments.size()]);
        Options opt = new Options();

        for (int i=0; i<args.length; i++) {
            plugin.parseArgument(opt, args, i);
        }

        plugin.buildOptions();

        assertEquals(NAMESPACE, plugin.options.getTargetNamespace());
        assertEquals(JaxbValidationsAnnotation.JAVAX, plugin.options.getAnnotationFactory());
        assertTrue(plugin.options.isJsr349());
        assertTrue(plugin.options.isNotNullAnnotations());
        assertTrue(plugin.options.isGenerateStringListAnnotations());
        assertEquals(plugin.options.getNotNullCustomMessageText(), MESSAGE);
        assertTrue(plugin.options.isJpaAnnotations());
        assertTrue(plugin.options.isVerbose());

    }

    @Test
    public void shouldNotNullFlagsBeFalseByDefault() {
        JaxbValidationsPlugin plugin = new JaxbValidationsPlugin();
        plugin.buildOptions();

        assertFalse(plugin.options.isNotNullCustomMessage());
        assertFalse(plugin.options.isNotNullPrefixFieldName());
        assertFalse(plugin.options.isNotNullPrefixClassName());
    }

    @Test
    public void shouldSetNotNullAnnotationsCustomMessagesOnClassName()
            throws BadCommandLineException, IOException {

        String option = CustomMessageType.Classname.value();

        JaxbValidationsPlugin plugin = setupNotNullMessage(option);

        assertTrue(plugin.options.isNotNullCustomMessage());
        assertFalse(plugin.options.isNotNullPrefixFieldName());
        assertTrue(plugin.options.isNotNullPrefixClassName());
    }

    @Test
    public void shouldSetNotNullAnnotationsCustomMessagesOnFieldName()
            throws BadCommandLineException, IOException {

        String option = CustomMessageType.Fieldname.value();

        JaxbValidationsPlugin plugin = setupNotNullMessage(option);

        assertTrue(plugin.options.isNotNullCustomMessage());
        assertTrue(plugin.options.isNotNullPrefixFieldName());
        assertFalse(plugin.options.isNotNullPrefixClassName());
    }

    private JaxbValidationsPlugin setupNotNullMessage(String option)
            throws IOException, BadCommandLineException {
        JaxbValidationsPlugin plugin = new JaxbValidationsPlugin();

        List<String> arguments = ArgumentBuilder.builder()
                .add(JaxbValidationsArgument.notNullAnnotationsCustomMessages, option)
                .getOptionList();

        String[] args = arguments.toArray(new String[arguments.size()]);
        Options opt = new Options();

        for (int i=0; i<args.length; i++) {
            plugin.parseArgument(opt, args, i);
        }

        plugin.buildOptions();
        return plugin;
    }
}
