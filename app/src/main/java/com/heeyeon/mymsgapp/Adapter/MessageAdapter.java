package com.heeyeon.mymsgapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.show_message.setText(chat.getMessage());
        if(imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.default_profile);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }
        holder.show_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int effect = chat.getEffect();
                int backeffect = chat.getBackeffect();
                int boxeffect = chat.getBoxeffect();
                if(backeffect==-1) ((MessageActivity) MessageActivity.mContext).lottieanim(true,effect);
                else if(effect==-1) ((MessageActivity) MessageActivity.mContext).lottieanim(false,backeffect);

                ((MessageActivity) MessageActivity.mContext).boxanim(boxeffect,holder.total);

            }
        });
        if(position == mChat.size()-1){
            if(chat.isIsseen()){
                holder.txt_seen.setText("읽음");
            } else{
                holder.txt_seen.setText("안읽음");
            }
        } else{
            holder.txt_seen.setVisibility(View.GONE);
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

    public ViewHolder(@NonNull  View itemView){
            super(itemView);
            show_message = itemView.findViewById(R.id.show_msg);
            profile_image=itemView.findViewById(R.id.profile_picture);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            total = itemView.findViewById(R.id.total);
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
