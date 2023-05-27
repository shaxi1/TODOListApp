package com.todo.todolistapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TaskDetailsFragment extends Fragment {


    public TaskDetailsFragment() {
        // Required empty public constructor
    }

    public static TaskDetailsFragment newInstance() {
        return new TaskDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            int task_id = getArguments().getInt("task_id");
            // TODO: get task from database and fill the components
        } else {
            // blank components
        }

        return inflater.inflate(R.layout.fragment_task_details, container, false);
    }
}