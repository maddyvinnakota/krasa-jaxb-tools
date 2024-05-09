package com.sun.tools.xjc.addon.krasa;

import com.sun.codemodel.JFieldVar;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * @author Vojtech Krasa
 */
class Utils {

    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }


    public static String setBoolean(String value, Consumer<Boolean> setter) {
        if (value == null || "".equals(value.trim())) {
            setter.accept(true);
            return null;
        }
        Boolean bool = toBoolean(value);
        if (bool == null) {
            return "argument not valid, must be 'true' or 'false'";
        }
        setter.accept(bool);
        return null;
    }

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

    public static boolean isCustomType(JFieldVar var) {
        if (var == null) {
            return false;
        }
        return "JDirectClass".equals(var.type().getClass().getSimpleName());
    }
}
