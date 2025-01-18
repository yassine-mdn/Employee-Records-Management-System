package com.erms.model.enums;

public enum Role {
    NO_ROLE("No Role"),
    HR("Human Resources"),
    MANAGER("Manager"),
    ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName; // Use display name for JComboBox
    }
}
