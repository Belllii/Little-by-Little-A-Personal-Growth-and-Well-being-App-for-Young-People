package com.example.littlebylittle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private final List<JournalEntry> entries;
    private final OnEditClickListener editListener;
    private final OnDeleteClickListener deleteListener;

    public interface OnEditClickListener {
        void onEdit(JournalEntry entry);
    }

    public interface OnDeleteClickListener {
        void onDelete(JournalEntry entry);
    }

    public JournalAdapter(
            List<JournalEntry> entries,
            OnEditClickListener editListener,
            OnDeleteClickListener deleteListener) {

        this.entries = new ArrayList<>(entries);
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_journal_entry, parent, false);

        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull JournalViewHolder holder, int position) {

        JournalEntry entry = entries.get(position);

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

        String dateStr = dateFormat.format(new Date(entry.getTimestamp()));
        if (entry.getTitle() != null && !entry.getTitle().isEmpty()) {
            holder.dateText.setText(entry.getTitle() + " • " + dateStr);
        } else {
            holder.dateText.setText(dateStr);
        }

        holder.contentText.setText(entry.getContent());

        if (holder.editButton != null) {
            holder.editButton.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onEdit(entry);
                }
            });
        }

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(entry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void removeEntry(JournalEntry entry) {
        int position = entries.indexOf(entry);
        if (position != -1) {
            entries.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {

        TextView dateText;
        TextView contentText;
        Button editButton;
        Button deleteButton;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);

            dateText = itemView.findViewById(R.id.tv_entry_date);
            contentText = itemView.findViewById(R.id.tv_entry_content);
            editButton = itemView.findViewById(R.id.btn_edit_entry);
            deleteButton = itemView.findViewById(R.id.btn_delete_entry);
        }
    }
}
