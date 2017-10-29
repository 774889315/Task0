package com.example.administrator.mytimetable;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Notification.DEFAULT_ALL;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    Button bt;
    MyDatabaseHelper mdbh;
    SQLiteDatabase db;
    TextView tv, tv0;
    Thread checker;
    boolean orderByTime, orderByImportance;

    String subject[] = {"b", "cd", "buicnqklanckac", ""};
    String event[] = {"event1 - bcdefg1234567", "cddccddccddc", "", "123"};
    long time[] = {1508843267L, 1509043267L, 150874326L, 150874324L};
    int[] importance = {1, 2, 3, 1};
    long editTime[] = {1507843267L, 1508853267L, 1508743267L, 1508743268L};

    static boolean stopped;

    MyAdapter adapter;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            MainActivity.this.notify(editTime[msg.what], subject[msg.what]);
        }
    };

    int getHappeningEvent()
    {
        for(int i = 0; i < time.length; i++)
        {
    //        Log.e("zzzzzzzzzzzzzzz", time[i] + "\t" + new Date().getTime() + "\t" + (time[i] - new Date().getTime()));
            if(Math.abs(time[i] - new Date().getTime())/1000 <= importance[i] * 5) return i;
        }
        return -1;
    }


    private ServiceConnection conn = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("我的时间表");
        setContentView(R.layout.activity_main);
     //   Intent intent0 = new Intent(MainActivity.this, MyService.class);
    //    intent0.setAction(".main.MyService");
     //   Log.e("", "service started");
     //   startService(intent0);

        mdbh = new MyDatabaseHelper(MainActivity.this, "memento.db", null, 1);
        db = mdbh.getReadableDatabase();

        bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AddingPanel.class);
                startActivity(intent);
            }
        });

        rv = (RecyclerView) findViewById(R.id.recycleView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        rv.setHasFixedSize(true);

        tv = (TextView) findViewById(R.id.title);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderByImportance = false;
                orderByTime = !orderByTime ;
                onStart();
                Toast.makeText(getApplicationContext(), orderByTime ? "已按发生时间排序" : "已取消排序", Toast.LENGTH_SHORT).show();
            }
        });

        tv0 = (TextView) findViewById(R.id.title0);
        tv0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderByTime = false;
                orderByImportance = !orderByImportance;
                onStart();
                Toast.makeText(getApplicationContext(), orderByImportance ? "已按重要性排序" : "已取消排序", Toast.LENGTH_SHORT).show();
            }
        });



        checker = new Thread()
        {
            public void run()
            {
                int c;
                for(;;)
                {
                    try {
                        Thread.sleep(1*1000);
                        if(stopped)
                        {
                            new Thread()
                            {
                                @Override
                                public void run()
                                {
                                    try
                                    {
                                        Thread.sleep(30*1000);
                                    }
                                    catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                    stopped = false;
                                }
                            }.start();
                            while(stopped)
                            {
                                Thread.sleep(1*1000);
                            }
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    c = getHappeningEvent();


                    if(c != -1)
                    {
                        Message msg = new Message();
                        msg.what = c;
                        handler.sendMessage(msg);
                    }
                }
            }
        };
        checker.start();
    }

    void notify(long editTime, String subject)
    {
        NotificationManager manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder1 = new Notification.Builder(MainActivity.this);
        builder1.setSmallIcon(R.drawable.icon3);
        builder1.setContentTitle("时间到了");
        builder1.setContentText(subject);
        builder1.setWhen(System.currentTimeMillis());
        builder1.setAutoCancel(true);
        builder1.setDefaults(DEFAULT_ALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder1.setPriority(Notification.PRIORITY_MAX);
        }
        Intent intent = getIntent();
        PendingIntent pendingIntent =PendingIntent.getActivity(this, 0, intent, 0);

        builder1.setContentIntent(pendingIntent);
        Notification notification1 = builder1.build();
        manager.notify((int) editTime, notification1);
    }


    void notify(long editTime, String subject, long time, String event)
    {
        Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("subject", subject);
        bundle.putLong("time", time);
        bundle.putString("event", event);
        intent.putExtras(bundle);
        PendingIntent pendingIntent =PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
        AlarmManager aManager = (AlarmManager)getSystemService(ALARM_SERVICE);
/*
        Calendar c = Calendar.getInstance();
    //    c.setTimeZone(TimeZone.getTimeZone("GMT"));
        c.set(Calendar.HOUR, 15);
        c.set(Calendar.MINUTE, 32);
*/


        aManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, pendingIntent);


    }

    @Override
    protected void onStart()
    {
        super.onStart();

 //       notify(0x312414, "subject1", 1509088200000L, "event1");

    //    Cursor cursor = db.rawQuery("select * from memento_tb where subject like ? and event like ? and time like ? and editTime like ? and importance like ?",
    //            new String[] {"%"+""+"%","%"+""+"%","%"+""+"%","%"+""+"%","%"+""+"%"});
        Cursor cursor = db.rawQuery("select * from memento_tb", null);

        ArrayList<String> subject0 = new ArrayList<>();
        ArrayList<String> event0 = new ArrayList<>();
        ArrayList<Long> time0 = new ArrayList<>();
        ArrayList<Long> editTime0 = new ArrayList<>();
        ArrayList<Integer> importance0 = new ArrayList<>();

        //String subject1;

        while(cursor.moveToNext())
        {
           // subject1 = cursor.getString(cursor.getColumnIndex("subject"));
            subject0.add(cursor.getString(cursor.getColumnIndex("subject")));
            event0.add(cursor.getString(cursor.getColumnIndex("event")));
            time0.add(cursor.getLong(cursor.getColumnIndex("time")));
            editTime0.add(cursor.getLong(cursor.getColumnIndex("editTime")));
            importance0.add(cursor.getInt(cursor.getColumnIndex("importance")));

     //       Log.e("\naaaaaaaa\n",cursor.getString(cursor.getColumnIndex("subject")));
        }
        if(orderByTime)
        {
            tv.setText("      <时间>      ");
            tv0.setText("                  事件               ");
            ArrayList<String> subject1 = new ArrayList<>();
            ArrayList<String> event1 = new ArrayList<>();
            ArrayList<Long> time1 = new ArrayList<>();
            ArrayList<Long> editTime1 = new ArrayList<>();
            ArrayList<Integer> importance1 = new ArrayList<>();

            long timeNow = System.currentTimeMillis();

            for(int i = 0; time0.size() != 0; i = 0)
            {
                for(int j = 0; j < time0.size(); j++)
                {
                    if(priority(time0.get(i), timeNow) < priority(time0.get(j), timeNow)) i = j;
                }
                subject1.add(subject0.get(i));
                subject0.remove(i);
                event1.add(event0.get(i));
                event0.remove(i);
                time1.add(time0.get(i));
                time0.remove(i);
                editTime1.add(editTime0.get(i));
                editTime0.remove(i);
                importance1.add(importance0.get(i));
                importance0.remove(i);
            }

            subject = toArray(subject1);
            event = toArray(event1);
            time = toArray0(time1);
            importance = toArray1(importance1);
            editTime = toArray0(editTime1);
            adapter = new MyAdapter(subject, event, time, editTime, importance);
            rv.setAdapter(adapter);
        }

        else if(orderByImportance)
        {
            tv.setText("       时间       ");
            tv0.setText("                 <事件>              ");
            ArrayList<String> subject1 = new ArrayList<>();
            ArrayList<String> event1 = new ArrayList<>();
            ArrayList<Long> time1 = new ArrayList<>();
            ArrayList<Long> editTime1 = new ArrayList<>();
            ArrayList<Integer> importance1 = new ArrayList<>();

            long timeNow = System.currentTimeMillis();

            for(int i = 0; time0.size() != 0; i = 0)
            {
                for(int j = 0; j < time0.size(); j++)
                {
                    if(priority(time0.get(i), timeNow, importance0.get(i)) < priority(time0.get(j), timeNow, importance0.get(j))) i = j;
                }
                subject1.add(subject0.get(i));
                subject0.remove(i);
                event1.add(event0.get(i));
                event0.remove(i);
                time1.add(time0.get(i));
                time0.remove(i);
                editTime1.add(editTime0.get(i));
                editTime0.remove(i);
                importance1.add(importance0.get(i));
                importance0.remove(i);
            }

            subject = toArray(subject1);
            event = toArray(event1);
            time = toArray0(time1);
            importance = toArray1(importance1);
            editTime = toArray0(editTime1);
            adapter = new MyAdapter(subject, event, time, editTime, importance);
            rv.setAdapter(adapter);
        }

        else
        {
            tv.setText("       时间       ");
            tv0.setText("                  事件               ");
            // SimpleCursorAdapter sca = new MyAdapter();
            // String a = cursor.getString(0);
            subject = toArray(subject0);
            //{"b", "cd", "buicnqklanckac"};
            event = toArray(event0);
            //    {"event1 - bcdefg1234567", "cddccddccddc", ""};
            time = toArray0(time0);
            //{1508843267L, 1509543267L, 1508743267L};
            importance = toArray1(importance0);
            //       {1, 2, 3};
            editTime = toArray0(editTime0);
            //{1507843267L, 1508853267L, 1508743267L};
            adapter = new MyAdapter(subject, event, time, editTime, importance);
            rv.setAdapter(adapter);

        }

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new Callback(adapter, MainActivity.this));
        mItemTouchHelper.attachToRecyclerView(rv);


        stopped = true;
    }

    private long priority(long time, long timeNow)
    {
        return (time > timeNow) ? (Long.MAX_VALUE - time) : (time - timeNow);
    }

    private long priority(long time, long timeNow, int importance)
    {
        return (time > timeNow) ? (Long.MAX_VALUE/3 * importance - time) : (time - Long.MAX_VALUE + Long.MAX_VALUE/3 * importance);
    }

    String[] toArray(ArrayList<String> s)
    {
        int length = s.size();
        String[] array = new String[length];
        for(int i = 0; i < length; i++)
        {
            array[i] = s.get(i);
        }
        return array;
    }

    long[] toArray0(ArrayList<Long> s)
    {
        int length = s.size();
        long[] array = new long[length];
        for(int i = 0; i < length; i++)
        {
            array[i] = s.get(i);
        }
        return array;
    }
