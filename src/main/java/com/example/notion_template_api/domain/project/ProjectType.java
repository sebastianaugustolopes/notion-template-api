package com.example.notion_template_api.domain.project;

public enum ProjectType {
    WORK("Work"),
    SCHOOL("School"),
    PERSONAL("Personal"),
    FINANCIAL("Financial"),
    FAMILY("Family");

    private final String displayName;

    ProjectType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
