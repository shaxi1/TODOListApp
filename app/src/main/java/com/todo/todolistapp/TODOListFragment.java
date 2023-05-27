package com.todo.todolistapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TODOListFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;
    public TODOListFragment() {
        // Required empty public constructor
    }

    private TaskAdapter.OnCardClickListener onCardClickListener;

    public static TODOListFragment newInstance() {
        return new TODOListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(getActivity());
        taskAdapter.setOnCardClickListener(onCardClickListener);
        recyclerView.setAdapter(taskAdapter);

        configureFloatingButtonClick(view);
        configureSpinner(view);

        return view;
    }

    private void configureSpinner(View view) {
        Spinner spinner = view.findViewById(R.id.category_spinner);
        if (spinner == null) {
            return;
        }

        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.addAll(databaseHelper.getCategories());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taskAdapter.setFilterCategory(categories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
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

    public void setOnCardClickListener(TaskAdapter.OnCardClickListener listener) {
        onCardClickListener = listener;
    }

}