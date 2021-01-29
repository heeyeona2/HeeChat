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
import com.heeyeon.mymsgapp.Model.Card;

import com.heeyeon.mymsgapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private Context mContext;
    private List<Card> mCard;
    HashMap<Integer,Integer> map1 = new HashMap<Integer, Integer>(){
        {
            put(1,R.raw.emoji_enamorado);
            put(2,R.raw.emoji_guino);
            put(3,R.raw.emoji_sorprendido);
            put(4,R.raw.emoji_triste);
            put(5,R.raw.email);
            put(6,R.raw.coffee);
            put(7,R.raw.heart_beat);
            put(8,R.raw.thumbup);
        }
    };
    HashMap<Integer,Integer> map2 = new HashMap<Integer, Integer>(){
        {
            put(1,R.raw.burst);
            put(2,R.raw.golden_particle);
            put(3,R.raw.night_background);
            put(4,R.raw.mountain);
            put(5,R.raw.pink_gradient);
            put(6,R.raw.sunrise);

        }
    };
    public CardAdapter(Context mContext, List<Card> mCard) {
        this.mContext = mContext;
        this.mCard = mCard;
    }

    @NonNull
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardlist1,parent,false);
        return new CardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.ViewHolder holder, int position) {
        Card card = mCard.get(position);
        if(card.getFlag()){//이모지
            if(card.getImage()!=0)
                Glide.with(mContext).asGif().load(map1.get(card.getImage()))
                    .diskCacheStrategy( DiskCacheStrategy.RESOURCE).into(holder.gifview);
            else holder.cardView.setCardBackgroundColor(((MessageActivity) MessageActivity.mContext).getColor(R.color.white));
        } else {//백그라운드
            if(card.getImage()!=0)
                Glide.with(mContext).asGif().load(map2.get(card.getImage()))
                    .diskCacheStrategy( DiskCacheStrategy.RESOURCE).into(holder.gifview);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(card.getFlag()) ((MessageActivity) MessageActivity.mContext).selectEffectCard(card,holder.cardView);
                else ((MessageActivity) MessageActivity.mContext).setSelectedBackCard(card,holder.cardView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCard.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
         public ImageView gifview;
         public CardView cardView;


        public ViewHolder(@NonNull  View itemView){
            super(itemView);
            gifview=itemView.findViewById(R.id.gifview);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }




}


