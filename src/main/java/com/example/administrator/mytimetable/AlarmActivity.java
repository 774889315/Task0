package com.example.administrator.mytimetable;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.Date;

import static android.app.Notification.DEFAULT_ALL;

/**
 * Created by Administrator on 2017/10/27.
 */

public class AlarmActivity extends Activity
{
    MediaPlayer mp;
    TextView time, subject, event;

    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.alarm_layout);
        Bundle bundle = this.getIntent().getExtras();
        time = (TextView) findViewById(R.id.time0);
        subject = (TextView) findViewById(R.id.subject0);
        event = (TextView) findViewById(R.id.event0);

        long time0 = bundle.getLong("time");
        String event0 = bundle.getString("event");
        String subject0 = bundle.getString("subject");

        subject.setText(subject0);
        event.setText(event0);

        Date date = new Date();
        date.setTime(time0);

        String time1 = date.getHours()+":"+date.getMinutes();

        time.setText(time1);



        mp = new MediaPlayer();


        try {
            mp.setDataSource("/storage/emulated/0/Downloads/test.mp3");
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
   //     mp.stop();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mp.stop();
    }

}
