package com.intelj.y_ral_gaming.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VideoListDao {
    @Query("Select * From videolist ")
    List<VideoList> getAllVideo();

    @Insert()
    void insertUser(VideoList...videos);

    @Delete
    void delete(VideoList videoList);

    @Query("Select * From videolist order by cid DESC limit 1")
    List<VideoList> getLastVideo();
}
