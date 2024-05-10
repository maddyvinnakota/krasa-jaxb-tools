package com.sun.tools.xjc.addon.krasa;

import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsLogger {
    private final boolean verbose;
    private final String className;
    private final String propertyName;

    public JaxbValidationsLogger(boolean verbose, String className, String propertyName) {
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

    static void log(String message) {
        System.out.println("[" + JaxbValidationsPlugin.class.getSimpleName() + "] " + message);
    }

}
