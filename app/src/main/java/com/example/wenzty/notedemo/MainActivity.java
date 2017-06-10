package com.example.wenzty.notedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {

    private Context mContext;
    private ListView mListView;
    private SimpleAdapter mSimpleAdapter;
    private List<Map<String, Object>> dataList;
    private TextView mTv_New;
    private TextView tv_content;
    private Notesdb mNotesdb;
    private SQLiteDatabase dbread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        tv_content = (TextView) findViewById(R.id.tv_content);
        mListView = (ListView) findViewById(R.id.listView);
        dataList = new ArrayList<Map<String, Object>>();

        mTv_New = (TextView) findViewById(R.id.Tv_newNote);
        mContext = this;
        mTv_New.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteEdit.ENTER_STATE = 0;
                Intent intent = new Intent(mContext,noteEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("no","");
                intent.putExtras(bundle);
                startActivityForResult(intent,1);

            }
        });
        mNotesdb = new Notesdb(this);
        dbread = mNotesdb.getReadableDatabase();
        RefreshList();

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnScrollListener(this);

    }

    private void RefreshList() {
        int size = dataList.size();
        if (size > 0){
            dataList.removeAll(dataList);
            mSimpleAdapter.notifyDataSetChanged();
            mListView.setAdapter(mSimpleAdapter);
        }
        mSimpleAdapter = new SimpleAdapter(this,getData(),R.layout.item,new String[]{"tv_content","tv_date"},
                new int[]{R.id.tv_content,R.id.tv_date});
    }

    private List<Map<String, Object>>  getData() {
        Cursor cursor = dbread.query("note",null,"content!=\"\"", null, null,
                null, null);
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("content"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tv_content", name);
            map.put("tv_date", date);
            dataList.add(map);
        }
        cursor.close();
        return dataList;

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_FLING:
                Log.i("main", "用户在手指离开屏幕之前，由于用力的滑了一下，视图能依靠惯性继续滑动");
            case SCROLL_STATE_IDLE:
                Log.i("main", "视图已经停止滑动");
            case SCROLL_STATE_TOUCH_SCROLL:
                Log.i("main", "手指没有离开屏幕，试图正在滑动");
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        noteEdit.ENTER_STATE = 1;
        String content = mListView.getItemAtPosition(position)+"";
        String content1 = content.substring(content.indexOf("=")+1,content.indexOf(","));
        Log.d("CON",content1);
        Cursor c = dbread.query("note", null,
                "content=" + "'" + content1 + "'", null, null, null, null);
        while (c.moveToNext()) {
            String No = c.getString(c.getColumnIndex("_id"));
            Log.d("TEXT", No);
            Intent myIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("info", content1);
            noteEdit.id = Integer.parseInt(No);
            myIntent.putExtras(bundle);
            myIntent.setClass(MainActivity.this, noteEdit.class);
            startActivityForResult(myIntent, 1);
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final int n=position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除该日志");
        builder.setMessage("确认删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = mListView.getItemAtPosition(n) + "";
                String content1 = content.substring(content.indexOf("=") + 1,
                        content.indexOf(","));
                Cursor c = dbread.query("note", null, "content=" + "'"
                        + content1 + "'", null, null, null, null);
                while (c.moveToNext()) {
                    String id = c.getString(c.getColumnIndex("_id"));
                    String sql_del = "update note set content='' where _id="
                            + id;
                    dbread.execSQL(sql_del);
                    RefreshList();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            RefreshList();
        }
    }
}
