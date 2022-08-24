package com.intelj.y_ral_gaming.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Chat {
    @PrimaryKey(autoGenerate = true)
    public int cid;
    @ColumnInfo(name = "messages")
    public String messages;

    @ColumnInfo(name = "subject")
    public int subject;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "status")
    public int status = 0;

    @ColumnInfo(name = "owner")
    public String owner;

    @ColumnInfo(name = "times")
    public String times;

    @ColumnInfo(name = "msgStatus")
    public int msgStatus;

    @ColumnInfo(name = "blurImg")
    public String blurImg;
}
