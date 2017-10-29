package com.example.administrator.mytimetable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/10/28.
 */

public class Callback extends ItemTouchHelper.Callback
{
    MyDatabaseHelper mdbh;
    MainActivity.MyAdapter adapter;
    SQLiteDatabase db;
    Callback(MainActivity.MyAdapter adapter, Context context)
    {
        this.adapter = adapter;
        mdbh = new MyDatabaseHelper(context, "memento.db", null, 1);
        db = mdbh.getReadableDatabase();
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragsFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragsFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        if(fromPosition < toPosition)
        {
            for(int i = fromPosition; i < toPosition; i++)
            {
                Collections.swap(adapter.me, i, i + 1);
                adapter.notifyItemMoved(i, i + 1);
            }
        }
        else
        {
            for(int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(adapter.me, i, i - 1);
                adapter.notifyItemMoved(i, i - 1);
            }
        }
        //保存修改
        db.execSQL("delete from memento_tb where 1=1");
        for(int i = 0; i < adapter.me.size(); i++)
        {
            adapter.me.get(i).addToDb(db);
        }

        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {
        int c = viewHolder.getAdapterPosition();
        db.execSQL("delete from memento_tb where editTime='"+adapter.me.get(viewHolder.getAdapterPosition()).editTime+"'");//以修改时间作为标准来找
   //     Log.e("cccccccccccc", c+"");

    //    adapter.notifyItemRemoved(c);
   //     Log.e("cccccccccccc", c+"");
        adapter.me.remove(c);//^
    }
}