/*
    long[] toArray1(ArrayList<String> s)
    {
        int length = s.size();
        long[] array = new long[length];
        for(int i = 0; i < length; i++)
        {
            array[i] = Long.getLong(s.get(i));
        }
        return array;
    }
*/
    int[] toArray1(ArrayList<Integer> s)
    {
        int length = s.size();
        int[] array = new int[length];
        for(int i = 0; i < length; i++)
        {
            array[i] = s.get(i);
        }
        return array;
    }




    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
    {
        /*
        private String subject[] = null;
        private String event[] = null;
        private long time[] = null;
        private int importance[] = null;*/
        long editTime[] = null;

        List<MyEvent> me;
     //   boolean finished[];
    //    boolean judge;
        MyAdapter(String[] subject, String event[], long[] time, long editTime[], int[] importance)
        {
    /*
            this.subject = subject;
            this.event = event;
            this.time = time;
            this.importance = importance;
            this.editTime = editTime;
    */
            me = new ArrayList<>();
            for(int i = 0; i < time.length; i++)
            {
                me.add(new MyEvent(subject[i], event[i], time[i], importance[i], editTime[i]));
            }
     //       finished = new boolean[subject.length];
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_layout, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                //    Toast.makeText(getApplicationContext(), (int)v.getTag(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, EditingPanel.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("subject", me.get((int)v.getTag()).subject);
                    bundle.putString("event", me.get((int)v.getTag()).event);
                    bundle.putString("time", me.get((int)v.getTag()).time+"");
                    bundle.putString("editTime", me.get((int)v.getTag()).editTime+"");
                    bundle.putString("importance", me.get((int)v.getTag()).importance+"");
                    bundle.putString("position", (int)v.getTag()+ "");                   //作为删除依据
      //              Log.e("id1\t\t", editTime[0]+"");
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
/*
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
  */



            MyViewHolder holder = new MyViewHolder(view);


            return holder;
        }



        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            holder.itemView.setTag(position);                           //作为标记
            holder.subject.setText(me.get(position).subject);
/*
            String time0 = "未知";

            try
            {
                SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Long time = this.time[position];
                String d = format.format(time);
                Date date = format.parse(d);
                time0 = date.toString();
            }
            catch(Exception e)
            {

            }
*/
            long relativeTime = (me.get(position).time - new Date().getTime())/1000;//转化成秒
            String time1;


            if(relativeTime <= -60)
            {
     //           finished[position] = true;//标记已完成
                holder.subject.setTextColor(Color.rgb(200,200,200));
                holder.time.setTextColor(Color.rgb(200,200,200));
            }
            else
            {
                holder.subject.setTextColor(Color.rgb(0,20,40));
                holder.time.setTextColor(Color.rgb(0,20,40));
            }

            if(relativeTime >= 86400) time1 = relativeTime/86400 + "天后";
            else if(3600 <= relativeTime && relativeTime < 86400) time1 = relativeTime/3600 + "小时后";
            else if(60 <= relativeTime && relativeTime < 3600) time1 = relativeTime/60 + "分钟后";

            else if(relativeTime <= -86400) time1 = relativeTime/-86400 + "天前";
            else if(-3600 >= relativeTime && relativeTime > -86400) time1 = relativeTime/-3600 + "小时前";
            else if(-60 >= relativeTime && relativeTime > -3600) time1 = relativeTime/-60 + "分钟前";


            else
            {
                time1 = "现在";
                holder.time.setTextColor(Color.rgb(255, 0, 0));
            }

          //  holder.time.setText(time0);
            holder.time.setText(time1);

            switch(importance[position] * (relativeTime > -60 ? 1 : -1))
            {
                case -1:
                    holder.subject.setBackgroundColor(Color.rgb(220, 220, 250));
                    break;
                case -2:
                    holder.subject.setBackgroundColor(Color.rgb(230, 210, 150));
                    break;
                case -3:
                    holder.subject.setBackgroundColor(Color.rgb(250, 210, 210));
                    break;
                case 1:
                    holder.subject.setBackgroundColor(Color.rgb(120, 120, 210));
                    break;
                case 2:
                    holder.subject.setBackgroundColor(Color.rgb(220, 180, 30));
                    break;
                case 3:
                    holder.subject.setBackgroundColor(Color.rgb(250, 110, 110));
                    break;
            }


        }

        @Override
        public int getItemCount()
        {
            return subject.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {
            public TextView subject;
            public TextView time;

            public MyViewHolder(View itemView) {
                super(itemView);
                subject = (TextView) itemView.findViewById(R.id.event);
                time = (TextView) itemView.findViewById(R.id.time);
           //     tv.setText("a");
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
   //     Log.e("paused","");
   //     stopped = false;                  //根据情况决定是否增加
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


}

