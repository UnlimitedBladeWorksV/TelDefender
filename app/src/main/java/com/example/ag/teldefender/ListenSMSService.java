package com.example.ag.teldefender;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Ag on 2017/6/11.
 */

public class ListenSMSService extends Service{
    String number="";
    String body="";
    MyDbHelper mydbhelper;
    boolean bool=false;
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        this.number=intent.getStringExtra("number");
        this.body=intent.getStringExtra("body");

        mydbhelper=new MyDbHelper(ListenSMSService.this);
        mydbhelper.open();
        Cursor cur=mydbhelper.querData("black");
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            if(cur.getString(0).equals(number)){
                Toast.makeText(ListenSMSService.this,"拦截信息"+number+":"+body,Toast.LENGTH_SHORT).show();
                bool=true;
                break;
            }
            cur.moveToNext();
        }
        if(bool){
            Calendar c=Calendar.getInstance();
            int year=c.get(Calendar.YEAR);
            int month=c.get(Calendar.MONTH);
            int day=c.get(Calendar.DAY_OF_WEEK);
            int hour=c.get(Calendar.HOUR_OF_DAY);
            int minute=c.get(Calendar.MINUTE);
            String data=String.valueOf(year)+"/"+String.valueOf(month)+"/"+String.valueOf(day);
            String time=String.valueOf(hour)+":"+String.valueOf(minute);
            mydbhelper.insertData(number, body, time, data,"sms");
        }
        cur.close();
        mydbhelper.close();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
