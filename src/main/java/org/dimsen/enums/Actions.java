package org.dimsen.enums;

import lombok.Getter;

@Getter
public enum Actions {
    CREATE("CREATE", "Resource creation action"),
    UPDATE("UPDATE", "Resource update action"),
    DELETE("DELETE", "Resource deletion action"),
    RESTORE("RESTORE", "Resource restoration action"),
    SOFT_DELETE("SOFT_DELETE", "Resource soft deletion action");

    private final String action;
    private final String description;

    Actions(String action, String description) {
        this.action = action;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.action;
    }
}
