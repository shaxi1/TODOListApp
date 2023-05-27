package com.todo.todolistapp;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private DatabaseHelper databaseHelper;
    List<Task> taskList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    public interface OnCardClickListener {
        void onCardClick (int task_id);
    }
    private OnCardClickListener onCardClickListener;

    private void configureCardClick(View view) {
        CardView cardView = view.findViewById(R.id.task_cell_container);
        if (cardView == null) {
            return;
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // determine card id
                int cardId = 1;

                onCardClickListener.onCardClick(cardId);
            }
        });
    }
}
