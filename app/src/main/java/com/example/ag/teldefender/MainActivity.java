package com.example.ag.teldefender;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.ag.teldefender.PagerSlidingTabStrip;

public class MainActivity extends AppCompatActivity {

    //首页的Fragment
    private PhoneFragment indexFragment;

    //拦截列表的Fragment
    private ListFragment listFragment;

    //系统设置的Fragment
    private SettingsFragment settingsFragment;

    //实例化PagerSlidingTabStrip
    private PagerSlidingTabStrip tabs;

    //获取屏幕密度
    private DisplayMetrics dm;


    //保存黑名单文件
    private String filePath = "/sdcard/BlackListBackups/";
    private String fileName = "blacklist.txt";
    SaveTxtFile saveTxtFile;

    MyDbHelper myDbHelper;

    AudioRecoderUtils audioRecoderUtils;

    int notifyId = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setOverflowShowingAlways();
        dm = getResources().getDisplayMetrics();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        setTabsValue();

        //状态栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.logo);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("My Title");
        //toolbar.setSubtitle("Sub title");
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        //.setContent(mRemoteViews)
        showButtonNotify();
    }

    //通知栏
    public void showButtonNotify() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_mian);

        BroadcastReceiver onClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.notifications.intent.action.ButtonClick")) {
                    //在这里处理点击事件
                    Intent i = new Intent(MainActivity.this,RecordingActivity.class);
                    startActivity(i);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.notifications.intent.action.ButtonClick");
        registerReceiver(onClickReceiver, filter);

        Intent buttonIntent = new Intent("com.notifications.intent.action.ButtonClick");
        PendingIntent pendButtonIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_nfrecord, pendButtonIntent);
        //R.id.trackname为你要监听按钮的id

        mBuilder.setTicker("拦截系统已启动")
                .setContent(mRemoteViews)
                .setSmallIcon(R.drawable.logo);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify(notifyId, mBuilder.build());
    }
        //状态栏
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_sweep:
                    //黑名单的读取
                    try {
                        File file = new File(filePath+fileName);
                        if (file.isFile() && file.exists()) {       //文件存在的前提
                            InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
                            BufferedReader br = new BufferedReader(isr);
                            String lineTxt = null;
                            myDbHelper = new MyDbHelper(MainActivity.this);
                            myDbHelper.open();
                            while ((lineTxt = br.readLine()) != null) {     //
                                if (!"".equals(lineTxt)) {
                                    String reds = lineTxt;      //java 正则表达式
                                    Log.e("read", "num = " + reds);
                                    myDbHelper.insertData(reds, "0", "black");
                                }
                            }
                            myDbHelper.close();
                            isr.close();
                            br.close();
                            msg ="从/sdcard/BlackListBackups/blacklist.txt读取成功！";
                        }else {
                            Toast.makeText(getApplicationContext(),"can not find file",Toast.LENGTH_SHORT).show();//找不到文件情况下
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.action_share:
                    //分享的实现
                    myDbHelper = new MyDbHelper(MainActivity.this);
                    saveTxtFile = new SaveTxtFile();
                    //创建文件夹和目录
                    saveTxtFile.makeFilePath(filePath, fileName);
                    String strFilePath = filePath+fileName;
                    if(new File(strFilePath).exists()){
                        new File(strFilePath).delete();
                    }
                    File file = new File(strFilePath);
                    try {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //读取黑名单内容
                    myDbHelper.open();
                    Cursor cur= myDbHelper.querData("black");
                    cur.moveToFirst();
                    while(!cur.isAfterLast()){
                        saveTxtFile.writeTxtToFile(cur.getString(0),filePath,fileName);
                        Log.e("read", "num = " + cur.getString(0));
                        cur.moveToNext();
                    }
                    myDbHelper.close();
                    //分享界面
                    Uri fileUri = Uri.fromFile(new File(strFilePath));
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM,fileUri);
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent,"分享黑名单到..."));
                    msg += "分享到黑名单到";
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //对PagerSlidingTabStrip的各项属性进行赋值
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#66cccc"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#66cccc"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "首页", "拦截记录", "系统设置" };

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (indexFragment == null) {
                        indexFragment = new PhoneFragment();
                    }
                    return indexFragment;
                case 1:
                    if (listFragment == null) {
                        listFragment = new ListFragment();
                    }
                    return listFragment;
                case 2:
                    if (settingsFragment == null) {
                        settingsFragment = new SettingsFragment();
                    }
                    return settingsFragment;
                default:
                    return null;
            }
        }
    }


    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}