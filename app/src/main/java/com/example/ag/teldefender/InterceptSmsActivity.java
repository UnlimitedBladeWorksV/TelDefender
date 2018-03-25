package com.example.ag.teldefender;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ag on 2017/6/15.
 */

public class InterceptSmsActivity extends AppCompatActivity {
    private List<InterSMSListDao> interSMSList = new ArrayList<>();   // 拦截信息列表
    private MyDbHelper myDbHelper;          //数据库操作类
    private ListView intersmslist_lv;          //LV视图
    private ImageButton delete_ibt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interceptsms);
        intersmslist_lv = (ListView) findViewById(R.id.lv_intercetsmslist);
        delete_ibt = (ImageButton) findViewById(R.id.ibt_isdelete);
        //状态栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.logo);
        setSupportActionBar(toolbar);
        /*
        //ListView测试
        for(int i=0;i<10;i++){
            InterSMSListDao stu=new InterSMSListDao();
            stu.setNumber("num"+i);
            stu.setBody("name"+i);
            interSMSList.add(stu);
        }
        */
        /*
        myDbHelper = new MyDbHelper(InterceptSmsActivity.this);
        myDbHelper.open();
        myDbHelper.insertData("1111", "1111", "1111","1111","sms");
        //读取黑名单内容
        Cursor cur= myDbHelper.querData("sms");
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            InterSMSListDao text=new InterSMSListDao();
            text.setNumber(cur.getString(0));
            text.setBody(cur.getString(1));
            text.setTime(cur.getString(3));
            text.setData(cur.getString(4));
            interSMSList.add(text);
            cur.moveToNext();
        }
        myDbHelper.close();
        MyAdapter myAdapter = new MyAdapter(interSMSList,this);
        intersmslist_lv.setAdapter(myAdapter);
        */
        delete_ibt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new  AlertDialog.Builder(InterceptSmsActivity.this);
                builder.setMessage("确定删除全部吗？");
                builder.setPositiveButton("取消",null);
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDbHelper = new MyDbHelper(InterceptSmsActivity.this);
                        myDbHelper.open();
                        myDbHelper.clearData("sms");
                        myDbHelper.close();
                        interSmsList();
                        Toast.makeText(InterceptSmsActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });
        interSmsList();
    }
    private void interSmsList(){
        MyAdapter myAdapter = new MyAdapter(interSMSList,this);
        intersmslist_lv.setAdapter(myAdapter);
        List<InterSMSListDao> list = new ArrayList<>();
        myDbHelper = new MyDbHelper(InterceptSmsActivity.this);
        myDbHelper.open();
        //myDbHelper.insertData("123456", "112", "black");
        //读取拦截短信内容
        Cursor cur= myDbHelper.querData("sms");
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            InterSMSListDao text=new InterSMSListDao();
            text.setNumber(cur.getString(0));
            text.setBody(cur.getString(1));
            text.setTime(cur.getString(3));
            text.setData(cur.getString(4));
            list.add(text);
            cur.moveToNext();
        }
        interSMSList = list;
        myDbHelper.close();
        myAdapter.UpdateList(interSMSList);
    }

    /*---start--黑名单ListView适配器---start---*/
    public class MyAdapter extends BaseAdapter {
        private List<InterSMSListDao> list;
        private LayoutInflater inflater;

        public MyAdapter() {
        }

        public MyAdapter(List<InterSMSListDao> sList, Context context) {
            this.list = sList;
            this.inflater = LayoutInflater.from(context);
        }

        public void UpdateList(List<InterSMSListDao> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public InterSMSListDao getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final InterSMSListNum interPhoneListNum;
            if (convertView == null) {
                interPhoneListNum = new InterSMSListNum();
                //加载布局为一个视图
                convertView = inflater.inflate(R.layout.interceptsmslist_listview, null);
                InterSMSListDao student = getItem(position);
                //初始化ListView组件
                interPhoneListNum.num = (TextView) convertView.findViewById(R.id.tv_isnumber);
                interPhoneListNum.body = (TextView) convertView.findViewById(R.id.tv_isbody);
                interPhoneListNum.time = (TextView) convertView.findViewById(R.id.tv_istime);
                interPhoneListNum.day = (TextView) convertView.findViewById(R.id.tv_isdata);
                interPhoneListNum.delete = (Button) convertView.findViewById(R.id.bt_isdelete);
                //适配内容
                final String num = list.get(position).getNumber();
                //final String time = list.get(position).getTime();
                interPhoneListNum.num.setText(list.get(position).getNumber());
                interPhoneListNum.body.setText("("+list.get(position).getBody()+")");
                interPhoneListNum.time.setText(list.get(position).getTime());
                interPhoneListNum.day.setText(list.get(position).getData());
                //删除事件
                interPhoneListNum.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new  AlertDialog.Builder(InterceptSmsActivity.this);
                        builder.setMessage("确定删除吗？");
                        builder.setPositiveButton("取消",null);
                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDbHelper = new MyDbHelper(InterceptSmsActivity.this);
                                myDbHelper.open();
                                myDbHelper.deleteData(num,"sms");
                                myDbHelper.close();
                                interSmsList();
                                notifyDataSetChanged();
                                Toast.makeText(InterceptSmsActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    }
                });
            } else {
                interPhoneListNum = (InterSMSListNum) convertView.getTag();
            }
            return convertView;
        }

        class InterSMSListNum {
            TextView num,body,time,day;
            Button delete;
        }
    }
    /*---end--黑名单ListView适配器---end---*/
}

