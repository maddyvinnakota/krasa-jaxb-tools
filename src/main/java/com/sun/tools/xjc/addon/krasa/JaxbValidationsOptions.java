package com.sun.tools.xjc.addon.krasa;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsOptions {
    String targetNamespace = null;
    boolean jsr349 = false;
    boolean verbose = false;
    boolean notNullAnnotations = true;
    boolean notNullCustomMessage = false;
    boolean notNullPrefixFieldName = false;
    boolean notNullPrefixClassName = false;
    String notNullCustomMessageText = null;
    boolean jpaAnnotations = false;
    boolean generateStringListAnnotations;
    JaxbValidationsAnnotation annotationFactory = JaxbValidationsAnnotation.JAVAX;

}
