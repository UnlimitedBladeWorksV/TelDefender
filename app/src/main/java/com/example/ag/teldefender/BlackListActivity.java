package com.example.ag.teldefender;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
 * Created by Ag on 2017/6/16.
 * 该Activity实现黑名单的管理
 */

public class BlackListActivity extends AppCompatActivity {
    private MyDbHelper myDbHelper;          //数据库操作类
    private ListView blacklist_lv;          //LV视图
    private List<BlackListDao> blackList = new ArrayList<>();   // 黑名单列表
    private ImageButton delete_ibt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        //TextView初始化和适配器
        blacklist_lv = (ListView) findViewById(R.id.lv_blacklist);
        delete_ibt = (ImageButton) findViewById(R.id.ibt_bldelete);
        //状态栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.logo);
        setSupportActionBar(toolbar);
        toolbar.setTitle("黑名单管理");
        //toolbar.setSubtitle("Sub title");
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        /*
        //初始化数据库
        myDbHelper = new MyDbHelper(BlackListActivity.this);
        myDbHelper.open();
        //myDbHelper.insertData("123456", "112", "black");
        //读取黑名单内容
        Cursor cur= myDbHelper.querData("black");
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            BlackListDao text=new BlackListDao();
            text.setName(cur.getString(1));
            text.setNum(cur.getString(0));
            blackList.add(text);
            cur.moveToNext();
        }
        myDbHelper.close();
        */
        /*
        //ListView测试
        for(int i=0;i<10;i++){
            BlackListDao stu=new BlackListDao();
            stu.setNum("num"+i);
            stu.setName("name"+i);
            blackList.add(stu);
        }
        */
        initBlackList();
        delete_ibt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new  AlertDialog.Builder(BlackListActivity.this);
                builder.setMessage("确定删除全部吗？");
                builder.setPositiveButton("取消",null);
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDbHelper = new MyDbHelper(BlackListActivity.this);
                        myDbHelper.open();
                        myDbHelper.clearData("black");
                        myDbHelper.close();
                        initBlackList();
                        Toast.makeText(BlackListActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initBlackList();
    }

    private void initBlackList(){
        MyAdapter myAdapter = new MyAdapter(blackList,this);
        blacklist_lv.setAdapter(myAdapter);
        List<BlackListDao> list = new ArrayList<>();
        myDbHelper = new MyDbHelper(BlackListActivity.this);
        myDbHelper.open();
        //myDbHelper.insertData("123456", "112", "black");
        //读取黑名单内容
        Cursor cur= myDbHelper.querData("black");
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            BlackListDao text=new BlackListDao();
            text.setName(cur.getString(1));
            text.setNum(cur.getString(0));
            list.add(text);
            cur.moveToNext();
        }
        blackList = list;
        myDbHelper.close();
        myAdapter.UpdateList(blackList);
    }

    //状态栏
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_search:
                    msg += "Click search";
                    break;
                case R.id.action_add:
                    msg += "Click add";
                    Intent intent = new Intent(BlackListActivity.this,UpdateBlackListActivity.class);
                    BlackListActivity.this.startActivity(intent);
                    break;
            }

            if(!msg.equals("")) {
                //Toast.makeText(BlackListActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_black, menu);
        return true;
    }
    /*---start--黑名单ListView适配器---start---*/
    public class MyAdapter extends BaseAdapter {
        private List<BlackListDao> list;
        private LayoutInflater inflater;

        public MyAdapter() {
        }

        public MyAdapter(List<BlackListDao> sList, Context context) {
            this.list = sList;
            this.inflater = LayoutInflater.from(context);
        }

        public void UpdateList(List<BlackListDao> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public BlackListDao getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final BlackListNum blacklistnum;
            if(convertView == null){
                blacklistnum = new BlackListNum();
                //加载布局为一个视图
                convertView = inflater.inflate(R.layout.blacklist_listview, null);
                //在view视图中查找id为image_photo的控件
                //初始化ListView组件
                blacklistnum.num = (TextView) convertView.findViewById(R.id.tv_blacklistnum);
                blacklistnum.name = (TextView) convertView.findViewById(R.id.tv_blacklistname);
                blacklistnum.update =  (Button) convertView.findViewById(R.id.bt_update);
                blacklistnum.delete =  (Button) convertView.findViewById(R.id.bt_delete);
                //适配内容
                final String num = list.get(position).getNum();
                final String name = list.get(position).getName();
                blacklistnum.num.setText(list.get(position).getNum());
                blacklistnum.name.setText(list.get(position).getName());
                //按钮点击事件
                blacklistnum.update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BlackListActivity.this,UpdateBlackListActivity.class);
                        intent.putExtra("number",num);
                        intent.putExtra("name",name);
                        BlackListActivity.this.startActivity(intent);
                    }
                });
                blacklistnum.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new  AlertDialog.Builder(BlackListActivity.this);
                        builder.setMessage("确定删除吗？");
                        builder.setPositiveButton("取消",null);
                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDbHelper = new MyDbHelper(BlackListActivity.this);
                                myDbHelper.open();
                                myDbHelper.deleteData(num,"black");
                                myDbHelper.close();
                                initBlackList();
                                notifyDataSetChanged();
                                Toast.makeText(BlackListActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    }
                });
            }else{
                blacklistnum = (BlackListNum) convertView.getTag();
            }
            return convertView;
        }
        class BlackListNum{
            TextView num,name;
            Button update,delete;
        }
    }
    /*---end--黑名单ListView适配器---end---*/
}
