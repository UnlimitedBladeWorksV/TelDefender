package com.example.ag.teldefender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Ag on 2017/6/19.
 */

public class RecorderActivity extends AppCompatActivity {
    ArrayList<String> records = new ArrayList<>();
    String path = "storage/sdcard/record/";
    boolean success = false;

    private ListView recorder_lv;
    private ImageButton delete_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        recorder_lv = (ListView) findViewById(R.id.lv_recorder);
        delete_bt = (ImageButton)  findViewById(R.id.ibt_recorderdelete);
        //状态栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.logo);
        setSupportActionBar(toolbar);
        /*测试
        records = GetVideoFileName(path);
        for(int i = 0;i < records.size();i++){
            Log.e("brady", "records = " + records.get(i));
        }
        */
        delete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new  AlertDialog.Builder(RecorderActivity.this);
                builder.setMessage("确定删除全部吗？");
                builder.setPositiveButton("取消",null);
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //deleteAllFiles(new File("/sdcard/BlackListBackups/"));
                        init();
                        Toast.makeText(RecorderActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });
        init();
    }
    private void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    public void init(){
        records = GetVideoFileName(path);   //重新读取
        MyAdapter myAdapter = new MyAdapter(records,this);
        recorder_lv.setAdapter(myAdapter);
    }

    public static ArrayList<String> GetVideoFileName(String fileAbsolutePath) {
        ArrayList<String> vecFile = new ArrayList<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // 判断是否为amr结尾
                if (filename.trim().toLowerCase().endsWith(".amr")) {
                    vecFile.add(filename);
                    Log.e("brady", "records = " + filename);
                }
            }
        }
        return vecFile;
    }
    public class MyAdapter extends BaseAdapter {
        private ArrayList<String> list;
        private LayoutInflater inflater;

        public MyAdapter() {
        }

        public MyAdapter(ArrayList<String> sList, Context context) {
            this.list = sList;
            this.inflater = LayoutInflater.from(context);
        }

        public void UpdateList(ArrayList<String> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Recorder recorder;
            if(convertView == null){
                recorder = new Recorder();
                //加载布局为一个视图
                convertView = inflater.inflate(R.layout.recorder_listview, null);
                //初始化
                recorder.record = (TextView) convertView.findViewById(R.id.tv_recorder);
                recorder.play = (Button) convertView.findViewById(R.id.bt_recorderplay);
                recorder.delete = (Button) convertView.findViewById(R.id.bt_recorderdelete);
                //适配内容
                final String strrecord = list.get(position);
                recorder.record.setText(list.get(position));
                //事件监听
                recorder.play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File audioFile = new File("storage/sdcard/record/",strrecord);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + audioFile), "audio/amr");
                        startActivity(intent);
                    }
                });
                recorder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new  AlertDialog.Builder(RecorderActivity.this);
                        builder.setMessage("确定删除吗？");
                        builder.setPositiveButton("取消",null);
                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File("storage/sdcard/record/",strrecord);
                                if(file.exists()){
                                    file.delete();
                                }
                                init();
                                Toast.makeText(RecorderActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    }
                });
            }else{
                recorder = (Recorder) convertView.getTag();
            }
            return convertView;
        }
        class Recorder{
            TextView record;
            Button play,delete;
        }
    }
    /*---end--黑名单ListView适配器---end---*/
}
