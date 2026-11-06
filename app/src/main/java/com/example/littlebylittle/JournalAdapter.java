
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
    private final OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDelete(JournalEntry entry);
    }

    public JournalAdapter(
            List<JournalEntry> entries,
            OnDeleteClickListener deleteListener) {

        this.entries = new ArrayList<>(entries);
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
                new SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        Locale.getDefault()
                );

        holder.dateText.setText(
                dateFormat.format(new Date(entry.getTimestamp()))
        );

        holder.contentText.setText(entry.getContent());

        holder.deleteButton.setOnClickListener(v ->
                deleteListener.onDelete(entry)
        );
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
        Button deleteButton;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);

            dateText = itemView.findViewById(R.id.tv_entry_date);
            contentText = itemView.findViewById(R.id.tv_entry_content);
            deleteButton = itemView.findViewById(R.id.btn_delete_entry);
        }
    }
}
