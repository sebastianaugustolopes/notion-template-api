package com.example.notion_template_api.domain.task;

public enum TaskStatus {
    TODO("To Do"),
    PLANNING("Planning"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCompleted() {
        return this == DONE;
    }
}
