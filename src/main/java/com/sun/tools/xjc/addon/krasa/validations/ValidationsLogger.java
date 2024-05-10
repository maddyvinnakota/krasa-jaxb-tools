package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidationsLogger {
    private final boolean verbose;
    private final String className;
    private final String propertyName;

    public ValidationsLogger(boolean verbose, String className, String propertyName) {
        this.verbose = verbose;
        this.className = className;
        this.propertyName = propertyName;
    }

    void addAnnotation(String annotationName, Map<String, String> parameterMap) {
        if (verbose) {
            String params = parameterMap.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining(", "));
            if (!params.isEmpty()) {
                params = "(" + params + ")";
            }
            log("adding @" + annotationName + params + " to " + className + "." + propertyName);
        }
    }

    public static void log(String message) {
        System.out.println("[" + JaxbValidationsPlugin.class.getSimpleName() + "] " + message);
    }

}
