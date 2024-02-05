package com.sun.tools.xjc.addon.krasa;

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
}
