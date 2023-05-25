package com.todo.todolistapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

        // create settings table
        db.execSQL("CREATE TABLE settings (settings_id INTEGER PRIMARY KEY AUTOINCREMENT, hide_finished_tasks INTEGER, filter_category TEXT, notify_before INTEGER)");

        /* set default values */

        db.execSQL("INSERT INTO settings (name, value) VALUES ('hide_finished_tasks', 'false')");
        /* "All" means don't filter at all */
        db.execSQL("INSERT INTO settings (name, value) VALUES ('filter_category', 'All')");
        /* notification X minutes before due time */
        db.execSQL("INSERT INTO settings (name, value) VALUES ('notify_before', '60')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE tasks_backup (task_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, creation_date INTEGER, due_date INTEGER, is_completed INTEGER, notifications_enabled INTEGER, category TEXT)");
        db.execSQL("INSERT INTO tasks_backup SELECT * FROM tasks");
        db.execSQL("DROP TABLE tasks");
        db.execSQL("ALTER TABLE tasks_backup RENAME TO tasks");
        
        db.execSQL("CREATE TABLE settings_backup (settings_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)");
        db.execSQL("INSERT INTO settings_backup SELECT * FROM settings");
        db.execSQL("DROP TABLE settings");
        db.execSQL("ALTER TABLE settings_backup RENAME TO settings");
    }
}
