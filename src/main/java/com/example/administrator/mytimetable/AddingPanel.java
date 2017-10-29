package com.example.administrator.mytimetable;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class AddingPanel extends AppCompatActivity implements View.OnClickListener
{
    EditText subject, date, time, event;
    TextView editTime, showImportance;
    Button add, selectTime, selectDate, delete;
    StringBuffer stringBuilder;
    MyDatabaseHelper mdbh;
    RatingBar rb;
    int importance = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle("新建事件");
        setContentView(R.layout.second_layout);
        subject = (EditText) findViewById(R.id.title);
        date = (EditText) findViewById(R.id.date);
        time = (EditText) findViewById(R.id.time);
        time.setText("12:00");
        event = (EditText) findViewById(R.id.event);
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);
        delete = (Button) findViewById(R.id.delete);
        delete.setVisibility(View.INVISIBLE);
        editTime = (TextView) findViewById(R.id.editTime);
        editTime.setVisibility(View.INVISIBLE);
        showImportance = (TextView) findViewById(R.id.showImportance);
        showImportance.setText("不重要");
        selectTime = (Button) findViewById(R.id.selectTime);
        selectTime.setOnClickListener(this);
        selectDate = (Button) findViewById(R.id.selectDate);
        selectDate.setOnClickListener(this);
        rb = (RatingBar) findViewById(R.id.importance);
        rb.setRating(1);
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

        stringBuilder = new StringBuffer("");

    }


    void add(SQLiteDatabase db, String subject, String event, long time, long editTime, int importance) throws Exception
    {
        if(time < 0) time = 1/0;//generate the exception deliberately
        db.execSQL("insert into memento_tb values(null,?,?,?,?,?)", new String[]{subject,event,time+"",editTime+"",importance+""});
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
                new DatePickerDialog(AddingPanel.this,
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
                new TimePickerDialog(AddingPanel.this, new TimePickerDialog.OnTimeSetListener()
                {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        stringBuilder.append(hourOfDay+":"+minute);
                        time.setText(stringBuilder);
                    }
                }, time0.get(Calendar.HOUR_OF_DAY), time0.get(Calendar.MINUTE), true).show();
                break;
            default:
                mdbh = new MyDatabaseHelper(AddingPanel.this, "memento.db", null, 1);
                SQLiteDatabase db = mdbh.getReadableDatabase();
                try
                {
                    importance = (int) (rb.getRating()+.1);
                    add(db, subject.getText().toString(), event.getText().toString(), getTime(), new Date().getTime(), importance);
    //                Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_SHORT).show();
                    exit(0);
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "请选择正确的日期时间", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
        }




    }
}


