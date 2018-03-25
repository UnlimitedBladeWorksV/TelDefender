package com.example.ag.teldefender;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * Created by Ag on 2017/6/11.
 * 来电监听服务
 */

public class ListenPhoneService extends Service {

    MyDbHelper myDbHelper;
    private TelephonyManager telephony;
    private MediaRecorder recorder;
    private boolean recording ;
    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;
    private boolean interceptState = false; //拦截开关
    private AudioRecoderUtils audioRecoderUtils;
    private boolean recordstate = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.telephony=(TelephonyManager) super.getSystemService(Context.TELEPHONY_SERVICE);
        this.telephony.listen(new PhoneStateListen(), PhoneStateListener.LISTEN_CALL_STATE);
        return super.onStartCommand(intent, flags, startId);
    }
    //获取手机状态
    private class PhoneStateListen extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            final AudioManager audioManager = (AudioManager) ListenPhoneService.this.getSystemService(Context.AUDIO_SERVICE);
            switch(state){
                //手机空闲
                case TelephonyManager.CALL_STATE_IDLE:
                    //Toast.makeText(ListenPhoneService.this, "结束录音"+incomingNumber, Toast.LENGTH_SHORT).show();
                    //stopRecord();
                    recordstate = false;
                    break;
                //接起电话
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Toast.makeText(ListenPhoneService.this, "开始录音"+incomingNumber, Toast.LENGTH_SHORT).show();
                    //recordCall();
                    break;
                //电话响铃
                case TelephonyManager.CALL_STATE_RINGING:
                    //静音处理
                    //audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    //查看拦截开关是否开启
                    SharedPreferences sharedPreferences = getSharedPreferences("mysetting", Context.MODE_PRIVATE);
                    interceptState = sharedPreferences.getBoolean("intercept_sp",false);
                    if(judgement(incomingNumber) && interceptState){
                        try{
                            //挂断电话
                            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
                            IBinder binder = (IBinder) method.invoke(null,new Object[] {"phone"});
                            ITelephony telephony = ITelephony.Stub.asInterface(binder);
                            telephony.endCall();
                            Log.e("msg", "endCall");
                            Toast.makeText(ListenPhoneService.this, "已拦截"+incomingNumber, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //记录拦截
                        record(incomingNumber);
                        //状态栏提示
                    }
                    break;
            }
        }
    }
    /**
     * 电话录音
     */
    private void recordCall(){
        Log.d("jereh", "record calling");
        if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            recorder = new MediaRecorder();
            try {
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
                recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 编码方式

                File file = new File("/sdcard/","recorder");
                if(!file.exists()){
                    file.mkdir();
                }
                recorder.setOutputFile( "/sdcard/"+ System.currentTimeMillis() + ".amr");// 存放的位置是放在sd卡recorder目录下
                recorder.setMaxDuration(MAX_LENGTH);
                recorder.prepare();
                recorder.start();
                recording=true;
                Toast.makeText(ListenPhoneService.this, "开始录音"+ file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                Log.d("jereh", "normal");
            } catch (IllegalStateException e) {
                Log.i("jereh", "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
            }catch (IOException e) {
                e.printStackTrace();
                Log.d("jereh", "error");
            }
        }
    }
    /**
     * 停止录音
     */
    private void stopRecord(){
        if(recording){
            recorder.stop();
            recorder.release();
            recording=false;
            Toast.makeText(ListenPhoneService.this, "结束录音", Toast.LENGTH_SHORT).show();
            stopSelf();//停止服务
        }
    }
    //判断是否拦截改号码
    private boolean judgement(String number){
        myDbHelper = new MyDbHelper(ListenPhoneService.this);
        myDbHelper.open();
        Cursor cur = myDbHelper.querData("black");
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            String phonenumber=cur.getString(0);
            if(number.equals(phonenumber)){
                return true;
            }
            cur.moveToNext();
        }
        myDbHelper.close();
        return false;
    }
    //记录拦截
    private void record(String phonenumber){
        boolean bool = false;   //拦截次数判断
        int frequency = 0;
        myDbHelper=new MyDbHelper(ListenPhoneService.this);
        myDbHelper.open();
        Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_WEEK);
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);
        String data=String.valueOf(year)+"/"+String.valueOf(month)+"/"+String.valueOf(day);
        String time=String.valueOf(hour)+":"+String.valueOf(minute);
        Cursor cur=myDbHelper.querData("record");
        cur.moveToFirst();
        while(!cur.isAfterLast()) {    //*****判断是不以拦截过******
            if (phonenumber.equals(cur.getString(0))) {
                frequency = cur.getInt(1);
                frequency++;//统计拦截次数
                bool = true;
                break;//是拦截过
            }
            cur.moveToNext();
        }
        if(bool){    //此号码已拦截过
            myDbHelper.updataData(time,frequency, data, phonenumber, "record");
        }else{
            myDbHelper.insertData(phonenumber, 1, time, data, "record");
        }
        myDbHelper.close();
    }
}
