package com.example.wenzty.notedemo;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.wenzty.notedemo.R.id.btn_cancel;
import static com.example.wenzty.notedemo.R.id.btn_ok;

/**
 * Created by wenzty on 2017/6/10.
 */

public class noteEdit extends Activity{
    private TextView tv_date;
    private EditText et_content;
    private TextView mTv_ok;
    private TextView mTv_list;
    private Notesdb mNotesdb;
    private SQLiteDatabase dbread;
    public static int ENTER_STATE = 0;
    public static String last_content;
    public static int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit);

       /* tv_date = (TextView) findViewById(R.id.tv_date);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = sdf.format(date);
        tv_date.setText(dateString);*/

        et_content = (EditText) findViewById(R.id.et_content);
        // 设置软键盘自动弹出
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        mNotesdb = new Notesdb(this);
        dbread = mNotesdb.getReadableDatabase();

        Bundle myBundle = this.getIntent().getExtras();
        last_content = myBundle.getString("info");
       // Log.d("LAST_CONTENT", last_content);
        et_content.setText(last_content);
        // 确认按钮的点击事件
        mTv_ok = (TextView) findViewById(btn_ok);
        mTv_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // 获取日志内容
                String content = et_content.getText().toString();
                Log.d("LOG1", content);
                // 获取写日志时间
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateNum = sdf.format(date);
                String sql;
                String sql_count = "SELECT COUNT(*) FROM note";
                SQLiteStatement statement = dbread.compileStatement(sql_count);
                long count = statement.simpleQueryForLong();
                Log.d("COUNT", count + "");
                Log.d("ENTER_STATE", ENTER_STATE + "");
                // 添加一个新的日志
                if (ENTER_STATE == 0) {
                    if (!content.equals("")) {
                        sql = "insert into " + Notesdb.TABLE_NAME_NOTES
                                + " values(" + count + "," + "'" + content
                                + "'" + "," + "'" + dateNum + "')";
                        Log.d("LOG", sql);
                        dbread.execSQL(sql);
                    }
                }
                // 查看并修改一个已有的日志
                else {
                    Log.d("执行命令", "执行了该函数");
                    String updatesql = "update note set content='"
                            + content + "' where _id=" + id;
                    dbread.execSQL(updatesql);
                    // et_content.setText(last_content);
                }
                Intent data = new Intent();
                setResult(2, data);
                finish();
            }
        });
        mTv_list = (TextView) findViewById(btn_cancel);
        mTv_list.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


    }
}
