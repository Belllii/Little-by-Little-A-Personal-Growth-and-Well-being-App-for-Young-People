package com.example.littlebylittle;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "journal_entries")
public class JournalEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;
    private String title;
    private String content;
    private long timestamp;

    public JournalEntry(String userId, String title, String content, long timestamp) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Ignore
    public JournalEntry(String content, long timestamp) {
        this("anonymous", "Reflection", content, timestamp);
    }

    @Ignore
    public JournalEntry(String userId, String content, long timestamp) {
        this(userId, "Reflection", content, timestamp);
    }

    @Ignore
    public JournalEntry() {
        // Default constructor for Firebase Firestore
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
