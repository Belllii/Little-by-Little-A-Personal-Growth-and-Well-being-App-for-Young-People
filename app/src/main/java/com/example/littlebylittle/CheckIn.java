
package com.example.littlebylittle;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "check_ins")
public class CheckIn {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;
    private String mood;
    private String note;
    private long timestamp;

    public CheckIn(String userId, String mood, String note, long timestamp) {
        this.userId = userId;
        this.mood = mood;
        this.note = note;
        this.timestamp = timestamp;
    }

    @Ignore
    public CheckIn(String mood, String note, long timestamp) {
        this("anonymous", mood, note, timestamp);
    }

    @Ignore
    public CheckIn() {
        // Default constructor for Firebase Realtime Database
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

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
