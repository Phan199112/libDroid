package com.a324.mbaaslibrary.util;

/**
 * Created by kevinfischer on 7/16/18.
 */

public enum InequalityEnum {
    GT(">"),LT("<"),GE(">="),LE("<="),EQ("=");

    private String inequality;

    InequalityEnum(String inequality) {
        this.inequality = inequality;
    }

    public String valueOf() {
        return inequality;
    }
}
