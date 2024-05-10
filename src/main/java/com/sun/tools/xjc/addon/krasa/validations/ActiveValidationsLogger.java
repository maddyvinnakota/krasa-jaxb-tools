package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class ActiveValidationsLogger implements ValidationsLogger {
    private final String className;
    private final String propertyName;

    public ActiveValidationsLogger(String className, String propertyName) {
        this.className = className;
        this.propertyName = propertyName;
    }

    @Override
    public void addAnnotation(String annotationName, Map<String, String> parameterMap) {
        String params = parameterMap.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", "));
        if (!params.isEmpty()) {
            params = "(" + params + ")";
        }
        ValidationsLogger.log("adding @" + annotationName + params +
                " to " + className + "." + propertyName);
    }

}
