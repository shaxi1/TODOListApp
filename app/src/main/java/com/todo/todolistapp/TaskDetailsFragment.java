package com.todo.todolistapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TaskDetailsFragment extends Fragment {
    DatabaseHelper databaseHelper;


    public TaskDetailsFragment() {
        // Required empty public constructor
    }

    public static TaskDetailsFragment newInstance() {
        return new TaskDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task_details, container, false);

        if (getArguments() != null) {
            int task_id = getArguments().getInt("task_id");
            Task task = databaseHelper.getTask(task_id);

            fillTaskDetails(view, task);
        } else {
            TextView creationDate = view.findViewById(R.id.text_task_creation_date);
            creationDate.setVisibility(View.INVISIBLE);
            Button deleteButton = view.findViewById(R.id.btn_delete_task);
            deleteButton.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private void fillTaskDetails(View view, Task task) {
        TextView taskTitle = view.findViewById(R.id.task_title);
        taskTitle.setText(task.title);

        TextView taskDescription = view.findViewById(R.id.edit_task_description);
        taskDescription.setText(task.description);

        TextView taskCreationDate = view.findViewById(R.id.text_task_creation_date);
        taskCreationDate.setText(String.valueOf(task.creationDate));

        TextView taskDueDate = view.findViewById(R.id.edit_task_due_date);
        taskDueDate.setText(String.valueOf(task.dueDate));

        TextView taskCategory = view.findViewById(R.id.edit_task_category);
        taskCategory.setText(task.category);

        CheckBox taskCompleted = view.findViewById(R.id.checkbox_task_completed);
        taskCompleted.setChecked(task.isCompleted);

        CheckBox notificationEnabled = view.findViewById(R.id.checkbox_notifications_enabled);
        notificationEnabled.setChecked(task.notificationsEnabled);
    }
}