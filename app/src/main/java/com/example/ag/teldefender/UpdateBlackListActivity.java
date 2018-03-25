package com.example.ag.teldefender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ag on 2017/6/16.
 */

public class UpdateBlackListActivity extends AppCompatActivity {

    private MyDbHelper myDbHelper;          //数据库操作类
    EditText num_tv;
    EditText name_tv;
    Button add_bt;
    Button modify_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateblacklist);
        //初始化按钮
        num_tv = (EditText) findViewById(R.id.bt_ublnum);
        name_tv = (EditText) findViewById(R.id.bt_ublname);
        add_bt = (Button) findViewById(R.id.bt_add);
        modify_bt = (Button) findViewById(R.id.bt_modify);


        Intent intent = getIntent();
        String num = intent.getStringExtra("number");
        String name = intent.getStringExtra("name");
        if(num == null){
            modify_bt.setVisibility(View.INVISIBLE);
        }else{
            add_bt.setVisibility(View.INVISIBLE);
            num_tv.setText(num+"（不可修改）");
            num_tv.setKeyListener(null);
            name_tv.setText(name);
        }
        //按钮事件监听
        add_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBlackList();
            }
        });
        modify_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyBlackList();
            }
        });
    }
    private void addBlackList(){
        String number = num_tv.getText().toString();
        String name = name_tv.getText().toString();
        if(!number.equals("")){
            myDbHelper = new MyDbHelper(UpdateBlackListActivity.this);
            myDbHelper.open();
            if(name.equals("")){
                name = "0";
            }
            myDbHelper.insertData(number, name, "black");
            myDbHelper.close();
            Toast.makeText(UpdateBlackListActivity.this, "Add BlackList success.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(UpdateBlackListActivity.this, "Number is null.", Toast.LENGTH_SHORT).show();
        }
    }

    private void modifyBlackList(){
        String number = num_tv.getText().toString();
        String name = name_tv.getText().toString();
        if(!number.equals("")){
            myDbHelper = new MyDbHelper(UpdateBlackListActivity.this);
            myDbHelper.open();
            if(name.equals("")){
                name = "0";
            }
            myDbHelper.updataData(number, name, "black");
            myDbHelper.close();
            Toast.makeText(UpdateBlackListActivity.this, "Modify BlackList success.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(UpdateBlackListActivity.this, "Number is null.", Toast.LENGTH_SHORT).show();
        }
    }
}