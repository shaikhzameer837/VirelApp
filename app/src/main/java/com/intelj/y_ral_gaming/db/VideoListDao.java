package com.intelj.y_ral_gaming.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VideoListDao {
    @Query("Select * From videolist ORDER BY RANDOM() LIMIT 200")
    List<VideoList> getAllVideo();

   @Query("Select * From videolist Where videoId = :videoId limit 200")
    List<VideoList> getAllSelectedVideo(String videoId);

    @Insert()
    void insertVideo(VideoList...videos);

    @Delete
    void delete(VideoList videoList);

    @Query("Select count(*) From videolist where videoId = :videoId limit 1")
    List<Integer> getLastVideo(String videoId);

    @Query("SELECT * FROM videolist WHERE videoId = :videoId")
    int isDataExist(String videoId);

    @Query("UPDATE videolist SET game = :game WHERE videoId = :videoId")
    int updateGame(String videoId, String game);
}
