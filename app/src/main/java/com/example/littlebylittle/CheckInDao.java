package com.example.littlebylittle;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CheckInDao {

    @Insert
    void insertCheckIn(CheckIn checkIn);

    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC LIMIT 1")
    CheckIn getLatestCheckIn();

    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC")
    java.util.List<CheckIn> getAllCheckIns();
}