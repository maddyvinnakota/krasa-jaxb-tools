package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JFieldVar;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Vojtech Krasa
 */
class Utils {

    /**
     * @return the boolean value of v (no case sensitive) or null otherwise.
     */
    public static boolean toBoolean(String v, Boolean defaultIfNull) {
        if (v != null) {
            return toBoolean(v);
        }
        return defaultIfNull != null ? defaultIfNull : false;
    }

    /**
     * @return the boolean value of v (no case sensitive) or null otherwise.
     */
    public static Boolean toBoolean(String v) {
        if (v != null) {
            String lc = v.toLowerCase().trim();
            if ("true".equals(lc)) {
                return Boolean.TRUE;
            } else if ("false".equals(lc)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    public static int toInt(Object value) {
        Objects.requireNonNull(value, "value is null");
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            throw new IllegalArgumentException("unknown type " + value.getClass());
        }
    }

    /**
     * Return the value of a field (even private ones) using reflection.
     */
    static Field getSimpleField(String fieldName, Class<?> clazz) {
        Class<?> currentClass = clazz;
        try {
            do {
                try {
                    Field field = currentClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                } catch (NoSuchFieldException ex) {
                    currentClass = currentClass.getSuperclass();
                }
            } while (currentClass != Class.class);
        } catch (Exception e) {
            System.err.println("krasa-jaxb-tools - Field '" + fieldName +
                    "' not found in class " + clazz);
        }
        return null;
    }

    /**
     * Return the value of a field (even private ones) using reflection.
     *
     * @param path sequence of 1 or more field name separated by '.'. i.e.: 'office.address.street'
     * @param obj the object to evaluate
     * @return the value of the specified field
     */
    public static Object _getFieldValue(String path, Object obj) {
        try {
            int idx = path.indexOf(".");
            if (idx != -1) {
                String field = path.substring(0, idx);
                Field declaredField = obj.getClass().getDeclaredField(field);
                declaredField.setAccessible(true);
                Object result = declaredField.get(obj);

                String nextField = path.substring(idx + 1);
                return getFieldValue(nextField, result);
            } else {
                Field simpleField = getSimpleField(path, obj.getClass());
                simpleField.setAccessible(true);
                return simpleField.get(obj);
            }
        } catch (Exception e) {
            String className = null;
            if (obj != null && obj.getClass() != null) {
                className = obj.getClass().getName();
            }
            System.err.println("krasa-jaxb-tools - Field " + path + " not found on " + className);
        }
        return null;
    }

    /**
     * Return the value of a field (even private ones) using reflection.
     *
     * @param path sequence of 1 or more field name separated by '.'. i.e.: 'office.address.street'
     * @param obj the object to evaluate
     * @return the value of the specified field
     */
    public static Object getFieldValue(String path, Object obj) {
        String[] fieldNames = path.split("\\.");
        Object actualObject = obj;
        try {
            for (String fieldName : fieldNames) {
                Objects.requireNonNull(actualObject, "null object on field '" + fieldName +
                        "' of " + obj.getClass().getCanonicalName() + "->" + path);
                Field field = getSimpleField(fieldName, actualObject.getClass());
                if (field == null) {
                    throw new IllegalArgumentException("cannot find field '" + fieldName + "' in " +
                            obj.getClass().getCanonicalName() + "->" + path);
                }
                field.setAccessible(true);
                actualObject = field.get(actualObject);
            }
            return actualObject;
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    private static final String LONG_MIN = Long.toString(Long.MIN_VALUE);
    private static final String LONG_MAX = Long.toString(Long.MAX_VALUE);
    private static final String INTEGER_MIN = Integer.toString(Integer.MIN_VALUE);
    private static final String INTEGER_MAX = Integer.toString(Integer.MAX_VALUE);

    public static boolean isMin(String value) {
        return LONG_MIN.equals(value) || INTEGER_MIN.equals(value);
    }

    public static boolean isMax(String value) {
        return LONG_MAX.equals(value) || INTEGER_MAX.equals(value);
    }

    public static final Set<String> NUMBERS = Arrays.stream( new Class<?>[] {
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

    public static boolean isNumberField(JFieldVar field) {
        return isFieldTypeNameNumber(field.type().name()) ||
                isFieldTypeFullNameNumber(field.type().fullName());
    }

    static boolean isFieldTypeNameNumber(String fieldTypeName) {
        if (NUMBERS.contains(fieldTypeName.toUpperCase())) {
            return true;
        }
        return false;
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

    public static boolean isCustomType(JFieldVar var) {
        if (var == null) {
            return false;
        }
        return "JDirectClass".equals(var.type().getClass().getSimpleName());
    }
}
