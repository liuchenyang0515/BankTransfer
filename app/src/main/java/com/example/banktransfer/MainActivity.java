package com.example.banktransfer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private MyOpenHelper helper;
    private EditText editText1, editText2, editText3;
    private String table = "info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new MyOpenHelper(this, table, null, 1);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
    }

    public String queryColumn(Cursor cursor, String s) {
        String ss = null;
        if (cursor.moveToFirst()) { // 必须moveToFirst（）否则异常
            ss = cursor.getString(cursor.getColumnIndex(s));
        }
        return ss;
    }

    public void onclick(View view) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String name1 = editText1.getText().toString().trim();
        String name2 = editText2.getText().toString().trim();
        String str = editText3.getText().toString().trim();
        // 使用事务进行转账
        db.beginTransaction(); // 开启事务
        try {
            Cursor cursor = db.query(table, new String[]{"money"}, "name = ?",
                    new String[]{name1}, null, null, null);
            int money = Integer.valueOf(queryColumn(cursor, "money"));
            // 实现转账的逻辑，实际就是写sql语句
            //db.execSQL("update info set money = money - ? where name = ?", new Object[]{str, name1});
            ContentValues values = new ContentValues();
            int remain = money - Integer.valueOf(str);
            if (remain < 0) {
                Toast.makeText(this, "您的余额不足，转账失败", Toast.LENGTH_SHORT).show();
                return;
            }
            values.put("money", remain + "");
            db.update(table, values, "name = ?", new String[]{name1});

            // int i = 9 / 0; // 让事务回滚示例

            // db.execSQL("update info set money = money + ? where name = ?", new Object[]{str, name2});
            cursor = db.query(table, new String[]{"money"}, "name = ?",
                    new String[]{name2}, null, null, null);
            int money1 = Integer.valueOf(queryColumn(cursor, "money"));
            ContentValues values1 = new ContentValues();
            int remain1 = money1 + Integer.valueOf(str);
            if (remain1 < 0) {
                return;
            }
            values1.put("money", remain1 + "");
            db.update(table, values1, "name = ?", new String[]{name2});

            // 转账之后的cursor
            cursor = db.query(table, new String[]{"money"}, "name = ?",
                    new String[]{name1}, null, null, null);
            String query1 = queryColumn(cursor, "money");
            cursor = db.query(table, new String[]{"money"}, "name = ?",
                    new String[]{name2}, null, null, null);
            String query2 = queryColumn(cursor, "money");
            cursor.close();
            Log.d(TAG, name1 + "账户余额:" + query1 + "\n");
            Log.d(TAG, name2 + "账户余额:" + query2 + "\n");
            Toast.makeText(this, name1 + "账户余额:" + query1 + "\n" + name2 + "账户余额:" + query2, Toast.LENGTH_LONG).show();
            // 给当前事务设置一个成功的标记
            db.setTransactionSuccessful();
        } catch (Exception e) { // 有catch不至于程序崩溃
            Toast.makeText(this, "服务器忙，请稍后再试", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction(); // 关闭事务，如果未执行setTransactionSuccessful，则回滚
        }
    }
}
