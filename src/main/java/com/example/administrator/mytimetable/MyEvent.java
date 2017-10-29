package com.example.administrator.mytimetable;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2017/10/28.
 */

public class MyEvent
{
    String subject;
    String event;
    long time;
    int importance;
    long editTime;

    MyEvent(String subject, String event, long time, int importance, long editTime)
    {
        this.subject = subject;
        this.event = event;
        this.time = time;
        this.importance = importance;
        this.editTime = editTime;
    }

    void addToDb(SQLiteDatabase db)
    {
        db.execSQL("insert into memento_tb values(null,?,?,?,?,?)", new String[]{subject,event,time+"",editTime+"",importance+""});
    }
}
