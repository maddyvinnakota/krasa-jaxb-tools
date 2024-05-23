package com.sun.tools.xjc.addon.krasa.validations;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Francesco Illuminati
 */
public class NumericTest extends RunXJC2MojoTestHelper {

    public NumericTest() {
        super("numeric", "a");
    }

    public void test() throws ClassNotFoundException {
        withElement("Numeric")
                .assertImportSimpleName("NotNull")
                .assertImportSimpleName("DecimalMin")
                .assertImportSimpleName("DecimalMax")
                .withField("abyte")
                        .assertClass(byte.class)
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
                        .assertClass(int.class)
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("ainteger")
                        .assertClass(BigInteger.class)
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("along")
                        .assertClass(long.class)
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
                        .withAnnotation("DecimalMin")
                            .assertParam("value", 1)
                            .assertParam("inclusive", true)
                            .end()
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("ashort")
                        .assertClass(short.class)
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
                        .assertClass(long.class)
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
                        .assertClass(int.class)
                        .withAnnotation("NotNull").assertNoParameters()
                        .end()

                .withField("aunsignedByte")
                        .assertClass(short.class)
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
