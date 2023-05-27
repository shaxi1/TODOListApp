package com.todo.todolistapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TODOListFragment extends Fragment {
    public TODOListFragment() {
        // Required empty public constructor
    }

    public static TODOListFragment newInstance() {
        return new TODOListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        configureFloatingButtonClick(view);

        return view;
    }

    private void configureFloatingButtonClick(View view) {
        FloatingActionButton floatingActionButton = view.findViewById(R.id.button_add_task);
        if (floatingActionButton == null) {
            return;
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTaskActivity.class);
                startActivity(intent);
            }
        });
    }


}