package com.intelj.y_ral_gaming.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Chat.class},version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract ChatDao chatDao();
    private static AppDataBase INSTANCE;
    public static AppDataBase getDBInstance(Context context,String dataBaseName){

        //if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDataBase.class,dataBaseName + ".db")
                    .allowMainThreadQueries()
                    .build();
      //  }
        return INSTANCE;
    }
}
