package com.example.ag.teldefender;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.Toast;

/**
 * Created by Ag on 2017/6/15.
 */

public class SettingsActivity extends PreferenceActivity{
    private boolean interceptState = false; //拦截开关

    //PreferenceScreen black_list;
    SwitchPreference switchstate;
    PreferenceScreen bateinfo;
    PreferenceScreen aboutus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        getPreferenceManager().setSharedPreferencesName("mysetting");
        //初始化
        //black_list = (PreferenceScreen) findPreference("blacklist_ps");
        switchstate = (SwitchPreference) findPreference("intercept_sp");
        bateinfo = (PreferenceScreen) findPreference("bata_info");
        aboutus = (PreferenceScreen) findPreference("aboutus_ps");
        initSettings();
        bateinfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(SettingsActivity.this,"test bate 1.0",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        aboutus.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(SettingsActivity.this,"联系我们：591263992@qq.com",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
    public void initSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences("mysetting", Context.MODE_PRIVATE);
        interceptState = sharedPreferences.getBoolean("intercept_sp",false);
        //初始化
        switchstate.setChecked(interceptState);
    }

}
