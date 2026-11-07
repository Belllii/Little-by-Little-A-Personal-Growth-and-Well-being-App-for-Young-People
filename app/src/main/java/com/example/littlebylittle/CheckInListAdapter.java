package com.example.littlebylittle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckInListAdapter extends BaseAdapter {

    private final Context context;
    private final List<CheckIn> checkIns;
    private final OnCheckInDeleteListener deleteListener;
    private final LayoutInflater inflater;

    public interface OnCheckInDeleteListener {
        void onDelete(CheckIn checkIn);
    }

    public CheckInListAdapter(Context context, List<CheckIn> checkIns, OnCheckInDeleteListener deleteListener) {
        this.context = context;
        this.checkIns = checkIns;
        this.deleteListener = deleteListener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return checkIns != null ? checkIns.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return checkIns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return checkIns.get(position).getId();
    }

    static class ViewHolder {
        TextView tvMoodEmoji;
        TextView tvMoodTitle;
        TextView tvDate;
        TextView tvNote;
        ImageButton btnDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_check_in, parent, false);
            holder = new ViewHolder();
            holder.tvMoodEmoji = convertView.findViewById(R.id.tv_item_mood_emoji);
            holder.tvMoodTitle = convertView.findViewById(R.id.tv_item_mood_title);
            holder.tvDate = convertView.findViewById(R.id.tv_item_checkin_date);
            holder.tvNote = convertView.findViewById(R.id.tv_item_checkin_note);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete_checkin);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CheckIn item = checkIns.get(position);

        String rawMood = item.getMood() != null ? item.getMood() : "🌱 Check-in";
        holder.tvMoodTitle.setText(rawMood);

        // Extract emoji if available
        if (rawMood.contains(" ")) {
            String[] parts = rawMood.split(" ", 2);
            holder.tvMoodEmoji.setText(parts[0]);
            holder.tvMoodTitle.setText(parts[1]);
        } else {
            holder.tvMoodEmoji.setText("🌱");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(item.getTimestamp())));

        if (item.getNote() != null && !item.getNote().trim().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText("Note: " + item.getNote());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(item);
            }
        });

        return convertView;
    }
}
