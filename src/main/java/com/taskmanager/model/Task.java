package com.taskmanager.model;

public class Task {
    private int id;
    private String title;
    private String priority;
    private String status;
    private String dueDate;
    private boolean completed;

    public Task(int id, String title, String priority, String status, String dueDate) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status != null ? status : "To Do";
        this.dueDate = dueDate != null ? dueDate : "";
        this.completed = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}