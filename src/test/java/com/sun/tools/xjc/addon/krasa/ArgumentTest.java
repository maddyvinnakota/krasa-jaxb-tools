package com.sun.tools.xjc.addon.krasa;

import com.sun.tools.xjc.BadCommandLineException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
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
    public void shouldBuildArguments() {
        List<String> list = ArgumentBuilder.builder()
                .add(Argument.targetNamespace, NAMESPACE)
                .add(Argument.JSR_349, true)
                .add(Argument.generateStringListAnnotations, true)
                .add(Argument.validationAnnotations, ANNOTATION)
                .getOptionList();

        Iterator<String> it = list.iterator();
        assertEquals(Argument.PLUGIN_OPTION_NAME, it.next());
        assertEquals(Argument.targetNamespace.getOptionValue(NAMESPACE), it.next());
        assertEquals(Argument.JSR_349.getOptionValue(true), it.next());
        assertEquals(Argument.generateStringListAnnotations.getOptionValue(true), it.next());
        assertEquals(Argument.validationAnnotations.getOptionValue(ANNOTATION), it.next());
    }

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
        assertEquals(plugin.notNullCustomMessage, MESSAGE);
        assertTrue(plugin.jpaAnnotations);
        assertTrue(plugin.verbose);

    }
}
