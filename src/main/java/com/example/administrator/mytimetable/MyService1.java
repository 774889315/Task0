package com.example.administrator.mytimetable;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2017/10/25.
 */

public class MyService1 extends Service {


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
        new Thread()
        {
            public void run()
            {
                Intent intent0 = new Intent(MyService1.this, MyService.class);
         //       while(true)
                {

                    Log.d("", "1111111111111");
                    startService(intent0);
                }
            }
        }.start();

        return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Intent intent0 = new Intent(MyService1.this, MyService.class);
        startService(intent0);
    }
}
