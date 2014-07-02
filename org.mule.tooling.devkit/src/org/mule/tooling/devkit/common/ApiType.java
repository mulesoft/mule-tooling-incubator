package org.mule.tooling.devkit.common;

public enum ApiType {
    GENERIC("Generic"), SOAP("Soap"), REST("Rest");

    ApiType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    private String label;

    public static ApiType fromLabel(String text) {
        for (ApiType item : values()) {
            if (item.label().equals(text)) {
                return item;
            }
        }
        return null;
    }
}
