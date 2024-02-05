package com.sun.tools.xjc.addon.krasa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CustomMessageTypeTest {

    @Test
    public void shouldRecognizeFieldName() {
        assertTrue(CustomMessageType.Fieldname.equalsIgnoreCase("fieldName"));
        assertTrue(CustomMessageType.Fieldname.equalsIgnoreCase("FieldName"));
        assertTrue(CustomMessageType.Fieldname.equalsIgnoreCase("fieldname"));
        assertTrue(CustomMessageType.Fieldname.equalsIgnoreCase("fIeLdNAmE"));

        assertFalse(CustomMessageType.Fieldname.equalsIgnoreCase("field name"));
    }

    @Test
    public void shouldRecognizeClassName() {
        assertTrue(CustomMessageType.Classname.equalsIgnoreCase("className"));
        assertTrue(CustomMessageType.Classname.equalsIgnoreCase("ClassName"));
        assertTrue(CustomMessageType.Classname.equalsIgnoreCase("classname"));
        assertTrue(CustomMessageType.Classname.equalsIgnoreCase("cLaSsNAmE"));

        assertFalse(CustomMessageType.Classname.equalsIgnoreCase("Class name"));
    }

}
