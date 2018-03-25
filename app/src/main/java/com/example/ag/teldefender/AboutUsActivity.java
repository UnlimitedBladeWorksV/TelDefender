package com.example.ag.teldefender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by Ag on 2017/6/21.
 */

public class AboutUsActivity extends AppCompatActivity {
    TextView email_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        email_tv = (TextView) findViewById(R.id.email);
        //状态栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.logo);
        setSupportActionBar(toolbar);
        email_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, "591263992@qq.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "title");
                intent.putExtra(Intent.EXTRA_TEXT, "content");
                startActivity(Intent.createChooser(intent, "Sending..."));
            }
        });
    }
}