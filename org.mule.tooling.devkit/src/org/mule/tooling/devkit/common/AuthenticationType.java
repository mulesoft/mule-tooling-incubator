package org.mule.tooling.devkit.common;

public enum AuthenticationType {
    NONE("None"), CONNECTION_MANAGEMENT("Custom"),HTTP_BASIC("HTTP Basic"), OAUTH_V2("OAuth V2");

    AuthenticationType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    private String label;

    public static AuthenticationType fromLabel(String text) {
        for (AuthenticationType item : values()) {
            if (item.label().equals(text)) {
                return item;
            }
        }
        return NONE;
    }
}
