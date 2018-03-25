package com.example.ag.teldefender;

/**
 * Created by Ag on 2017/6/13.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


public class PhoneBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "广播开启", Toast.LENGTH_SHORT).show();
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){ //去电操作

        }else{
            if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){ //去电操作

            }else{
                Intent i=new Intent(context,ListenPhoneService.class);
                //pit.putExtra("mIncomingNumber", mIncomingNumber);
                context.startService(i);
            }
        }
    }
}
