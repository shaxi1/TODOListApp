package com.todo.todolistapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        TaskDetailsFragment taskDetailsFragment = (TaskDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.task_details);
        if (taskDetailsFragment == null) {
            taskDetailsFragment = TaskDetailsFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.task_details, taskDetailsFragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FileHelper.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                FileHelper fileHelper = new FileHelper(this);
                String filePath = fileHelper.handleFileSelection(resultCode, data);

                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                databaseHelper.addFileToTask(filePath, TaskDetailsFragment.task_id);
                TaskDetailsFragment taskDetailsFragment = TaskDetailsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("task_id", TaskDetailsFragment.task_id);
                taskDetailsFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.task_details, taskDetailsFragment).commit();
            }
        }
    }
}