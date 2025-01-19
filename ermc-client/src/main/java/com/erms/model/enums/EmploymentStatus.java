package com.erms.model.enums;

public enum EmploymentStatus  {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    TERMINATED("Terminated"),
    SUSPENDED("Suspended"),
    PROBATION("Probation"),
    RETIRED("Retired");

    private final String displayName;

    EmploymentStatus(String displayName) {
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
