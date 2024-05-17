package com.sun.tools.xjc.addon.krasa.validations;


public class ClassNameJavaxTest extends ClassNameBase {

    public ClassNameJavaxTest() {
        super(ValidationsAnnotation.JAVAX);
    }

    @Override
    public String getAnnotationFileName() {
        return "ClassNameTest-JAVAX-annotation.txt";
    }

}
