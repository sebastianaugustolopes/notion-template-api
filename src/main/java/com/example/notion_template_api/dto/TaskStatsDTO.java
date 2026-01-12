package com.example.notion_template_api.dto;

public record TaskStatsDTO(
    int totalTasks,
    int todoCount,
    int planningCount,
    int inProgressCount,
    int doneCount,
    int missedCount
) {
    public double getTodoPercentage() {
        return totalTasks > 0 ? (double) todoCount / totalTasks * 100 : 0;
    }

    public double getPlanningPercentage() {
        return totalTasks > 0 ? (double) planningCount / totalTasks * 100 : 0;
    }

    public double getInProgressPercentage() {
        return totalTasks > 0 ? (double) inProgressCount / totalTasks * 100 : 0;
    }

    public double getDonePercentage() {
        return totalTasks > 0 ? (double) doneCount / totalTasks * 100 : 0;
    }

    public double getMissedPercentage() {
        return totalTasks > 0 ? (double) missedCount / totalTasks * 100 : 0;
    }
}
