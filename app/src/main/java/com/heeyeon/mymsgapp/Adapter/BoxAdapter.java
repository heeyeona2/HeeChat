package com.heeyeon.mymsgapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.heeyeon.mymsgapp.MessageActivity;
import com.heeyeon.mymsgapp.Model.Box;

import com.heeyeon.mymsgapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.ViewHolder> {
    private Context mContext;
    private List<Box> mBox;


    public BoxAdapter(Context mContext, List<Box> mBox) {
        this.mContext = mContext;
        this.mBox = mBox;
    }

    @NonNull
    @Override
    public BoxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardlist2,parent,false);
        return new BoxAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoxAdapter.ViewHolder holder, int position) {
        Box box = mBox.get(position);
        holder.textview.setText(box.getText());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ((MessageActivity) MessageActivity.mContext).setSelectedBoxCard(box,holder.cardView);
                ((MessageActivity) MessageActivity.mContext).boxanim(box.getId(),holder.textview);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBox.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView textview;
        public CardView cardView;


        public ViewHolder(@NonNull  View itemView){
            super(itemView);
            textview=itemView.findViewById(R.id.textview);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }


}


