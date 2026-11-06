
package com.example.littlebylittle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private final List<JournalEntry> entries;

    public JournalAdapter(List<JournalEntry> entries) {
        this.entries = entries;
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

        String date = dateFormat.format(
                new Date(entry.getTimestamp())
        );

        holder.dateText.setText(date);
        holder.contentText.setText(entry.getContent());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {

        TextView dateText;
        TextView contentText;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);

            dateText = itemView.findViewById(R.id.tv_entry_date);
            contentText = itemView.findViewById(R.id.tv_entry_content);
        }
    }
}
