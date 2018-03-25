package com.example.ag.teldefender;

/**
 * Created by Ag on 2017/6/14.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
    //按钮
    private Button BT_Settings;
    private Button BT_Aboutus;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        //初始化
        BT_Settings = (Button) view.findViewById(R.id.bt_interceptsettings);
        BT_Aboutus = (Button) view.findViewById(R.id.bt_aboutus);
        //监听事件
        BT_Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SettingsActivity.class);
                startActivity(intent);
            }
        });
        BT_Aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AboutUsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
