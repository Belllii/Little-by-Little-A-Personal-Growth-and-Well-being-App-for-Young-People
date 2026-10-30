
package com.example.littlebylittle;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "check_ins")
public class CheckIn {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String mood;
    private String note;
    private long timestamp;

    public CheckIn(String mood, String note, long timestamp) {
        this.mood = mood;
        this.note = note;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMood() {
        return mood;
    }

    public String getNote() {
        return note;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
