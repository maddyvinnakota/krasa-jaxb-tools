package com.sun.tools.xjc.addon.krasa.validations;

/**
 * Unfortunately there isn't a way to add JSR349 validation to a generic in JAXB
 * (as far as I know) so I had to use a project that allows to add annotations
 * intended for the content of a collection.
 *
 * @see https://github.com/jirutka/validator-collection
 * @author Francesco Illuminati
 */
public class ListsBase extends AnnotationsMojoTestHelper {

    public ListsBase(JaxbValidationsAnnotation annotation) {
        super("lists", annotation);
    }

    public void testContainer() throws ClassNotFoundException {
        element("Container")
                .annotationSimpleName("Valid")
                .annotationCanonicalName("Size")
                .annotationCanonicalName("NotNull")
                .attribute("listOfString")
                        .annotation("Valid").assertNoValues()
                        .annotation("Size")
                            .assertParam("min", 0)
                            .assertParam("max", 5).end()
                        .annotation("EachSize")
                            .assertParam("min", 1)
                            .assertParam("max", 500).end()
                        .end()
                .attribute("listOfAddress")
                        .annotation("Size")
                            .assertParam("min", 3)
                            .assertParam("max", 7).end()
                        .annotation("NotNull").assertNoValues()
                        .annotation("Valid").assertNoValues()
                        .end()
                .attribute("listOfPercentage")
                        .annotation("Valid").assertNoValues()
                        .annotation("Size")
                            .assertParam("min", 2)
                            .assertParam("max", 4).end()
                        .annotation("EachDigits")
                            .assertParam("integer", 3)
                            .assertParam("fraction", 2).end()
                        .annotation("EachDecimalMin")
                            .assertParam("value", "0.00")
                            .assertParam("inclusive", false).end()
                        .annotation("EachDecimalMax")
                            .assertParam("value", "100.00")
                            .assertParam("inclusive", true).end()
                        .annotation("NotNull").assertNoValues();
    }

    public void testAddressType() {
        element("AddressType")
                .attribute("name")
                        .annotation("NotNull").assertNoValues()
                        .end()
                .attribute("formalTitle")
                        .annotation("Size")
                            .assertParam("max", 10);
    }
}
