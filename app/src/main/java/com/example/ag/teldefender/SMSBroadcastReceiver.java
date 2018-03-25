package com.example.ag.teldefender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by Ag on 2017/6/18.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver{
    String number="";
    String body="";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle=intent.getExtras();
            if(bundle!=null){
                Object[]myobject=(Object[]) bundle.get("pdus");
                SmsMessage[]messages=new SmsMessage[myobject.length];
                for(int i=0;i<myobject.length;i++){
                    messages[i]=SmsMessage.createFromPdu((byte[]) myobject[i]);
                }
                StringBuilder n=new StringBuilder();
                StringBuilder b=new StringBuilder();
                for(SmsMessage t:messages){
                    n.append(t.getDisplayOriginatingAddress());
                    b.append(t.getDisplayMessageBody());
                }
                //Toast.makeText(context, n.toString()+b.toString(), Toast.LENGTH_SHORT).show();
                number = n.toString();
                body = b.toString();
            }
            Intent tent=new Intent(context,ListenSMSService.class);
            tent.putExtra("number", number);
            tent.putExtra("body", body);
            context.startService(tent);
            abortBroadcast();
        }
    }
}

