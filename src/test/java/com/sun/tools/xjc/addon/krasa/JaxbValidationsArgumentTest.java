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

        JaxbValidationsOptions options = plugin.buildOptions();

        assertEquals(NAMESPACE, options.getTargetNamespace());
        assertEquals(JaxbValidationsAnnotation.JAVAX, options.getAnnotationFactory());
        assertTrue(options.isJsr349());
        assertTrue(options.isNotNullAnnotations());
        assertTrue(options.isGenerateStringListAnnotations());
        assertEquals(options.getNotNullCustomMessageText(), MESSAGE);
        assertTrue(options.isJpaAnnotations());
        assertTrue(options.isVerbose());

    }

    @Test
    public void shouldNotNullFlagsBeFalseByDefault() {
        JaxbValidationsPlugin plugin = new JaxbValidationsPlugin();
        JaxbValidationsOptions options = plugin.buildOptions();

        assertFalse(options.isNotNullCustomMessage());
        assertFalse(options.isNotNullPrefixFieldName());
        assertFalse(options.isNotNullPrefixClassName());
    }

    @Test
    public void shouldSetNotNullAnnotationsCustomMessagesOnClassName()
            throws BadCommandLineException, IOException {

        String option = CustomMessageType.Classname.value();

        JaxbValidationsPlugin plugin = setupNotNullMessage(option);
        JaxbValidationsOptions options = plugin.buildOptions();

        assertTrue(options.isNotNullCustomMessage());
        assertFalse(options.isNotNullPrefixFieldName());
        assertTrue(options.isNotNullPrefixClassName());
    }

    @Test
    public void shouldSetNotNullAnnotationsCustomMessagesOnFieldName()
            throws BadCommandLineException, IOException {

        String option = CustomMessageType.Fieldname.value();

        JaxbValidationsPlugin plugin = setupNotNullMessage(option);
        JaxbValidationsOptions options = plugin.buildOptions();

        assertTrue(options.isNotNullCustomMessage());
        assertTrue(options.isNotNullPrefixFieldName());
        assertFalse(options.isNotNullPrefixClassName());
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

    @Test
    public void shouldConvertToBooleanWithDefault() {
        assertTrue(JaxbValidationsArgument.toBoolean("True", false));
        assertTrue(JaxbValidationsArgument.toBoolean("true", false));
        assertTrue(JaxbValidationsArgument.toBoolean("TRUE", false));
        assertTrue(JaxbValidationsArgument.toBoolean(" true", false));
        assertTrue(JaxbValidationsArgument.toBoolean("true ", false));
        assertTrue(JaxbValidationsArgument.toBoolean(" true ", false));

        assertFalse(JaxbValidationsArgument.toBoolean("False", true));
        assertFalse(JaxbValidationsArgument.toBoolean("false", true));
        assertFalse(JaxbValidationsArgument.toBoolean("FALSE", true));
        assertFalse(JaxbValidationsArgument.toBoolean("FalSe", true));
    }

    @Test
    public void shouldConvertToBoolean() {
        assertTrue(JaxbValidationsArgument.toBoolean("True"));
        assertTrue(JaxbValidationsArgument.toBoolean("true"));
        assertTrue(JaxbValidationsArgument.toBoolean("TRUE"));
        assertTrue(JaxbValidationsArgument.toBoolean(" true"));
        assertTrue(JaxbValidationsArgument.toBoolean("true "));
        assertTrue(JaxbValidationsArgument.toBoolean(" true "));

        assertFalse(JaxbValidationsArgument.toBoolean("False"));
        assertFalse(JaxbValidationsArgument.toBoolean("false"));
        assertFalse(JaxbValidationsArgument.toBoolean("FALSE"));
        assertFalse(JaxbValidationsArgument.toBoolean("FalSe"));
    }

}
