package com.sun.tools.xjc.addon.krasa.validations;

import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidationsAnnotationTest {

    @Test
    public void testConversionFromJavaxClassNames() throws ClassNotFoundException {
        assertEquals("javax.validation.Valid",
                JaxbValidationsAnnotation.JAVAX.getCanonicalClassName("Valid"));
        assertEquals("javax.validation.constraints.NotNull",
                JaxbValidationsAnnotation.JAVAX.getCanonicalClassName("NotNull"));
        assertEquals("javax.validation.constraints.Size",
                JaxbValidationsAnnotation.JAVAX.getCanonicalClassName("Size"));
        assertEquals("javax.validation.constraints.Digits",
                JaxbValidationsAnnotation.JAVAX.getCanonicalClassName("Digits"));
        assertEquals("javax.validation.constraints.DecimalMin",
                JaxbValidationsAnnotation.JAVAX.getCanonicalClassName("DecimalMin"));
        assertEquals("javax.validation.constraints.DecimalMax",
                JaxbValidationsAnnotation.JAVAX.getCanonicalClassName("DecimalMax"));
        assertEquals("javax.validation.constraints.Pattern",
                JaxbValidationsAnnotation.JAVAX.getCanonicalClassName("Pattern"));
        assertEquals("javax.validation.constraints.Pattern.List",
                JaxbValidationsAnnotation.JAVAX.getCanonicalClassName("List"));
    }

    @Test
    public void testConversionFromJakartaClassNames() throws ClassNotFoundException {
        assertEquals("jakarta.validation.Valid",
                JaxbValidationsAnnotation.JAKARTA.getCanonicalClassName("Valid"));
        assertEquals("jakarta.validation.constraints.NotNull",
                JaxbValidationsAnnotation.JAKARTA.getCanonicalClassName("NotNull"));
        assertEquals("jakarta.validation.constraints.Size",
                JaxbValidationsAnnotation.JAKARTA.getCanonicalClassName("Size"));
        assertEquals("jakarta.validation.constraints.Digits",
                JaxbValidationsAnnotation.JAKARTA.getCanonicalClassName("Digits"));
        assertEquals("jakarta.validation.constraints.DecimalMin",
                JaxbValidationsAnnotation.JAKARTA.getCanonicalClassName("DecimalMin"));
        assertEquals("jakarta.validation.constraints.DecimalMax",
                JaxbValidationsAnnotation.JAKARTA.getCanonicalClassName("DecimalMax"));
        assertEquals("jakarta.validation.constraints.Pattern",
                JaxbValidationsAnnotation.JAKARTA.getCanonicalClassName("Pattern"));
        assertEquals("jakarta.validation.constraints.Pattern.List",
                JaxbValidationsAnnotation.JAKARTA.getCanonicalClassName("List"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowExceptionIfJavaxClassNotFound() throws ClassNotFoundException {
        JaxbValidationsAnnotation.JAVAX.getCanonicalClassName("NotExistentClassName");
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowExceptionIfJakartaClassNotFound() throws ClassNotFoundException {
        JaxbValidationsAnnotation.JAKARTA.getCanonicalClassName("NotExistentClassName");
    }
}
