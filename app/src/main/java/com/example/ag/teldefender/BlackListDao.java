package com.example.ag.teldefender;

/**
 * Created by Ag on 2017/6/11.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackListDao {
    private String num;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public String getNum() {
        return num;
    }
}
