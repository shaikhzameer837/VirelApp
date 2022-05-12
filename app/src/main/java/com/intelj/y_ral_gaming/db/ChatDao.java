package com.intelj.y_ral_gaming.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatDao {
    @Query("Select * From chat ")
    List<Chat> getAllChat();

    @Insert()
    void insertUser(Chat...chats);

    @Delete
    void delete(Chat chat);
}
