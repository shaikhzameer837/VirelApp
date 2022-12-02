package com.intelj.y_ral_gaming.db;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Chat.class,VideoList.class},version = 3,autoMigrations = {
        @AutoMigration(from = 2, to = 3)
})
public abstract class AppDataBase extends RoomDatabase {
    public abstract ChatDao chatDao();
    public abstract VideoListDao videosDao();
    private static AppDataBase INSTANCE;
    public static AppDataBase getDBInstance(Context context,String dataBaseName){

        //if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDataBase.class,dataBaseName + ".db")
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    //.fallbackToDestructiveMigration()
                    .build();
      //  }
        return INSTANCE;
    }
    @NonNull
    public static Migration MIGRATION_1_2 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS VideoList (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `videoId` TEXT, `created_at` INTEGER NOT NULL, `views` INTEGER NOT NULL, `status` INTEGER NOT NULL, `owner` TEXT, `times` TEXT, `game` TEXT)");
        }
    };
}
