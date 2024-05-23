package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.PrimitiveFixerPlugin;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class PrimitiveFixerPluginTest extends RunXJC2MojoTestHelper {

    public PrimitiveFixerPluginTest() {
        super("primitive", "a");
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
        withElement("Primitive")
                .assertImportSimpleName("NotNull")
                .assertImportSimpleName("DecimalMin")
                .assertImportSimpleName("DecimalMax")
                .withField("abyte")
                        .assertClass(Byte.class)
                        .withAnnotation("NotNull").assertNoParameters()
                        .withAnnotation("DecimalMin")
                                .assertParam("value", -128)
                                .assertParam("inclusive", true)
                                .end()
                        .withAnnotation("DecimalMax")
                                .assertParam("value", 127)
                                .assertParam("inclusive", true)
                                .end()
                        .end()

                .withField("adecimal")
                        .assertClass(BigDecimal.class)
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("aint")
                        .assertClass(Integer.class)
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("ainteger")
                        .assertClass(BigInteger.class)
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("along")
                        .assertClass(Long.class)
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("anegativeInteger")
                        .assertClass(BigInteger.class)
                        .withAnnotation("DecimalMax")
                                .assertParam("value", -1)
                                .assertParam("inclusive", true)
                                .end()
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("anonNegativeInteger")
                        .assertClass(BigInteger.class)
                        .withAnnotation("DecimalMin")
                                .assertParam("value", 0)
                                .assertParam("inclusive", true)
                                .end()
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("anonPositiveInteger")
                        .assertClass(BigInteger.class)
                        .withAnnotation("DecimalMax")
                            .assertParam("value", 0)
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("apositiveInteger")
                        .assertClass(BigInteger.class)
                        .withAnnotation("DecimalMin")
                            .assertParam("value", 1)
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("ashort")
                        .assertClass(Short.class)
                        .withAnnotation("DecimalMin")
                            .assertParam("value", -32768)
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("DecimalMax")
                            .assertParam("value", 32767)
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("aunsignedLong")
                        .assertClass(BigInteger.class)
                        .withAnnotation("DecimalMin")
                            .assertParam("value", 0)
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("DecimalMax")
                            .assertParam("value", new BigDecimal("18446744073709551615"))
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("aunsignedInt")
                        .assertClass(Long.class)
                        .withAnnotation("DecimalMin")
                            .assertParam("value", 0)
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("DecimalMax")
                            .assertParam("value", 4294967295L)
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("aunsignedShort")
                        .assertClass(Integer.class)
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("aunsignedByte")
                        .assertClass(Short.class)
                        .withAnnotation("DecimalMin")
                            .assertParam("value", 0)
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("DecimalMax")
                            .assertParam("value", 255)
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("NotNull").assertNoParameters()
                        .end();
    }

}
