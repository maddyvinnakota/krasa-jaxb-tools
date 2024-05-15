package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import java.util.Iterator;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati 
 */
public class ArgumentBuilderTest {

    private static final String NAMESPACE = "xyz";
    private static final String ANNOTATION = "javax";

    @Test
    public void shouldBuildArguments() {
        List<String> list = ArgumentBuilder.builder()
                .add(ValidationsArgument.targetNamespace, NAMESPACE)
                .add(ValidationsArgument.JSR_349, true)
                .add(ValidationsArgument.generateStringListAnnotations, true)
                .add(ValidationsArgument.validationAnnotations, ANNOTATION)
                .getOptionList();

        Iterator<String> it = list.iterator();
        assertEquals(JaxbValidationsPlugin.PLUGIN_OPTION_NAME, it.next());
        assertEquals(ValidationsArgument.targetNamespace.withValue(NAMESPACE), it.next());
        assertEquals(ValidationsArgument.JSR_349.withValue("true"), it.next());
        assertEquals(ValidationsArgument.generateStringListAnnotations.withValue("true"), it.next());
        assertEquals(ValidationsArgument.validationAnnotations.withValue(ANNOTATION), it.next());
    }

}
