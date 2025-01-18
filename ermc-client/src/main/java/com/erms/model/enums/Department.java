package com.erms.model.enums;


// Used ENUM since it's just a mock application for real world use case I'll switch to a separate table
public enum Department {
    HR("Human Resources"),
    IT("Information Technology"),
    SALES("Sales"),
    MARKETING("Marketing"),
    FINANCE("Finance"),
    OPERATIONS("Operations"),
    RESEARCH("Research and Development"),
    LEGAL("Legal");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
