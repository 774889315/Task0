package com.example.administrator.mytimetable;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Administrator on 2017/10/25.
 */

public class MyService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /*
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }
    */

    @Override
    public void onCreate()
    {






    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(MyService.this)
                //           .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("a")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("b")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        Notification notification = mNotifyBuilder.build();

        startForeground(0x111, notification);



        new Thread()
        {
            public void run()
            {
           //     Intent intent0 = new Intent(MyService.this, MyService1.class);
          //      while(true)
                {


                    Log.d("", "00000000000");
          //          startService(intent0);
                }
            }
        }.start();

        return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Intent intent = new Intent(MyService.this, MyService.class);
        startService(intent);
    }
}
