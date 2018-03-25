package com.example.ag.teldefender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InterceptPhoneActivity extends AppCompatActivity {
    private List<InterPhoneListDao> interPhoneList = new ArrayList<>();   // 拦截电话列表
    private MyDbHelper myDbHelper;          //数据库操作类
    private ListView interphonelist_lv;          //LV视图
    private ImageButton delete_ibt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercetptphone);
        interphonelist_lv = (ListView) findViewById(R.id.lv_intercetphonelist);
        delete_ibt = (ImageButton) findViewById(R.id.ibt_ipdelete);
        //状态栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.logo);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("");
        //toolbar.setSubtitle("Sub title");
        //toolbar.setOnMenuItemClickListener(onMenuItemClick);
        //initBlackList();
        delete_ibt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new  AlertDialog.Builder(InterceptPhoneActivity.this);
                builder.setMessage("确定删除全部吗？");
                builder.setPositiveButton("取消",null);
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDbHelper = new MyDbHelper(InterceptPhoneActivity.this);
                        myDbHelper.open();
                        myDbHelper.clearData("record");
                        myDbHelper.close();
                        InterPhoneList();
                        Toast.makeText(InterceptPhoneActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });
        InterPhoneList();
    }

    private void InterPhoneList(){
        MyAdapter myAdapter = new MyAdapter(interPhoneList,this);
        interphonelist_lv.setAdapter(myAdapter);
        List<InterPhoneListDao> list = new ArrayList<>();
        myDbHelper = new MyDbHelper(InterceptPhoneActivity.this);
        myDbHelper.open();
        //myDbHelper.insertData("123456", "112", "black");
        //读取黑名单内容
        Cursor cur= myDbHelper.querData("record");
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            InterPhoneListDao text=new InterPhoneListDao();
            text.setNum(cur.getString(0));
            text.setName(cur.getString(1));
            text.setTime(cur.getString(2));
            text.setData(cur.getString(3));
            list.add(text);
            cur.moveToNext();
        }
        interPhoneList = list;
        myDbHelper.close();
        myAdapter.UpdateList(interPhoneList);
    }

    public void record(){
        //记录测试
        myDbHelper=new MyDbHelper(InterceptPhoneActivity.this);
        myDbHelper.open();
        Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_WEEK);
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);
        String data=String.valueOf(year)+"/"+String.valueOf(month)+"/"+String.valueOf(day);
        String time=String.valueOf(hour)+":"+String.valueOf(minute);
        myDbHelper.insertData("123", 1, time, data, "record");
        myDbHelper.close();
    }
    /*---start--黑名单ListView适配器---start---*/
    public class MyAdapter extends BaseAdapter {
        private List<InterPhoneListDao> list;
        private LayoutInflater inflater;

        public MyAdapter() {
        }

        public MyAdapter(List<InterPhoneListDao> sList, Context context) {
            this.list = sList;
            this.inflater = LayoutInflater.from(context);
        }

        public void UpdateList(List<InterPhoneListDao> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public InterPhoneListDao getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final InterPhoneListNum interPhoneListNum;
            if (convertView == null) {
                interPhoneListNum = new InterPhoneListNum();
                //加载布局为一个视图
                convertView = inflater.inflate(R.layout.interceptphonelist_listview, null);
                InterPhoneListDao student = getItem(position);
                //初始化ListView组件
                interPhoneListNum.num = (TextView) convertView.findViewById(R.id.tv_ipnum);
                interPhoneListNum.name = (TextView) convertView.findViewById(R.id.tv_ipname);
                interPhoneListNum.day = (TextView) convertView.findViewById(R.id.tv_ipdata);
                interPhoneListNum.time = (TextView) convertView.findViewById(R.id.tv_iptime);
                interPhoneListNum.delete = (Button) convertView.findViewById(R.id.bt_ipdelete);
                //适配内容
                final String num = list.get(position).getNum();
                interPhoneListNum.num.setText(list.get(position).getNum());
                interPhoneListNum.name.setText("("+list.get(position).getName()+")");
                interPhoneListNum.day.setText(list.get(position).getData());
                interPhoneListNum.time.setText(list.get(position).getTime());
                interPhoneListNum.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new  AlertDialog.Builder(InterceptPhoneActivity.this);
                        builder.setMessage("确定删除吗？");
                        builder.setPositiveButton("取消",null);
                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDbHelper = new MyDbHelper(InterceptPhoneActivity.this);
                                myDbHelper.open();
                                myDbHelper.deleteData(num,"record");
                                myDbHelper.close();
                                InterPhoneList();
                                notifyDataSetChanged();
                                Toast.makeText(InterceptPhoneActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    }
                });
            } else {
                interPhoneListNum = (InterPhoneListNum) convertView.getTag();
            }
            return convertView;
        }

        class InterPhoneListNum {
            TextView num, name, day, time;
            Button delete;
        }
    }
    /*---end--黑名单ListView适配器---end---*/

}
