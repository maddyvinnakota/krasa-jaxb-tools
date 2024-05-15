package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.codemodel.JFieldVar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati 
 */
class FieldHelper {
    private final JFieldVar field;

    public FieldHelper(JFieldVar field) {
        this.field = field;
    }

    public boolean isString() {
        return field.type().name().equals("String");
    }

    public boolean isArray() {
        return field.type().isArray();
    }

    public boolean isCustomType() {
        return "JDirectClass".equals(field.type().getClass().getSimpleName());
    }

    private static final Set<String> NUMBERS = Arrays.stream(new Class<?>[]{
        BigDecimal.class,
        BigInteger.class,
        Byte.class,
        Short.class,
        Integer.class,
        Double.class,
        Float.class,
        Long.class})
            .map(c -> c.getSimpleName().toUpperCase())
            .collect(Collectors.toSet());

    public boolean isNumber() {
        return isFieldTypeNameNumber(field.type().name()) ||
                isFieldTypeFullNameNumber(field.type().fullName());
    }

    static boolean isFieldTypeNameNumber(String fieldTypeName) {
        return NUMBERS.contains(fieldTypeName.toUpperCase());
    }

    static boolean isFieldTypeFullNameNumber(String fieldTypeFullName) {
        try {
            if (isNumber(Class.forName(fieldTypeFullName))) {
                return true;
            }
        } catch (ClassNotFoundException ex) {
            // ignore
        }
        return false;
    }

    static boolean isNumber(Class<?> aClass) {
        return Number.class.isAssignableFrom(aClass);
    }

}
