package com.todo.todolistapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
            System.out.println("TaskAdapter: " + taskList.size());
        }
        filterCategory = "All";
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }

        public void bind(Task task) {
            TextView taskTitle = itemView.findViewById(R.id.task_title);
            taskTitle.setText(task.title);

            TextView taskDueDate = itemView.findViewById(R.id.due_time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(task.dueDate);
            taskDueDate.setText(formattedDate);

            TextView taskDescription = itemView.findViewById(R.id.invisible_task_id);
            taskDescription.setText(String.valueOf(task.taskId));

            ImageButton imageButton = itemView.findViewById(R.id.complete_button);
            if (task.isCompleted) {
                imageButton.setImageResource(R.drawable.checked_24);
            } else {
                imageButton.setImageResource(R.drawable.unchecked_24);
            }

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
                    TextView textView = cardView.findViewById(R.id.invisible_task_id);
                    String text = textView.getText().toString();
                    int task_id = Integer.parseInt(text);
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
                    TextView textView = itemView.findViewById(R.id.invisible_task_id);
                    String text = textView.getText().toString();
                    int task_id = Integer.parseInt(text);
                    TaskAdapter.databaseHelper.setTaskToFinished(task_id);

                    ImageButton imageButton = itemView.findViewById(R.id.complete_button);
                    imageButton.setImageResource(R.drawable.checked_24);
                    setTaskList(databaseHelper.getTasksSortedByDueDate());
                    notifyDataSetChanged();

                }
            });

        }
    }

    public interface OnCardClickListener {
        void onCardClick (int task_id);
    }
    static OnCardClickListener onCardClickListener;
    public void setOnCardClickListener(OnCardClickListener listener) {
        onCardClickListener = listener;
    }

}
