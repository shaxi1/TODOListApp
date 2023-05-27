package com.todo.todolistapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    static DatabaseHelper databaseHelper;
    private List<Task> taskList;
    private String filterCategory;

    public TaskAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context);
        if (databaseHelper.hideFinishedTasks()) {
            taskList = databaseHelper.getTasksNoFinished();
        } else {
            taskList = databaseHelper.getTasksSortedByDueDate();
        }
        filterCategory = "All";
    }

    public void setFilterCategory(String choice) {
        filterCategory = choice;
        updateTaskList();
    }

    private void updateTaskList() {
        if (filterCategory.equals("All")) {
            taskList = databaseHelper.getTasksSortedByDueDate();
        } else {
            taskList = databaseHelper.getTasksWithCategory(filterCategory);
        }

        if (databaseHelper.hideFinishedTasks()) {
            for (int i = 0; i < taskList.size(); i++) {
                if (taskList.get(i).isCompleted) {
                    taskList.remove(i);
                    i--;
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(),R.layout.task_cell,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }

        public void bind(Task task) {
            TextView taskTitle = itemView.findViewById(R.id.task_title);
            taskTitle.setText(task.title);

            TextView taskDueDate = itemView.findViewById(R.id.due_time);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            taskDueDate.setText(simpleDateFormat.format(task.dueDate));

            TextView taskDescription = itemView.findViewById(R.id.invisible_task_id);
            taskDescription.setText(String.valueOf(task.taskId));

            configureImageButton(itemView);
            configureCardClick(itemView);
        }

        private void configureCardClick(View view) {
            CardView cardView = view.findViewById(R.id.task_cell_container);
            if (cardView == null) {
                return;
            }

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int task_id = Integer.parseInt(String.valueOf(itemView.findViewById(R.id.invisible_task_id)));
                    onCardClickListener.onCardClick(task_id);
                }
            });
        }

        private void configureImageButton(View itemView) {
            ImageButton imageButton = itemView.findViewById(R.id.complete_button);
            if (imageButton == null) {
                return;
            }

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int task_id = Integer.parseInt(String.valueOf(itemView.findViewById(R.id.invisible_task_id)));
                    TaskAdapter.databaseHelper.setTaskToFinished(task_id);

                    ImageButton imageButton = itemView.findViewById(R.id.complete_button);
                    imageButton.setImageResource(R.drawable.checked_24);
                }
            });

        }
    }

    public interface OnCardClickListener {
        void onCardClick (int task_id);
    }
    static OnCardClickListener onCardClickListener;

}
