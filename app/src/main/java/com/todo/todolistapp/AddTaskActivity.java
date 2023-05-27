package com.todo.todolistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        TaskDetailsFragment taskDetailsFragment = TaskDetailsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.task_details, taskDetailsFragment).commit();
    }
}