package com.example.administrator.mytimetable;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.exit;

public class EditingPanel extends AppCompatActivity implements View.OnClickListener
{
    EditText subject, date, time, event;
    TextView editTime, showImportance;
    Button add, selectTime, selectDate, delete;
    StringBuffer stringBuilder;
    MyDatabaseHelper mdbh;
    SQLiteDatabase db;
    RatingBar rb;
    boolean check = false;
    int position, importance;
    long editTime0 = -1;

    Bundle bundle;

    String getDate(long time)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String s = format.format(time);//秒单位转换成毫秒
        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bundle = this.getIntent().getExtras();

        setTitle("编辑事件");
        setContentView(R.layout.second_layout);

        long time0 = Long.valueOf(bundle.get("time")+"");
        editTime0 = Long.valueOf(bundle.get("editTime").toString());
   //     Log.e("editTime2\t\t", Long.valueOf(bundle.get("editTime").toString())+"");
        importance = Integer.valueOf(bundle.get("importance").toString());

        subject = (EditText) findViewById(R.id.title);
        subject.setText(bundle.get("subject").toString());
        date = (EditText) findViewById(R.id.date);
        date.setText(getDate(time0).substring(0, getDate(time0).indexOf(' ')));
        time = (EditText) findViewById(R.id.time);
        time.setText(getDate(time0).substring(getDate(time0).indexOf(' ')));
        event = (EditText) findViewById(R.id.event);
        event.setText(bundle.get("event").toString());
        add = (Button) findViewById(R.id.add);
        add.setText("保存修改");
        add.setOnClickListener(this);
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        editTime = (TextView) findViewById(R.id.editTime);
        editTime.setText("创建/修改时间： " + getDate(editTime0));
        showImportance = (TextView) findViewById(R.id.showImportance);
        selectTime = (Button) findViewById(R.id.selectTime);
        selectTime.setOnClickListener(this);
        selectDate = (Button) findViewById(R.id.selectDate);
        selectDate.setOnClickListener(this);
        rb = (RatingBar) findViewById(R.id.importance);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                switch((int)(rating+.1))
                {
                    case 1:
                        showImportance.setText("不重要");
                        break;
                    case 2:
                        showImportance.setText("一般");
                        break;
                    case 3:
                        showImportance.setText("重要");
                        break;
                }
            }
        });

        rb.setRating(importance);

        stringBuilder = new StringBuffer("");

        mdbh = new MyDatabaseHelper(EditingPanel.this, "memento.db", null, 1);
        db = mdbh.getReadableDatabase();

        position = Integer.valueOf(bundle.get("position")+"");

    }


    void add(SQLiteDatabase db, String subject, String event, long time, long editTime, int importance) throws Exception
    {
        if(time < 0) time = 1/0;//generate the exception deliberately
        db.execSQL("insert into memento_tb values(null,?,?,?,?,?)", new String[]{subject,event,time+"",editTime+"",importance+""});
    }

    void delete()
    {
        db.execSQL("delete from memento_tb where editTime='"+editTime0+"'");//以修改时间作为标准来找
    }

    long getTime() throws Exception
    {

        long time0 = -1;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String timeSet = date.getText() + " " + time.getText();
        Date date = format.parse(timeSet);
        time0 = date.getTime();

        return time0;


    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.selectDate:
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(EditingPanel.this,
                        new DatePickerDialog.OnDateSetListener()
                        {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                date.setText(year+"-"+(month+1)+"-"+dayOfMonth);

                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
                ).show();
                break;

            case R.id.selectTime:
                Calendar time0 = Calendar.getInstance();
                new TimePickerDialog(EditingPanel.this, new TimePickerDialog.OnTimeSetListener()
                {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        stringBuilder.append(hourOfDay+":"+minute);
                        time.setText(stringBuilder);
                    }
                }, time0.get(Calendar.HOUR_OF_DAY), time0.get(Calendar.MINUTE), true).show();
                break;
            case R.id.delete:
                if(!check)
                {
                    check = true;
                    new Thread()
                    {
                        @Override
                        public void run()
                        {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            check = false;
                        }
                    }.start();
                    Toast.makeText(getApplicationContext(), "再点一次删除", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    delete();
                    exit(0);
                }

                break;

            default:
                try
                {
                    importance = (int) (rb.getRating()+.1);
                    add(db, subject.getText().toString(), event.getText().toString(), getTime(), new Date().getTime(), importance);
    //                Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_SHORT).show();
                    delete();
                    exit(0);
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "出错了！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
        }




    }
}


