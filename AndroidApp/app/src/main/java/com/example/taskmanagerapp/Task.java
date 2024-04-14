package com.example.taskmanagerapp;

public class Task {
    private long id;
    private String title;
    private String description;
    private long dueDate;

    public Task(long id, String title, String description, long dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getDueDate() { return dueDate; }
}
