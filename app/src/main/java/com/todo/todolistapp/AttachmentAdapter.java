package com.todo.todolistapp;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder> {

    private List<Attachment> attachmentList;
    private DatabaseHelper databaseHelper;
    private int task_id;

    public AttachmentAdapter(Context context, int task_id) {
        databaseHelper = new DatabaseHelper(context);
        this.task_id = task_id;
        if (task_id != -1)
            attachmentList = databaseHelper.getTaskAttachments(task_id);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attachment attachment = attachmentList.get(position);
        holder.bind(attachment);
    }

    @Override
    public int getItemCount() {
        if (attachmentList == null)
            return 0;
        return attachmentList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Attachment attachment) {
            TextView invisibleId = itemView.findViewById(R.id.invisible_attachment_id);
            invisibleId.setText(String.valueOf(attachment.attachment_id));

            TextView textView = itemView.findViewById(R.id.attachment_name);
            textView.setText(attachment.path);

            CardView cardView = itemView.findViewById(R.id.attachment_cell_container);

            configureDeleteAttachmentButton(cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileHelper filehelper = new FileHelper(v.getContext());
                    filehelper.launchFile(attachment.path);
                }
            });
        }

        private void configureDeleteAttachmentButton(CardView cardView) {
            ImageButton deleteAttachmentButton = itemView.findViewById(R.id.attachment_delete_button);
            deleteAttachmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup parent = (ViewGroup) v.getParent();
                    TextView invisibleId = parent.findViewById(R.id.invisible_attachment_id);
                    int attachment_id = Integer.parseInt(invisibleId.getText().toString());
                    TextView textView = parent.findViewById(R.id.attachment_name);
                    String path = textView.getText().toString();

                    DatabaseHelper databaseHelper = new DatabaseHelper(v.getContext());
                    databaseHelper.removeFileFromTask(path, attachment_id);
                    FileHelper fileHelper = new FileHelper(v.getContext());
                    fileHelper.deleteFileFromCache(path);
                    TextView name = parent.findViewById(R.id.attachment_name);
                    name.setText("Attachment deleted");
                    cardView.setOnClickListener(null);
                    deleteAttachmentButton.setVisibility(View.GONE);
                }
            });
        }

    }

}
