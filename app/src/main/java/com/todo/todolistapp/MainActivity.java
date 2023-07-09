package com.todo.todolistapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnCardClickListener {
    static volatile boolean dontRecreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = {
                "android.permission.POST_NOTIFICATIONS",
                "android.permission.SCHEDULE_EXACT_ALARM",
                "android.permission.VIBRATE",
                "android.permission.MANAGE_EXTERNAL_STORAGE"
        };

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), 888);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("notificationId")) {
            int taskId = intent.getIntExtra("notificationId", -1);

            TaskDetailsFragment taskDetailsFragment = TaskDetailsFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("task_id", taskId);
            taskDetailsFragment.setArguments(bundle);

            if (isTablet(this)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.tablet_details, taskDetailsFragment)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_list, taskDetailsFragment)
                        .commit();
            }
        } else {
            TODOListFragment todoListFragment = TODOListFragment.newInstance();
            todoListFragment.setOnCardClickListener(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list, todoListFragment).commit();
        }

    }

    public static boolean isTablet(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        int screenLayout = configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public void onCardClick(int task_id) {
        TaskDetailsFragment taskDetailsFragment = TaskDetailsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("task_id", task_id);
        taskDetailsFragment.setArguments(bundle);

        if (isTablet(this)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tablet_details, taskDetailsFragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_list, taskDetailsFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null) {
            setIntent(null);
            return;
        }
        if (dontRecreate) {
            dontRecreate = false;
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
                if (isTablet(this)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.tablet_details, taskDetailsFragment).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list, taskDetailsFragment).commit();
                }
            }
        }
    }

}