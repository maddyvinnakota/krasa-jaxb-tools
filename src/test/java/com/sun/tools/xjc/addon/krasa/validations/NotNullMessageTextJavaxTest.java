package com.sun.tools.xjc.addon.krasa.validations;

public class NotNullMessageTextJavaxTest extends NotNullBase {

    public NotNullMessageTextJavaxTest() {
        super(ValidationsAnnotation.JAVAX,
                "{FieldName} in {ClassName} should be not null");
    }

}
