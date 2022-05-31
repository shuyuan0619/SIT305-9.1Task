package com.example.lostandfound;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity implements ShowAdapter.OnItemClickListener{

    public List<ItemBean> list;
    public ShowAdapter showAdapter;
    public RecyclerView recucle_view_content;
    private DatabaseHelper dbHelper;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        ActionBar actionBar = getSupportActionBar();
        if(!(actionBar==null))
        {
            actionBar.hide();
        }
        setContentView(R.layout.activity_show);
        recucle_view_content = findViewById(R.id.recucle_view_content);
        list = new ArrayList<>();

        dbHelper = new DatabaseHelper(this,"LocalDatabase.db",null,1);
        getData();

        showAdapter = new ShowAdapter(list,this);
        recucle_view_content.setLayoutManager(new LinearLayoutManager(this));
        recucle_view_content.setItemAnimator(new DefaultItemAnimator());
        showAdapter.addItemClickListener(this);
        recucle_view_content.setAdapter(showAdapter);

    }


    @SuppressLint("Range")
    public void getData(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Item",null,null, null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                ItemBean bean = new ItemBean();
                bean.u_id =cursor.getString(cursor.getColumnIndex("u_id"));
                bean.name =cursor.getString(cursor.getColumnIndex("name"));
                bean.phone =cursor.getString(cursor.getColumnIndex("phone"));
                bean.description =cursor.getString(cursor.getColumnIndex("description"));
                bean.date =cursor.getString(cursor.getColumnIndex("date"));
                bean.location =cursor.getString(cursor.getColumnIndex("location"));
                bean.type =cursor.getString(cursor.getColumnIndex("type"));
                list.add(bean);
            }while(cursor.moveToNext());
        }


    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(ShowActivity.this,DetailActivity.class);
        i.putExtra("u_id",list.get(position).u_id);
        i.putExtra("description",list.get(position).description);
        i.putExtra("date",list.get(position).date);
        i.putExtra("location",list.get(position).location);
        startActivity(i);


    }
}