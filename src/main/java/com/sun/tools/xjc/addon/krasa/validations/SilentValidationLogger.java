package com.sun.tools.xjc.addon.krasa.validations;

import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SilentValidationLogger implements ValidationsLogger {

    public static final SilentValidationLogger INSTANCE = new SilentValidationLogger();

    private SilentValidationLogger() {}

    @Override
    public void addAnnotation(String annotationName, Map<String, String> parameterMap) {
        // do nothing
    }

}
