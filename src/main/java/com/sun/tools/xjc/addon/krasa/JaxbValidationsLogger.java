package com.sun.tools.xjc.addon.krasa;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsLogger {
    private final boolean verbose;

    public JaxbValidationsLogger(boolean verbose) {
        this.verbose = verbose;
    }

    void log(String log) {
        if (verbose) {
            System.out.println(log);
        }
    }

}
