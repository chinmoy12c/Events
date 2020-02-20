package com.example.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.MyViewHolder> {

    private int NumOfItems = 3;
    private LayoutInflater mInflater;

    public MessagesRecyclerAdapter(Context context){
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MessagesRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View myItem = mInflater.inflate(R.layout.message_item_view,parent,false);

        return new MyViewHolder(myItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesRecyclerAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return NumOfItems;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
