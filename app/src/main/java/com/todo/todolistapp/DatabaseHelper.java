package com.todo.todolistapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

// connect to the sqlite, crud tasks, crud settings
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todoapp.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tasks table
        db.execSQL("CREATE TABLE tasks (task_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, creation_date INTEGER, due_date INTEGER, is_completed INTEGER, notifications_enabled INTEGER, category TEXT)");
        // create tasks_attachments table
        db.execSQL("CREATE TABLE tasks_attachments (attachment_id INTEGER PRIMARY KEY AUTOINCREMENT, task_id INTEGER, attachment_path TEXT, FOREIGN KEY(task_id) REFERENCES tasks(task_id))");

        // create settings table
        db.execSQL("CREATE TABLE settings (settings_id INTEGER PRIMARY KEY AUTOINCREMENT, hide_finished_tasks INTEGER, notify_before INTEGER)");
        /* notify_before stored in minutes | there will be always only one settings record */
        db.execSQL("INSERT OR REPLACE INTO settings (settings_id, hide_finished_tasks, notify_before) VALUES (1, 0, 60)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE tasks_backup (task_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, creation_date INTEGER, due_date INTEGER, is_completed INTEGER, notifications_enabled INTEGER, category TEXT)");
        db.execSQL("INSERT INTO tasks_backup SELECT * FROM tasks");
        db.execSQL("DROP TABLE tasks");
        db.execSQL("ALTER TABLE tasks_backup RENAME TO tasks");

        db.execSQL("CREATE TABLE tasks_attachments_backup (attachment_id INTEGER PRIMARY KEY AUTOINCREMENT, task_id INTEGER, attachment_path TEXT)");
        db.execSQL("INSERT INTO tasks_attachments_backup SELECT * FROM tasks_attachments");
        db.execSQL("DROP TABLE tasks_attachments");
        db.execSQL("ALTER TABLE tasks_attachments_backup RENAME TO tasks_attachments");

        onCreate(db);
    }

    public int addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", task.title);
        values.put("description", task.description);
        values.put("creation_date", task.creationDate.getTime());
        values.put("due_date", task.dueDate.getTime());
        values.put("is_completed", task.isCompleted);
        values.put("notifications_enabled", task.notificationsEnabled);
        values.put("category", task.category);

        return (int) db.insert("tasks", null, values);
    }


    public void addFileToTask(String path, int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO tasks_attachments (task_id, attachment_path) VALUES (" + taskId + ", '" + path + "')");
    }

    public void removeFileFromTask(String path, int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tasks_attachments WHERE task_id = " + taskId + " AND attachment_path = '" + path + "'");
    }

    public void removeTaskAttachments(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tasks_attachments WHERE task_id = " + taskId);
    }

    public void updateTask(Task task, int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE tasks SET title = '" + task.title + "', description = '" + task.description + "', creation_date = " + task.creationDate.getTime() + ", due_date = " + task.dueDate.getTime() + ", is_completed = " + task.isCompleted + ", notifications_enabled = " + task.notificationsEnabled + ", category = '" + task.category + "' WHERE task_id = " + taskId);
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tasks WHERE task_id = " + taskId);
    }

    public void deleteAllTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tasks_attachments");
        db.execSQL("DELETE FROM tasks");
    }

    public void updateSettings(boolean hideFinishedTasks, String filterCategory, int notifyBefore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hide_finished_tasks", hideFinishedTasks ? 1 : 0);
        values.put("notify_before", notifyBefore);
        db.update("settings", values, "settings_id = 1", null);
    }

    // closest to the deadline
    @SuppressLint("Range")
    public List<Task> getTasksSortedByDueDate() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = { "task_id", "title", "description", "creation_date", "due_date", "is_completed", "notifications_enabled", "category" };
        String orderBy = "due_date ASC"; // Sorting by ascending due date

        Cursor cursor = db.query("tasks", columns, null, null, null, null, orderBy);

        while (cursor.moveToNext()) {
            Task task = new Task();
            task.taskId = cursor.getInt(cursor.getColumnIndex("task_id"));
            task.title = cursor.getString(cursor.getColumnIndex("title"));
            task.description = cursor.getString(cursor.getColumnIndex("description"));

            long creationTime = cursor.getLong(cursor.getColumnIndex("creation_date"));
            task.creationDate = new Date(creationTime);

            long dueTime = cursor.getLong(cursor.getColumnIndex("due_date"));
            task.dueDate = new Date(dueTime);

            int isCompletedValue = cursor.getInt(cursor.getColumnIndex("is_completed"));
            task.isCompleted = isCompletedValue == 1;

            int notificationsEnabledValue = cursor.getInt(cursor.getColumnIndex("notifications_enabled"));
            task.notificationsEnabled = notificationsEnabledValue == 1;

            task.category = cursor.getString(cursor.getColumnIndex("category"));

            taskList.add(task);
        }

        cursor.close();
        return taskList;
    }

    @SuppressLint("Range")
    public List<Attachment> getTaskAttachments(int taskId) {
        List<Attachment> attachmentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = { "attachment_id", "attachment_path" };
        String selection = "task_id = ?";
        String[] selectionArgs = { String.valueOf(taskId) };

        Cursor cursor = db.query("tasks_attachments", columns, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            Attachment attachment = new Attachment();
            attachment.attachment_id = cursor.getInt(cursor.getColumnIndex("attachment_id"));
            attachment.path = cursor.getString(cursor.getColumnIndex("attachment_path"));

            attachmentList.add(attachment);
        }

        cursor.close();
        return attachmentList;
    }

    @SuppressLint("Range")
    public Task getTask(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = { "title", "description", "creation_date", "due_date", "is_completed", "notifications_enabled", "category" };
        String selection = "task_id = ?";
        String[] selectionArgs = { String.valueOf(taskId) };

        Cursor cursor = db.query("tasks", columns, selection, selectionArgs, null, null, null);

        Task task = new Task();
        if (cursor.moveToFirst()) {
            task.taskId = taskId;
            task.title = cursor.getString(cursor.getColumnIndex("title"));
            task.description = cursor.getString(cursor.getColumnIndex("description"));

            long creationTime = cursor.getLong(cursor.getColumnIndex("creation_date"));
            task.creationDate = new Date(creationTime);

            long dueTime = cursor.getLong(cursor.getColumnIndex("due_date"));
            task.dueDate = new Date(dueTime);

            int isCompletedValue = cursor.getInt(cursor.getColumnIndex("is_completed"));
            task.isCompleted = isCompletedValue == 1;

            int notificationsEnabledValue = cursor.getInt(cursor.getColumnIndex("notifications_enabled"));
            task.notificationsEnabled = notificationsEnabledValue == 1;

            task.category = cursor.getString(cursor.getColumnIndex("category"));
        }

        cursor.close();
        if (task.taskId == 0) {
            return null;
        }

        return task;
    }

    public int getNotificationTime() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT notify_before FROM settings WHERE settings_id = 1", null);
        cursor.moveToFirst();
        @SuppressLint("Range") int notificationTime = cursor.getInt(cursor.getColumnIndex("notify_before"));
        cursor.close();

        return notificationTime;
    }

    public boolean hideFinishedTasks() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT hide_finished_tasks FROM settings WHERE settings_id = 1", null);
        cursor.moveToFirst();
        @SuppressLint("Range") int hideFinishedTasks = cursor.getInt(cursor.getColumnIndex("hide_finished_tasks"));
        cursor.close();

        return hideFinishedTasks == 1;
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT category FROM tasks", null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
            categories.add(category);
        }

        cursor.close();
        return categories;
    }

    @SuppressLint("Range")
    public List<Task> getTasksWithCategory(String category) {
        if (category.equals("All")) {
            return getTasksSortedByDueDate();
        }

        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = { "task_id", "title", "description", "creation_date", "due_date", "is_completed", "notifications_enabled", "category" };
        String selection = "category = ?";
        String[] selectionArgs = { category };
        String orderBy = "due_date ASC"; // Sorting by ascending due date

        @SuppressLint("Recycle") Cursor cursor = db.query("tasks", columns, selection, selectionArgs, null, null, orderBy);
        while (cursor.moveToNext()) {
            Task task = new Task();
            task.taskId = cursor.getInt(cursor.getColumnIndex("task_id"));
            task.title = cursor.getString(cursor.getColumnIndex("title"));
            task.description = cursor.getString(cursor.getColumnIndex("description"));

            long creationTime = cursor.getLong(cursor.getColumnIndex("creation_date"));
            task.creationDate = new Date(creationTime);

            long dueTime = cursor.getLong(cursor.getColumnIndex("due_date"));
            task.dueDate = new Date(dueTime);

            int isCompletedValue = cursor.getInt(cursor.getColumnIndex("is_completed"));
            task.isCompleted = isCompletedValue == 1;

            int notificationsEnabledValue = cursor.getInt(cursor.getColumnIndex("notifications_enabled"));
            task.notificationsEnabled = notificationsEnabledValue == 1;

            task.category = cursor.getString(cursor.getColumnIndex("category"));

            taskList.add(task);
        }

        return taskList;
    }

    public void setTaskToFinished(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_completed", 1);
        db.update("tasks", values, "task_id = " + taskId, null);
    }

    @SuppressLint("Range")
    public List<Task> getTasksNoFinished() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = { "task_id", "title", "description", "creation_date", "due_date", "is_completed", "notifications_enabled", "category" };
        String selection = "is_completed = ?";
        String[] selectionArgs = { "0" };
        String orderBy = "due_date ASC"; // Sorting by ascending due date

        Cursor cursor = db.query("tasks", columns, selection, selectionArgs, null, null, orderBy);

        while (cursor.moveToNext()) {
            Task task = new Task();
            task.taskId = cursor.getInt(cursor.getColumnIndex("task_id"));
            task.title = cursor.getString(cursor.getColumnIndex("title"));
            task.description = cursor.getString(cursor.getColumnIndex("description"));

            long creationTime = cursor.getLong(cursor.getColumnIndex("creation_date"));
            task.creationDate = new Date(creationTime);

            long dueTime = cursor.getLong(cursor.getColumnIndex("due_date"));
            task.dueDate = new Date(dueTime);

            int isCompletedValue = cursor.getInt(cursor.getColumnIndex("is_completed"));
            task.isCompleted = isCompletedValue == 1;

            int notificationsEnabledValue = cursor.getInt(cursor.getColumnIndex("notifications_enabled"));
            task.notificationsEnabled = notificationsEnabledValue == 1;

            task.category = cursor.getString(cursor.getColumnIndex("category"));

            taskList.add(task);
        }

        cursor.close();
        return taskList;
    }

}
