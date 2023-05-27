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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list, todoListFragment).commit();


    }

    @Override
    public void onCardClick(int task_id) {
        // create task details fragment with task_id as argument
    }
}