package com.example.littlebylittle;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CheckInDao {

    @Insert
    void insertCheckIn(CheckIn checkIn);

    @Update
    void updateCheckIn(CheckIn checkIn);

    @Query("SELECT * FROM check_ins WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    CheckIn getLatestCheckIn(String userId);

    @Query("SELECT * FROM check_ins WHERE userId = :userId ORDER BY timestamp DESC")
    List<CheckIn> getAllCheckIns(String userId);

    @Query("SELECT COUNT(*) FROM check_ins WHERE userId = :userId")
    int getTotalCheckIns(String userId);

    @Query("DELETE FROM check_ins WHERE id = :id")
    void deleteCheckIn(int id);

    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC LIMIT 1")
    CheckIn getLatestCheckIn();

    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC")
    List<CheckIn> getAllCheckIns();

    @Query("SELECT COUNT(*) FROM check_ins")
    int getTotalCheckIns();
}