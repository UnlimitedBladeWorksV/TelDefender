package com.example.ag.teldefender;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;

/**
 * Created by Ag on 2017/6/14.
 */

public class ListFragment extends Fragment{
    //按钮
    private Button BT_Phone;
    private Button BT_SMS;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list,container,false);
        //初始化
        BT_Phone = (Button) view.findViewById(R.id.bt_interceptphone);
        BT_SMS = (Button) view.findViewById(R.id.bt_interceptsms);
        //监听事件
        BT_Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),InterceptPhoneActivity.class);
                startActivity(intent);
            }
        });
        BT_SMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),InterceptSmsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
