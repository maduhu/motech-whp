package org.motechproject.whp.common.domain;

public enum Gender {

    M("Male"), F("Female"), O("Other");

    private String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
