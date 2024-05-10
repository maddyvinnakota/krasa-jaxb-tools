package com.sun.tools.xjc.addon.krasa.validations;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
enum CustomMessageType {
    Fieldname("fieldname"), Classname("classname");

    private String value;

    private CustomMessageType(String value) {
        this.value = value;
    }

    public boolean equalsIgnoreCase(String v) {
        return value.equalsIgnoreCase(v);
    }

    public String value() {
        return value;
    }
}
