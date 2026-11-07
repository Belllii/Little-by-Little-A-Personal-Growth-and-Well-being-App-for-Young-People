package com.example.littlebylittle;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {

    private final Context context;
    private final List<CommunityPost> posts;
    private final String currentUserId;
    private final OnLikeClickListener likeListener;

    public interface OnLikeClickListener {
        void onLikeClick(CommunityPost post);
    }

    public CommunityAdapter(Context context, List<CommunityPost> posts, String currentUserId, OnLikeClickListener likeListener) {
        this.context = context;
        this.posts = posts;
        this.currentUserId = currentUserId;
        this.likeListener = likeListener;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community_post, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        CommunityPost post = posts.get(position);

        holder.tvAuthor.setText(post.getAuthorName() != null ? post.getAuthorName() : "Community Member");
        holder.tvCategory.setText(post.getCategory() != null ? "#" + post.getCategory() : "#Reflection");
        holder.tvMessage.setText(post.getMessage());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(post.getTimestamp())));

        boolean isLiked = post.getLikedBy() != null && post.getLikedBy().contains(currentUserId);
        holder.btnLike.setText((isLiked ? "❤️ " : "🤍 ") + post.getLikeCount() + " Support");

        holder.btnLike.setOnClickListener(v -> {
            if (likeListener != null) {
                likeListener.onLikeClick(post);
            }
        });

        // Implicit Intent: Share social post
        holder.btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Inspiration from Little by Little 🌱");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "\"" + post.getMessage() + "\"\n— " + post.getAuthorName() + " on Little by Little");
            context.startActivity(Intent.createChooser(shareIntent, "Share post via"));
        });
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    static class CommunityViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor;
        TextView tvCategory;
        TextView tvDate;
        TextView tvMessage;
        Button btnLike;
        Button btnShare;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_post_author);
            tvCategory = itemView.findViewById(R.id.tv_post_category);
            tvDate = itemView.findViewById(R.id.tv_post_date);
            tvMessage = itemView.findViewById(R.id.tv_post_message);
            btnLike = itemView.findViewById(R.id.btn_like_post);
            btnShare = itemView.findViewById(R.id.btn_share_post);
        }
    }
}
