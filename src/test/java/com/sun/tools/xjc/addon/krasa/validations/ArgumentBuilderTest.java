package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Iterator;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArgumentBuilderTest {

    private static final String NAMESPACE = "xyz";
    private static final String ANNOTATION = "javax";

    @Test
    public void shouldBuildArguments() {
        List<String> list = ArgumentBuilder.builder()
                .add(JaxbValidationsArgument.targetNamespace, NAMESPACE)
                .add(JaxbValidationsArgument.JSR_349, true)
                .add(JaxbValidationsArgument.generateStringListAnnotations, true)
                .add(JaxbValidationsArgument.validationAnnotations, ANNOTATION)
                .getOptionList();

        Iterator<String> it = list.iterator();
        assertEquals(JaxbValidationsArgument.PLUGIN_OPTION_NAME, it.next());
        assertEquals(JaxbValidationsArgument.targetNamespace.withValue(NAMESPACE), it.next());
        assertEquals(JaxbValidationsArgument.JSR_349.withValue("true"), it.next());
        assertEquals(JaxbValidationsArgument.generateStringListAnnotations.withValue("true"), it.next());
        assertEquals(JaxbValidationsArgument.validationAnnotations.withValue(ANNOTATION), it.next());
    }

}
