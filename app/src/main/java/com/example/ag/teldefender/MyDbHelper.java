package com.example.ag.teldefender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ag on 2017/6/14.
 */

public class MyDbHelper {
    private Context mcontext = null;
    //数据库名称
    private static final String DB_NAME = "list.db";
    //表——拦截记录
    private static final String TABLE_RECORD = "create table record(number text primary key, frquency int, time text,day text)";
    //表——黑名单
    private static final String TABLE_BLACK= "create table black(number text primary key, name text,frequency text)";
    //表——拦截信息
        private static final String TABLE_RECORDSMS = "create table sms(number text primary key, body text, frquency int, time text,day text)";
    //数据库版本
    private static final int DB_VERSION=1;
    //
    private SQLiteDatabase sqlitedatabase;
    private MySQLiteOpenHelper mysqliteopen;

    public MyDbHelper(Context context){
        this.mcontext=context;
    }

    private class MySQLiteOpenHelper extends SQLiteOpenHelper {
        //数据库操作
        public MySQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //创建表
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_RECORD);
            db.execSQL(TABLE_BLACK);
            db.execSQL(TABLE_RECORDSMS);
        }

        //删除表
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists record");
            db.execSQL("drop table if exists black");
            db.execSQL("drop table if exists recordsms");
            onCreate(db);
        }
    }

    //打开数据库
    public void open(){
        mysqliteopen = new MySQLiteOpenHelper(mcontext,DB_NAME,null,DB_VERSION);
        sqlitedatabase = mysqliteopen.getWritableDatabase();
    }

    //关闭数据库
    public void close(){
        mysqliteopen.close();
    }

    /*
     *插入操作
     *number
     *name
     *frequency
     *count
     *day
     *num
     *startime
     *smscontent
     *table
     */
    //插入1——number，name——table
    public long insertData(String number,String name,String table){
        ContentValues  v = new ContentValues();
        v.put("number",number);
        v.put("name",name);
        return  sqlitedatabase.insert(table,null,v);
    }
    //插入2——number，name，frequency——table
    public long insertData(String number,String name,String frequency,String table){
        ContentValues v = new ContentValues();
        v.put("number", number);
        v.put("name", name);
        v.put("frequency",frequency );
        return sqlitedatabase.insert(table, null, v);
    }
    //插入3——number，count，time，day——table
    public long insertData(String number,int count,String time,String day,String table){
        ContentValues v=new ContentValues();
        v.put("number", number);
        v.put("frquency", count);
        v.put("time", time);
        v.put("day", day);
        return sqlitedatabase.insert(table, null, v);
    }
    //插入4——number，num，count，starttime，startminute，startminute，smscontent——table
    public long insertData(String number,int num,int count,int startime,int startminute,String smscontent,String table){
        ContentValues v = new ContentValues();
        v.put("number", number);
        v.put("count", count);
        v.put("starthour", startime);
        v.put("startminute", startminute);
        v.put("num", num);
        v.put("context",smscontent);
        return sqlitedatabase.insert(table, null, v);
    }
    //插入5
    public long insertData(String number,String smscontent,String time,String day,String table){
        ContentValues v = new ContentValues();
        v.put("number", number);
        v.put("body", smscontent);
        v.put("time", time);
        v.put("day", day);
        return sqlitedatabase.insert(table, null, v);
    }

    /*
     *更新操作
     */
    //更新1
    public boolean updataData(String number,String name,String table) {
        String str = "update " + table + " set name=? where number=?";
        sqlitedatabase.execSQL(str, new Object[]{name, number});
        return true;
    }
    //更新2
    public void updataData(String number,String context){
        String str="update stranger set context=? where number=?";
        sqlitedatabase.execSQL(str,new Object[]{context,number});
    }
    //更新3
    public void updataData(String number,int count){
        String str="update stranger set count=? where number=?";
        sqlitedatabase.execSQL(str,new Object[]{count,number});
    }
    //更新4
    public void updataData(int nownum,String phone){
        String str="update stranger set num=? where number=?";
        sqlitedatabase.execSQL(str,new Object[]{nownum,phone});
    }
    //更新5
    public void updataData(String time,String data,String phone,String table){
        String str="update "+table+"  set time=? , day=? where number=?";
        sqlitedatabase.execSQL(str, new Object[]{time,data,phone});
    }
    //更新6
    public void updataData(String time,int frequency,String data,String phone,String table){
        String str="update "+table+"  set frquency=? , time=? , day=? where number=?";
        sqlitedatabase.execSQL(str, new Object[]{frequency,time,data,phone});
    }

    /*
     *删除操作
     */
    //删除1
    public void deleteData(String number,String table){
        String str="delete from "+table+" where number=?";
        sqlitedatabase.execSQL(str,new Object[]{number});
    }
    //删除2
    public void deleteData(int num){
        String str="delete from stranger where num=?";
        sqlitedatabase.execSQL(str,new Object[]{num});
    }

    //清空数据库指定表
    public void clearData(String table){
        String str="delete from "+table;
        sqlitedatabase.execSQL(str);
    }

    //查询数据
    public Cursor querData(String table){
        Cursor cur=null;
        cur=sqlitedatabase.rawQuery("select * from "+table, null);
        return cur;
    }
}
