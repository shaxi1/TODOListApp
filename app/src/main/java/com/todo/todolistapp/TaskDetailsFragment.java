package com.todo.todolistapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class TaskDetailsFragment extends Fragment {
    DatabaseHelper databaseHelper;
    public RecyclerView recyclerView;
    public static volatile int task_id;
    public static AttachmentAdapter attachmentAdapter;


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

        task_id = -1;
        if (getArguments() != null) {
            task_id = getArguments().getInt("task_id");
            Task task = databaseHelper.getTask(task_id);

            fillTaskDetails(view, task);
        } else {
            TextView creationDate = view.findViewById(R.id.text_task_creation_date);
            creationDate.setVisibility(View.INVISIBLE);
            Button deleteButton = view.findViewById(R.id.btn_delete_task);
            deleteButton.setVisibility(View.INVISIBLE);
        }

        // due date picker
        EditText dueDate = view.findViewById(R.id.edit_task_due_date);
        DatePickerDialog datePickerDialog = setupDatePicker(view);
        dueDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        configureDeleteTaskButton(view, task_id);
        configureSaveButton(view, task_id);

        recyclerView = view.findViewById(R.id.recycler_attachments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        attachmentAdapter = new AttachmentAdapter(getActivity(), task_id);
        recyclerView.setAdapter(attachmentAdapter);
        configureAttachButton(view, task_id);

        return view;
    }

    private void configureAttachButton(View view, int task_id) {
        Button attachButton = view.findViewById(R.id.attach_file_button);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allFieldsFilled() || TaskDetailsFragment.task_id == -1) {
                    Toast toast = Toast.makeText(getActivity(), "Save the task before attaching files", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                FileHelper fileHelper = new FileHelper(v.getContext());
                fileHelper.chooseFile(requireActivity());
            }
        });
    }

    private boolean allFieldsFilled() {
        EditText title = requireActivity().findViewById(R.id.edit_task_title);
        EditText description = requireActivity().findViewById(R.id.edit_task_description);
        EditText dueDate = requireActivity().findViewById(R.id.edit_task_due_date);
        EditText category = requireActivity().findViewById(R.id.edit_task_category);

        return !title.getText().toString().isEmpty() && !description.getText().toString().isEmpty()
                && !dueDate.getText().toString().isEmpty() && !category.getText().toString().isEmpty() && !dueDate.getText().toString().isEmpty();
    }

    private void configureSaveButton(View view, int task_id) {
        Button saveButton = view.findViewById(R.id.btn_save_task);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = getTaskFromView(view, task_id);
                if (task != null) {
                    if (databaseHelper.getTask(task_id) != null) {
                        databaseHelper.updateTask(task, task_id);
                        Toast toast = Toast.makeText(getActivity(), "Task updated", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        TaskDetailsFragment.task_id = databaseHelper.addTask(task);
                        Toast toast = Toast.makeText(getActivity(), "Task saved", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    //requireActivity().onBackPressed();
                }
            }
        });
    }

    private Task getTaskFromView(View view, int task_id) {
        EditText title = view.findViewById(R.id.edit_task_title);
        EditText description = view.findViewById(R.id.edit_task_description);
        EditText dueDate = view.findViewById(R.id.edit_task_due_date);
        EditText category = view.findViewById(R.id.edit_task_category);
        CheckBox isDone = view.findViewById(R.id.checkbox_task_completed);

        // check if every field is filled
        if (title.getText().toString().isEmpty() || description.getText().toString().isEmpty()
                || dueDate.getText().toString().isEmpty() || category.getText().toString().isEmpty() || dueDate.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "Please fill all fields first", Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }

        Task task = new Task();
        task.title = title.getText().toString();
        task.description = description.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String dateString = dueDate.getText().toString();
        java.util.Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert parsedDate != null;
        task.dueDate = new Date(parsedDate.getTime());

        task.isCompleted = isDone.isChecked();
        task.taskId = task_id;
        task.creationDate = new Date(System.currentTimeMillis());
        task.category = category.getText().toString();
        // TODO: attachments

        return task;
    }

    private void configureDeleteTaskButton(View view, int task_id) {
        Button deleteButton = view.findViewById(R.id.btn_delete_task);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task_id != -1) {
                    databaseHelper.removeTaskAttachments(task_id);
                    databaseHelper.deleteTask(task_id);
                    Toast toast = Toast.makeText(getActivity(), "Task deleted", Toast.LENGTH_SHORT);
                    toast.show();
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    @NonNull
    private DatePickerDialog setupDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        final EditText editTaskDueDate = view.findViewById(R.id.edit_task_due_date);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the selected date
                        calendar.set(year, month, dayOfMonth);

                        // Create a TimePickerDialog for time selection
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                requireActivity(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Update the selected time
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);

                                        // Format the selected date and time as "dd-MM-yyyy HH:mm"
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                        String formattedDateTime = dateFormat.format(calendar.getTime());

                                        // Update the EditText with the selected date and time
                                        editTaskDueDate.setText(formattedDateTime);
                                    }
                                },
                                hour,
                                minute,
                                DateFormat.is24HourFormat(requireContext())
                        );

                        // Show the TimePickerDialog
                        timePickerDialog.show();
                    }
                },
                year,
                month,
                dayOfMonth
        );
        return datePickerDialog;
    }


    private void fillTaskDetails(View view, Task task) {
        if (task == null) {
            System.out.println("Task is null");
            return;
        }

        EditText taskTitle = view.findViewById(R.id.edit_task_title);
        taskTitle.setText(task.title);

        EditText taskDescription = view.findViewById(R.id.edit_task_description);
        taskDescription.setText(task.description);

        TextView taskCreationDate = view.findViewById(R.id.text_task_creation_date);
        SimpleDateFormat creationDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedCreationDate = creationDateFormat.format(task.creationDate);
        taskCreationDate.setText(formattedCreationDate);

        EditText taskDueDate = view.findViewById(R.id.edit_task_due_date);
        SimpleDateFormat dueDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDueDate = dueDateFormat.format(task.dueDate);
        taskDueDate.setText(formattedDueDate);

        EditText taskCategory = view.findViewById(R.id.edit_task_category);
        taskCategory.setText(task.category);

        CheckBox taskCompleted = view.findViewById(R.id.checkbox_task_completed);
        taskCompleted.setChecked(task.isCompleted);

        CheckBox notificationEnabled = view.findViewById(R.id.checkbox_notifications_enabled);
        notificationEnabled.setChecked(task.notificationsEnabled);
    }
}