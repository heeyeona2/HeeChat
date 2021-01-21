package com.heeyeon.mymsgapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.autofill.Dataset;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.heeyeon.mymsgapp.Adapter.ChatAdapter;
import com.heeyeon.mymsgapp.Adapter.UserAdapter;
import com.heeyeon.mymsgapp.Model.Chat;
import com.heeyeon.mymsgapp.Model.Chatlist;
import com.heeyeon.mymsgapp.Model.User;
import com.heeyeon.mymsgapp.Notification.Token;
import com.heeyeon.mymsgapp.R;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;

    private ChatAdapter chatAdapter;
    private ArrayList<User> mUser;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    private List<Chatlist> userList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    userList.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 =new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }


    private void chatList() {
        mUser = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for(Chatlist chatlist : userList){
                        if(user.getId().equals(chatlist.getId())){
                            mUser.add(user);
                        }
                    }
                }
                chatAdapter = new ChatAdapter(getContext(), mUser,true);
                recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//    private void readChats(){
//        mUser = new ArrayList<>();
//
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                mUser.clear();
//                //사용자1
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    User user = snapshot.getValue(User.class);
//                    for(String id:userList){
//                        if(user.getId().equals(id)){
//
//                            assert user != null;
//                            int size= mUser.size();
//                            boolean isNew = true;
//                            if(size!=0){
//                                for(int i=0; i<size; i++){
//                                    User user1 = mUser.get(i);
//                                    if(user.getId().equals(user1.getId())){
//                                        isNew = false;
//                                    }
//                                }
//                                if(isNew) {
//                                    mUser.add(user);
//                                }
//                            }
//                            else {
//                                mUser.add(user);
//                            }
//                        }
//                    }
//                }
//                userAdapter = new UserAdapter(getContext(), mUser,true);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}