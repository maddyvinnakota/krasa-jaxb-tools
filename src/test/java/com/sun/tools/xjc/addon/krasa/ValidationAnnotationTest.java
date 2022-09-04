package com.sun.tools.xjc.addon.krasa;

import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidationAnnotationTest {

    @Test
    public void testConversionFromJavaxClassNames() throws ClassNotFoundException {
        assertEquals("javax.validation.Valid",
                ValidationAnnotation.JAVAX.getCanonicalClassName("Valid"));
        assertEquals("javax.validation.constraints.NotNull",
                ValidationAnnotation.JAVAX.getCanonicalClassName("NotNull"));
        assertEquals("javax.validation.constraints.Size",
                ValidationAnnotation.JAVAX.getCanonicalClassName("Size"));
        assertEquals("javax.validation.constraints.Digits",
                ValidationAnnotation.JAVAX.getCanonicalClassName("Digits"));
        assertEquals("javax.validation.constraints.DecimalMin",
                ValidationAnnotation.JAVAX.getCanonicalClassName("DecimalMin"));
        assertEquals("javax.validation.constraints.DecimalMax",
                ValidationAnnotation.JAVAX.getCanonicalClassName("DecimalMax"));
        assertEquals("javax.validation.constraints.Pattern",
                ValidationAnnotation.JAVAX.getCanonicalClassName("Pattern"));
        assertEquals("javax.validation.constraints.Pattern.List",
                ValidationAnnotation.JAVAX.getCanonicalClassName("List"));
    }

    @Test
    public void testConversionFromJakartaClassNames() throws ClassNotFoundException {
        assertEquals("jakarta.validation.Valid",
                ValidationAnnotation.JAKARTA.getCanonicalClassName("Valid"));
        assertEquals("jakarta.validation.constraints.NotNull",
                ValidationAnnotation.JAKARTA.getCanonicalClassName("NotNull"));
        assertEquals("jakarta.validation.constraints.Size",
                ValidationAnnotation.JAKARTA.getCanonicalClassName("Size"));
        assertEquals("jakarta.validation.constraints.Digits",
                ValidationAnnotation.JAKARTA.getCanonicalClassName("Digits"));
        assertEquals("jakarta.validation.constraints.DecimalMin",
                ValidationAnnotation.JAKARTA.getCanonicalClassName("DecimalMin"));
        assertEquals("jakarta.validation.constraints.DecimalMax",
                ValidationAnnotation.JAKARTA.getCanonicalClassName("DecimalMax"));
        assertEquals("jakarta.validation.constraints.Pattern",
                ValidationAnnotation.JAKARTA.getCanonicalClassName("Pattern"));
        assertEquals("jakarta.validation.constraints.Pattern.List",
                ValidationAnnotation.JAKARTA.getCanonicalClassName("List"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowExceptionIfJavaxClassNotFound() throws ClassNotFoundException {
        ValidationAnnotation.JAVAX.getCanonicalClassName("NotExistentClassName");
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowExceptionIfJakartaClassNotFound() throws ClassNotFoundException {
        ValidationAnnotation.JAKARTA.getCanonicalClassName("NotExistentClassName");
    }
}
