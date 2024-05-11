package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
interface ValidationsLogger {
    static final String PREFIX = "[" + JaxbValidationsPlugin.PLUGIN_NAME + "] ";

    void addAnnotation(String annotationName, Map<String, String> parameterMap);

}
