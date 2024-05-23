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
        element("Numeric")
                .assertImportSimpleName("NotNull")
                .assertImportSimpleName("DecimalMin")
                .assertImportSimpleName("DecimalMax")
                .attribute("abyte")
                        .assertClass(byte.class)
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
                        .assertClass(int.class)
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("ainteger")
                        .assertClass(BigInteger.class)
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("along")
                        .assertClass(long.class)
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
                        .annotation("DecimalMin")
                            .assertParam("value", 1)
                            .assertParam("inclusive", true)
                            .end()
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("ashort")
                        .assertClass(short.class)
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
                        .assertClass(long.class)
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
                        .assertClass(int.class)
                        .annotation("NotNull").assertNoValues()
                        .end()

                .attribute("aunsignedByte")
                        .assertClass(short.class)
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
