package com.todo.todolistapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TODOListFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    public TaskAdapter taskAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
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
        configureNotifyBefore(view);
        configureHideCompleted(view);

        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query, view);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText, view);
                return true;
            }
        });


        return view;
    }

    private void configureHideCompleted(View view) {
        CheckBox checkBox = view.findViewById(R.id.checkbox_hide_completed);
        if (checkBox == null)
            return;

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int notifyBefore = databaseHelper.getNotificationTime();
                Spinner spinner = view.findViewById(R.id.category_spinner);
                String filterCategory = spinner.getSelectedItem().toString();
                SearchView searchView = view.findViewById(R.id.search_view);
                String query = searchView.getQuery().toString();

                List<Task> tasks;
                if (isChecked)
                    tasks = databaseHelper.getTasksNoFinishedFiltered(filterCategory, query);
                else
                    tasks = databaseHelper.getTasksNameContaining(query);

                databaseHelper.updateSettings(isChecked, filterCategory, notifyBefore);
                taskAdapter.setTaskList(tasks);
            }
        });
    }

    private void configureNotifyBefore(View view) {
        Button button = view.findViewById(R.id.button_set_notify_before);
        if (button == null)
            return;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = view.findViewById(R.id.edit_text_notify_before);
                if (editText == null)
                    return;

                String text;
                int notifyBefore;
                try {
                    text = editText.getText().toString();
                    notifyBefore = Integer.parseInt(text);
                } catch(NumberFormatException e) {
                    editText.setError("Invalid value");
                    return;
                }

                if (notifyBefore < 0 || notifyBefore > 2000) {
                    editText.setError("Invalid value");
                } else {
                    CheckBox checkBox = view.findViewById(R.id.checkbox_hide_completed);
                    boolean hideCompleted = checkBox.isChecked();
                    Spinner spinner = view.findViewById(R.id.category_spinner);
                    String filterCategory = spinner.getSelectedItem().toString();
                    databaseHelper.updateSettings(hideCompleted, filterCategory, notifyBefore);
                    Toast.makeText(getContext(), "Notify before set to " + notifyBefore + " minutes", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void performSearch(String query, View view) {
        CheckBox checkBox = view.findViewById(R.id.checkbox_hide_completed);
        boolean hideCompleted = checkBox.isChecked();
        Spinner spinner = view.findViewById(R.id.category_spinner);
        String filterCategory = spinner.getSelectedItem().toString();

        if (hideCompleted)
            taskAdapter.setTaskList(databaseHelper.getTasksNoFinishedFiltered(filterCategory, query));
        else
            taskAdapter.setTaskList(databaseHelper.getTasksNameContaining(query));
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