package com.todo.todolistapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnCardClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("notificationId")) {
            int taskId = intent.getIntExtra("notificationId", -1);

            TaskDetailsFragment taskDetailsFragment = TaskDetailsFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("task_id", taskId);
            taskDetailsFragment.setArguments(bundle);
            //setIntent(null);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_list, taskDetailsFragment)
                    .commit();
        } else {
            TODOListFragment todoListFragment = TODOListFragment.newInstance();
            todoListFragment.setOnCardClickListener(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list, todoListFragment).commit();
        }

    }

    @Override
    public void onCardClick(int task_id) {
        TaskDetailsFragment taskDetailsFragment = TaskDetailsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("task_id", task_id);
        taskDetailsFragment.setArguments(bundle);

        // TODO: add logic for tablets
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list, taskDetailsFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null) {
            setIntent(null);
            return;
        }
        TODOListFragment todoListFragment = TODOListFragment.newInstance();
        todoListFragment.setOnCardClickListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list, todoListFragment).commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_list);

        if (currentFragment instanceof TaskDetailsFragment) {
            TODOListFragment todoListFragment = TODOListFragment.newInstance();
            todoListFragment.setOnCardClickListener(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list, todoListFragment).commit();
        } else {
            super.onBackPressed();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list, taskDetailsFragment).commit();
            }
        }
    }

}