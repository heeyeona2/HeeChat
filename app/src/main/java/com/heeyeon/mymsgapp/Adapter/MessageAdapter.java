package com.heeyeon.mymsgapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heeyeon.mymsgapp.MessageActivity;
import com.heeyeon.mymsgapp.Model.Chat;
import com.heeyeon.mymsgapp.Model.User;
import com.heeyeon.mymsgapp.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT =0;
    public static final int MSG_TYPE_RIGHT=1;

    private Context mContext;
    private ArrayList<Chat> mChat;
    private String imageurl;

    FirebaseUser firebaseUser;
    String[] colorList = new String[]{
            "#ffab91","#F48FB1", "#ce93d8", "#b39ddb", "#9fa8da", "#90caf9", "#81d4fa", "#80deea","#80cbc4", "#c5e1a5",
            "#e6ee9c","#fff59d","#ffe082","#ffcc80","#bcaaa4"};
    public MessageAdapter(Context context, ArrayList<Chat> chatArrayList,String imageurl) {
        this.mContext = context;
        this.mChat = chatArrayList;
        this.imageurl = imageurl;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if(viewType == MSG_TYPE_RIGHT){
           View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
           return new MessageAdapter.ViewHolder(view);
       } else {
           View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
           return new MessageAdapter.ViewHolder(view);
       }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        int effect = chat.getEffect();
        int backeffect = chat.getBackeffect();
        int boxeffect = chat.getBoxeffect();
        boolean effectFlag = chat.getEffectFlag();
        int boxColor = chat.getBoxColor();

        int tmp = getItemViewType(position);
        boolean isRight = false;
        if(tmp==1) isRight = true;

        holder.show_message.getBackground().setColorFilter(Color.parseColor(colorList[boxColor]), PorterDuff.Mode.SRC_IN);

        holder.playStatus.setVisibility(View.VISIBLE);
        holder.emoticonView.setVisibility(View.GONE);
        if(effect==-1&&backeffect==-1&&boxeffect==-1) holder.playStatus.setVisibility(View.GONE);
        if(effect!=-1 && !effectFlag){//이모티콘
            holder.playStatus.setVisibility(View.GONE);//굳이 재생버튼 안보여도 되니깐
            ((MessageActivity) MessageActivity.mContext).putEmoticon(effect,holder.emoticonView);//이모티콘
        }
        if(position==mChat.size()-1) {//마지막 읽은 문자의 효과를 재생
            if(effect!=-1) {
                if (effectFlag)
                    ((MessageActivity) MessageActivity.mContext).lottieanim(true, effect, chat.getEffectFlag(),holder.show_message,isRight);//백그라운드
                else
                    ((MessageActivity) MessageActivity.mContext).putEmoticon(effect, holder.emoticonView);//이모티콘
            }
            else if (backeffect!=-1) ((MessageActivity) MessageActivity.mContext).lottieanim(false, backeffect,chat.getEffectFlag(),holder.show_message,isRight );
            else ((MessageActivity) MessageActivity.mContext).clearBack();
            ((MessageActivity) MessageActivity.mContext).boxanim(boxeffect,holder.total,holder.show_message,isRight);
        }

        holder.show_message.setText(chat.getMessage());

        if(imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.default_profile);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        holder.show_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tmp = getItemViewType(position);
                boolean isRight = false;
                if(tmp==1) isRight = true;

                 if(effect!=-1) {
                     if (effectFlag)
                         ((MessageActivity) MessageActivity.mContext).lottieanim(true, effect, chat.getEffectFlag(),holder.show_message,isRight);//백그라운드
                     else
                         ((MessageActivity) MessageActivity.mContext).putEmoticon(effect, holder.emoticonView);//이모티콘
                 }
                 else if (backeffect!=-1) ((MessageActivity) MessageActivity.mContext).lottieanim(false,backeffect,chat.getEffectFlag(),holder.show_message,isRight);
                 if(effect==-1&&backeffect==-1) ((MessageActivity) MessageActivity.mContext).clearBack();

                 ((MessageActivity) MessageActivity.mContext).boxanim(boxeffect,holder.total,holder.show_message,isRight);

            }
        });


            if(chat.isIsseen()){
                holder.txt_seen.setText("읽음");
            } else{
                holder.txt_seen.setText("안읽음");
            }


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

//----------메세지 + 프로필 틀 홀더 클래스----------
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public RelativeLayout total;
        public ImageView playStatus;
        public LottieAnimationView emoticonView;

    public ViewHolder(@NonNull  View itemView){
            super(itemView);
            show_message = itemView.findViewById(R.id.show_msg);
            profile_image=itemView.findViewById(R.id.profile_picture);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            total = itemView.findViewById(R.id.total);
            playStatus = itemView.findViewById(R.id.playstatus);
            emoticonView = itemView.findViewById(R.id.emoticon);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
