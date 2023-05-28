package com.todo.todolistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnCardClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TODOListFragment todoListFragment = TODOListFragment.newInstance();
        todoListFragment.setOnCardClickListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list, todoListFragment).commit();


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
}