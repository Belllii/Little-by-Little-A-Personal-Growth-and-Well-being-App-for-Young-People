package com.example.littlebylittle;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JournalDao {

    @Insert
    void insertEntry(JournalEntry entry);

    @Query("SELECT * FROM journal_entries WHERE userId = :userId ORDER BY timestamp DESC")
    List<JournalEntry> getAllEntries(String userId);

    @Query("SELECT * FROM journal_entries WHERE id = :entryId LIMIT 1")
    JournalEntry getEntryById(int entryId);

    @Query("UPDATE journal_entries SET content = :content WHERE id = :entryId")
    void updateEntryContent(int entryId, String content);

    @Query("DELETE FROM journal_entries WHERE id = :entryId")
    void deleteEntry(int entryId);

    @Query("SELECT COUNT(*) FROM journal_entries WHERE userId = :userId")
    int getTotalJournalEntries(String userId);

    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    List<JournalEntry> getAllEntries();

    @Query("SELECT COUNT(*) FROM journal_entries")
    int getTotalJournalEntries();
}
