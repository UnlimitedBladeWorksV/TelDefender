package com.example.ag.teldefender;

/**
 * Created by Ag on 2017/6/13.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class PhoneFragment extends Fragment {
    private boolean interceptState = false; //拦截开关

    TextView interceptState_tv;
    Button blackList_bt,recorder_bt;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_index,container,false);
        //初始化
        interceptState_tv = (TextView) view.findViewById(R.id.tv_interceptstate);
        blackList_bt = (Button) view.findViewById(R.id.bt_indexblacklist);
        recorder_bt = (Button) view.findViewById(R.id.bt_indexrecorder);
        //监听时间
        blackList_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),BlackListActivity.class);
                startActivity(intent);
            }
        });
        recorder_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RecorderActivity.class);
                startActivity(intent);
            }
        });
        initstate();
        interceptState_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mysetting",Context.MODE_PRIVATE);
                if(interceptState_tv.getText().equals("拦截已关闭")){
                    interceptState = true;
                    interceptState_tv.setText("拦截已开启");
                }else{
                    interceptState = false;
                    interceptState_tv.setText("拦截已关闭");
                }
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("intercept_sp", interceptState);
                editor.commit();
            }
        });
        return view;
    }
    public void initstate(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mysetting",Context.MODE_PRIVATE);
        interceptState = sharedPreferences.getBoolean("intercept_sp",false);
        if(interceptState){
            interceptState_tv.setText("拦截已开启");
        }else{
            interceptState_tv.setText("拦截已关闭");
        }
    }
}