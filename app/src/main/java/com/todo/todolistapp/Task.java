package com.todo.todolistapp;

import java.sql.Date;
import java.util.List;

public class Task {
    public String title;
    public String description;
    public Date creationDate;
    public Date dueDate;
    public boolean isCompleted;
    public boolean notificationsEnabled;
    public String Category;
    public List<String> attachmentsPath;
}
