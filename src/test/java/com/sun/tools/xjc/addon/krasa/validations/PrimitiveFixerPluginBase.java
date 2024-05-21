package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.PrimitiveFixerPlugin;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class PrimitiveFixerPluginBase extends AnnotationsMojoTestHelper {

    public PrimitiveFixerPluginBase(ValidationsAnnotation annotation) {
        super("primitive", annotation);
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add("-" + PrimitiveFixerPlugin.PLUGIN_NAME)
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

    public void test() throws ClassNotFoundException {
        element("Primitive")
                .annotationSimpleName("NotNull")
                .annotationSimpleName("DecimalMin")
                .annotationSimpleName("DecimalMax")
                .attribute("abyte")
                        .assertClass(Byte.class)
                        .annotation("NotNull").assertNoValues()
                        .annotation("DecimalMin")
                                .assertParam("value", -128)
                                .assertParam("inclusive", true)
                                .end()
                        .annotation("DecimalMax")
                                .assertParam("value", 127)
                                .assertParam("inclusive", true)
                                .end()
                        .end()

                .attribute("adecimal")
                        .assertClass(BigDecimal.class)
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("aint")
                        .assertClass(Integer.class)
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("ainteger")
                        .assertClass(BigInteger.class)
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("along")
                        .assertClass(Long.class)
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("anegativeInteger")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMax")
                                .assertParam("value", -1)
                                .assertParam("inclusive", true)
                                .end()
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("anonNegativeInteger")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMin")
                                .assertParam("value", 0)
                                .assertParam("inclusive", true)
                                .end()
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("anonPositiveInteger")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMax")
                            .assertParam("value", 0)
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("apositiveInteger")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMin")
                            .assertParam("value", 1)
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("ashort")
                        .assertClass(Short.class)
                        .annotation("DecimalMin")
                            .assertParam("value", -32768)
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("DecimalMax")
                            .assertParam("value", 32767)
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("aunsignedLong")
                        .assertClass(BigInteger.class)
                        .annotation("DecimalMin")
                            .assertParam("value", 0)
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("DecimalMax")
                            .assertParam("value", new BigDecimal("18446744073709551615"))
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("aunsignedInt")
                        .assertClass(Long.class)
                        .annotation("DecimalMin")
                            .assertParam("value", 0)
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("DecimalMax")
                            .assertParam("value", 4294967295L)
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("aunsignedShort")
                        .assertClass(Integer.class)
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("aunsignedByte")
                        .assertClass(Short.class)
                        .annotation("DecimalMin")
                            .assertParam("value", 0)
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("DecimalMax")
                            .assertParam("value", 255)
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("NotNull").assertNoValues()
                        .end();
    }

}
