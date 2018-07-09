package com.example.banktransfer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyOpenHelper extends SQLiteOpenHelper {
    private String TAG = "MyOpenHelper";
    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 当数据库第一次创建时调用，特别适合用于表的初始化
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "数据库被创建了，onCreate里面开始建表 ");
        db.execSQL("create table info (_id integer primary key autoincrement, name varchar(20), phone varchar(20), money varchar(20))");
        db.execSQL("insert into info ('name', 'phone', 'money') values('zhangsan', '138888', '2000')");
        db.execSQL("insert into info ('name', 'phone', 'money') values('lisi', '139999', '4000')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists info");
        onCreate(db);
    }
}
