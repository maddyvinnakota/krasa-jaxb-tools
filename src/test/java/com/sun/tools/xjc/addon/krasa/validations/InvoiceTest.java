package com.sun.tools.xjc.addon.krasa.validations;

/**
 * Validation API 2.0 supports inclusive for @DecimalMin and @DecimalMax
 *
 * @see https://github.com/krasa/krasa-jaxb-tools/issues/38
 *
 * @author Francesco Illuminati
 */
public class InvoiceTest extends RunXJC2MojoTestHelper {

    public InvoiceTest() {
        super("invoice", "a");
    }

    public void test() throws ClassNotFoundException {
        element("Invoice")
                .assertImportSimpleName("DecimalMin")
                .assertImportSimpleName("NotNull")
                .attribute("amount")
                        .annotation("DecimalMin")
                            .assertParam("value", 0)
                            .assertParam("inclusive", false)
                            .end()
                        .annotation("NotNull").assertNoValues();
    }

}
