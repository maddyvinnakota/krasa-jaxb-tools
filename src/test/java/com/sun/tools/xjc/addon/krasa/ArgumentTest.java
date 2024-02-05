package com.sun.tools.xjc.addon.krasa;

import java.util.Iterator;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
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

    private String targetNamespace = null;
    private Boolean jsr349 = null;
    private Boolean generateStringListAnnotations = null;
    private String validationAnnotations = null;
    private Boolean generateNotNullAnnotations = null;
    private Boolean generateServiceValidationAnnotations = null;
    private Boolean jpa = null;
    private String notNullAnnotationsCustomMessages = null;
    private Boolean verbose = null;

    @Test
    public void shouldTakeLastOfRepeatingArguments() {
        List<String> list = Argument.builder()
                .add(Argument.JSR_349, true)
                .add(Argument.JSR_349, false)
                .getList();

        assertEquals(2, list.size());
        assertEquals("-XJsr303Annotations", list.get(0));
        assertEquals("-XJsr303Annotations:JSR_349=false", list.get(1));
    }

    @Test
    public void shouldBuildArguments() {
        List<String> list = Argument.builder()
                .add(Argument.targetNamespace, NAMESPACE)
                .add(Argument.JSR_349, true)
                .add(Argument.generateStringListAnnotations, true)
                .add(Argument.validationAnnotations, ANNOTATION)
                .getList();

        Iterator<String> it = list.iterator();
        assertEquals(Argument.PLUGIN_OPTION_NAME, it.next());
        assertEquals(Argument.targetNamespace.getOptionValue(NAMESPACE), it.next());
        assertEquals(Argument.JSR_349.getOptionValue(true), it.next());
        assertEquals(Argument.generateStringListAnnotations.getOptionValue(true), it.next());
        assertEquals(Argument.validationAnnotations.getOptionValue(ANNOTATION), it.next());
    }

    @Test
    public void shouldParseArguments() {
        List<String> arguments = Argument.builder()
                .add(Argument.targetNamespace, NAMESPACE)
                .add(Argument.JSR_349, true)
                .add(Argument.generateStringListAnnotations, false)
                .add(Argument.validationAnnotations, ANNOTATION)
                .add(Argument.generateNotNullAnnotations, true)
                .add(Argument.generateServiceValidationAnnotations, false)
                .add(Argument.notNullAnnotationsCustomMessages, MESSAGE)
                .add(Argument.jpa, true)
                .add(Argument.verbose, false)
                .getList();


        Argument.Parser parser = Argument.parse(arguments.toArray(new String[arguments.size()]))
                .stringArgument(Argument.targetNamespace, v -> targetNamespace = v)
                .stringArgument(Argument.validationAnnotations, v -> validationAnnotations = v)
                .booleanArgument(Argument.JSR_349, v -> jsr349 = v)
                .booleanArgument(Argument.generateStringListAnnotations, v -> generateStringListAnnotations = v)
                .booleanArgument(Argument.generateNotNullAnnotations, v -> generateNotNullAnnotations = v)
                .booleanArgument(Argument.generateServiceValidationAnnotations, v -> generateServiceValidationAnnotations = v)
                .booleanArgument(Argument.jpa, v -> jpa = v)
                .stringArgument(Argument.notNullAnnotationsCustomMessages, v -> notNullAnnotationsCustomMessages = v)
                .booleanArgument(Argument.verbose, v -> verbose = v);


        assertEquals(targetNamespace, NAMESPACE);
        assertEquals(validationAnnotations, ANNOTATION);
        assertTrue(jsr349);
        assertFalse(generateStringListAnnotations);
        assertTrue(generateNotNullAnnotations);
        assertFalse(generateServiceValidationAnnotations);
        assertEquals(notNullAnnotationsCustomMessages, MESSAGE);
        assertTrue(jpa);
        assertFalse(verbose);

        assertEquals(1, Argument.returnOneIfOwnArgument("-XJsr303Annotations:targetNamespace=a"));
        assertEquals(1, Argument.returnOneIfOwnArgument("-XJsr303Annotations:JSR_349=true"));
        assertEquals(1, Argument.returnOneIfOwnArgument("-XJsr303Annotations"));
        assertEquals(0, Argument.returnOneIfOwnArgument("-XUnknown"));
    }
}
