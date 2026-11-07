package com.example.littlebylittle;

import java.util.ArrayList;
import java.util.List;

public class CommunityPost {

    private String id;
    private String userId;
    private String authorName;
    private String message;
    private String category;
    private long timestamp;
    private int likeCount;
    private List<String> likedBy;
    private String imageUrl;

    public CommunityPost() {
        this.likedBy = new ArrayList<>();
    }

    public CommunityPost(String id, String userId, String authorName, String message, String category, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.authorName = authorName;
        this.message = message;
        this.category = category;
        this.timestamp = timestamp;
        this.likeCount = 0;
        this.likedBy = new ArrayList<>();
        this.imageUrl = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
