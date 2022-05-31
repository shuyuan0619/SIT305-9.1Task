package com.example.lostandfound;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.MyViewHolder> {


    private OnItemClickListener item_cl_listener;
    private List<ItemBean> item_bean_list;
    private Context context;
    private DatabaseHelper dbHelper;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout2,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int index = position;
        holder.text_description.setText(item_bean_list.get(position).description);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item_cl_listener.onItemClick(view,index);
            }
        });

    }

    @Override
    public int getItemCount() {
        return item_bean_list.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    public void addItemClickListener(OnItemClickListener listener){
        item_cl_listener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView text_description;
        public LinearLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            text_description = itemView.findViewById(R.id.text_description);
            layout = itemView.findViewById(R.id.linear);
        }
    }

    public ShowAdapter(List<ItemBean> item_bean_list, Context context){
        this.item_bean_list = item_bean_list;
        this.context = context;

    }


}
