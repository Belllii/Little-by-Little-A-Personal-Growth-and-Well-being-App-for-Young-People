package com.example.littlebylittle;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface JournalDao {

    @Insert
    void insertEntry(JournalEntry entry);

    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    List<JournalEntry> getAllEntries();
    @Query("DELETE FROM journal_entries WHERE id = :entryId")
    void deleteEntry(int entryId);
}
