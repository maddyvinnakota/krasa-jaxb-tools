package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
interface ValidationsLogger {

    void addAnnotation(String annotationName, Map<String, String> parameterMap);

    public static void log(String message) {
        System.out.println("[" + JaxbValidationsPlugin.class.getSimpleName() + "] " + message);
    }

}
