package com.heeyeon.mymsgapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.heeyeon.mymsgapp.Adapter.BoxAdapter;
import com.heeyeon.mymsgapp.Adapter.CardAdapter;
import com.heeyeon.mymsgapp.Adapter.MessageAdapter;
import com.heeyeon.mymsgapp.Fragments.APIService;
import com.heeyeon.mymsgapp.Model.Box;
import com.heeyeon.mymsgapp.Model.Card;
import com.heeyeon.mymsgapp.Model.Chat;
import com.heeyeon.mymsgapp.Model.User;
import com.heeyeon.mymsgapp.Notification.Client;
import com.heeyeon.mymsgapp.Notification.Data;
import com.heeyeon.mymsgapp.Notification.MyResponse;
import com.heeyeon.mymsgapp.Notification.Sender;
import com.heeyeon.mymsgapp.Notification.Token;

import java.security.cert.PKIXRevocationChecker;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import petrov.kristiyan.colorpicker.ColorPicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.speech.tts.TextToSpeech;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

public class MessageActivity extends AppCompatActivity {

    public static Context mContext;
    static String[] Backgroundlist = new String[]{"burst.json","golden_particle.json","night_background.json","mountain.json","pink_gradient.json","sunrise.json"};
    static String[] Emojilist = new String[]{"emoji_enamorado.json","emoji_guinoo.json","emoji_sorprendido.json","emoji_triste.json","email.json","coffee.json","heart_beat.json","thumbup.json"};
    static HashMap<Integer, Integer> BoxAnim = new HashMap<Integer, Integer>(){
        {
            put(0,R.anim.fadein);
            put(1,R.anim.fadeout);
            put(2,R.anim.blink);
            put(3,R.anim.bounce);
            put(4,R.anim.zoomin);
            put(5,R.anim.zoomout);
            put(6,R.anim.slide_down);
            put(7,R.anim.move);
            put(8,R.anim.rotate);

        }
    };
    static int[] ColorResID = new int[]{
            R.color.color1, R.color.color2,R.color.color3,R.color.color4,R.color.color5,R.color.color6,R.color.color7,R.color.color8,R.color.color9,R.color.color10,R.color.color11,R.color.color12,R.color.color13,R.color.color14,R.color.color15
    };
    ArrayList<String> colors;
    CircleImageView profile_image;
    TextView username;
    String userid;
    APIService apiService;
    boolean isnotify=false;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    ArrayList<Chat> mChat;

    RecyclerView recyclerView;
    RecyclerView recyclerCardView1,recyclerCardView2,BoxRecyclerView;
    CardAdapter cardAdapter,cardAdapter2;
    BoxAdapter boxAdapter;
    ArrayList<Card> mCard,mCard2;
    ArrayList<Box> BoxCard;


    Intent intent;
    ValueEventListener valueEventListener;
    FrameLayout effectBackground;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2,fab3;
    LottieAnimationView lottieAnimationView,smalllottieAnimationView;

    MultiStateToggleButton effectTog;
    RelativeLayout OptionsBar;
    Card selectedEffectCard, selectedBackCard;
    Box selectedBox;
    CardView selectedEffectCardView, selectedBackCardView, selectedBoxCardView;
    int selectedColor=0;

    Animation ZooomIn;
    private boolean isLongPressd=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mContext = this;

        OptionsBar = findViewById(R.id.OptionsBar); //효과 창
        OptionsBar.setVisibility(View.INVISIBLE);

        effectTog = findViewById(R.id.effect_tog); // 단순효과들
        effectTog.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                if(value==0){
                    recyclerCardView1.setVisibility(View.VISIBLE);//이모지들
                    recyclerCardView2.setVisibility(View.INVISIBLE);
                } else {
                    recyclerCardView1.setVisibility(View.INVISIBLE); // 백그라운드들
                    recyclerCardView2.setVisibility(View.VISIBLE);
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.tool_bar_msg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerCardView1 = findViewById(R.id.cardrecycle1);
        recyclerCardView1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        recyclerCardView1.setLayoutManager(linearLayoutManager2);

        recyclerCardView2 = findViewById(R.id.cardrecycle2);
        recyclerCardView2.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager3.setOrientation(RecyclerView.HORIZONTAL);
        recyclerCardView2.setLayoutManager(linearLayoutManager3);

        BoxRecyclerView = findViewById(R.id.boxcardrecycle);
        BoxRecyclerView .setHasFixedSize(true);
        LinearLayoutManager boxlinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        boxlinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        BoxRecyclerView .setLayoutManager(boxlinearLayoutManager);

        profile_image = findViewById(R.id.profile_picture);
        username = findViewById(R.id.username_msg);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.lottie);
        smalllottieAnimationView = (LottieAnimationView) findViewById(R.id.smalllottie);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab.setOnClickListener(btnClickListener);
        fab1.setOnClickListener(btnClickListener);
        fab2.setOnClickListener(btnClickListener);
        fab3.setOnClickListener(btnClickListener);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        effectBackground = findViewById(R.id.effectbackground);
        effectBackground.setVisibility(View.INVISIBLE);
        //backgroundEffect();

        btn_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isnotify = true;
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                } else {
                    Toast.makeText(MessageActivity.this,"내용을 입력해주세요",Toast.LENGTH_SHORT).show();;
                }
                text_send.setText("");
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.default_profile);
                } else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
                readMessage(firebaseUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessage(userid);
        setCard1();

        ZooomIn = AnimationUtils.loadAnimation(MessageActivity.this, R.anim.lottiezoom);

        colors = new ArrayList<>();  // Color 넣어줄 list

        colors.add("#ffab91");
        colors.add("#F48FB1");
        colors.add("#ce93d8");
        colors.add("#b39ddb");
        colors.add("#9fa8da");
        colors.add("#90caf9");
        colors.add("#81d4fa");
        colors.add("#80deea");
        colors.add("#80cbc4");
        colors.add("#c5e1a5");
        colors.add("#e6ee9c");
        colors.add("#fff59d");
        colors.add("#ffe082");
        colors.add("#ffcc80");
        colors.add("#bcaaa4");

    }

    public void selectEffectCard(Card card, CardView cardView){
        this.selectedEffectCard = card;
        if(selectedEffectCardView!=null){//전에 선택되었던 것
            selectedEffectCardView.setCardBackgroundColor(mContext.getColor(R.color.white));
        }
        isLongPressd = card.getLongPressed();
        card.setLongPressed(false);

        lottieanim(card.getFlag(),card.getImage(),isLongPressd,null,true);
        selectedEffectCardView = cardView;
        if(selectedBackCardView!=null) selectedBackCard.setImage(-1);
        selectedEffectCardView.setCardBackgroundColor(mContext.getColor(R.color.colorPrimary));
    }

    public void setSelectedBackCard(Card card,CardView cardView){
        this.selectedBackCard = card;
        if(selectedBackCardView!=null){//전에 선택되었던 것
            selectedBackCardView.setCardBackgroundColor(mContext.getColor(R.color.white));
        }
        lottieanim(card.getFlag(),card.getImage(),false,null,true);

        selectedBackCardView = cardView;
        if(selectedEffectCard!=null) selectedEffectCard.setImage(-1);
        selectedBackCardView.setCardBackgroundColor(mContext.getColor(R.color.colorPrimary));
     }

    public void setSelectedBoxCard(Box box, CardView cardView){
        this.selectedBox = box;
        int id = box.getId();
        if(selectedBoxCardView!=null){//전에 선택되었던 것
            selectedBoxCardView.setCardBackgroundColor(mContext.getColor(R.color.white));
        }
        selectedBoxCardView = cardView;
        selectedBoxCardView.setCardBackgroundColor(mContext.getColor(R.color.colorPrimary));

    }
    public void boxanim(int id, RelativeLayout total,TextView textView,boolean isRight){
        if(id!=-1) {
            Animation animation= AnimationUtils.loadAnimation(MessageActivity.this, BoxAnim.get(id));
            total.startAnimation(animation);
            boxgradientEffect(textView,isRight);
        }
    }
    public void boxanim(int id, TextView total){
        if(id!=-1) {
            Animation animation= AnimationUtils.loadAnimation(MessageActivity.this, BoxAnim.get(id));
            total.startAnimation(animation);
        }
    }
    public void lottieanim(boolean flag, int order,boolean isLong,TextView textView, boolean isRight) {

        if(order<0){
            if(!flag) {
                lottieAnimationView.setVisibility(View.INVISIBLE);
            }
            else {
                smalllottieAnimationView.clearAnimation();
                smalllottieAnimationView.setVisibility(View.INVISIBLE);
            }
            return;
        }
        if(textView!=null) boxgradientEffect(textView,isRight);
        if(flag){
               if(order<Emojilist.length) {
                   smalllottieAnimationView.setVisibility(View.VISIBLE);
                   lottieAnimationView.setVisibility(View.INVISIBLE);
                   if(isLong) {
                       smalllottieAnimationView.setAnimation(Emojilist[order]);
                       smalllottieAnimationView.playAnimation();
                       smalllottieAnimationView.startAnimation(ZooomIn);
                   }
                   else {
                       smalllottieAnimationView.clearAnimation();
                       smalllottieAnimationView.setAnimation(Emojilist[order]);
                       smalllottieAnimationView.playAnimation();
                   }
               }

        } else {
            smalllottieAnimationView.clearAnimation();
            smalllottieAnimationView.setVisibility(View.INVISIBLE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            if(order<Backgroundlist.length) lottieAnimationView.setAnimation(Backgroundlist[order]);
            lottieAnimationView.playAnimation();
        }
       // lottieAnimationView.setRepeatCount(0);
    }

    public void putEmoticon(int order, @NonNull LottieAnimationView lottieAnimationView){
        if(order<0||order>=Emojilist.length) return;
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.setAnimation(Emojilist[order]);
        lottieAnimationView.playAnimation();
    }

    public void clearBack(){
        smalllottieAnimationView.clearAnimation();
        lottieAnimationView.setVisibility(View.INVISIBLE);
        smalllottieAnimationView.setVisibility(View.INVISIBLE);
    }
    private void boxgradientEffect(@NonNull TextView textView,boolean isRight){
        Handler mHandler = new Handler();
        Drawable original = textView.getBackground();
        Runnable mMyTask = new Runnable() {
             @Override
            public void run() {
                // 원래대로 돌아오기
               // textView.setBackground(original);
                if(isRight) textView.setBackgroundResource(R.drawable.background_right);
                else textView.setBackgroundResource(R.drawable.background_left);
//                if(isRight) textView.setBackground(original);
//                else textView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.background_left));
            }
        };

        mHandler.postDelayed(mMyTask, 4000); // 5초후에 실행
        if(isRight) textView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.gradient_animation));
        else  textView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.keft_gradient_animation));
        AnimationDrawable animDrawable =  (AnimationDrawable) textView.getBackground();
        animDrawable.setEnterFadeDuration(800);
        animDrawable.setExitFadeDuration(1000);
//        animDrawable.setTint(getColor(R.color.colorPrimary));
//        animDrawable.setTintMode(PorterDuff.Mode.OVERLAY);
        animDrawable.setOneShot(true);
        animDrawable.start();
    }

    private void backgroundEffect(){//백그라운드 그라이데이션 커스텀
        Handler mHandler = new Handler();
        effectBackground.setVisibility(View.VISIBLE);

        Runnable mMyTask = new Runnable() {
            @Override
            public void run() {
                // 실제 동작
                effectBackground.setVisibility(View.INVISIBLE);
            }
        };

        mHandler.postDelayed(mMyTask, 5000); // 5초후에 실행

        AnimationDrawable animDrawable =  (AnimationDrawable) effectBackground.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(1200);
//        animDrawable.setTint(getColor(R.color.colorPrimary));
//        animDrawable.setTintMode(PorterDuff.Mode.OVERLAY);
        animDrawable.setOneShot(true);
        animDrawable.start();
    }
    private void seenMessage(final String userid){
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        hashMap.put("boxColor",selectedColor);

        if(selectedEffectCard==null) {
            hashMap.put("effect",-1);
            hashMap.put("effectFlag", false);
        }
        else {
            hashMap.put("effect", selectedEffectCard.getImage());
            hashMap.put("effectFlag", isLongPressd);
        }

        if(selectedBackCard==null) hashMap.put("backeffect",-1);
        else  hashMap.put("backeffect",selectedBackCard.getImage());

        if(selectedBox==null) hashMap.put("boxeffect",-1);
        else hashMap.put("boxeffect",selectedBox.getId());

        databaseReference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final String msg =message;
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(isnotify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                isnotify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, String username, String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher_round,username+": "+msg,"새로운 메시지",
                            userid);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success ==1){
                                         //   Toast.makeText(MessageActivity.this, "Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setCard1(){
        mCard = new ArrayList<>();
        mCard.add(new Card(true,-1));
        mCard.add(new Card(true,0));
        mCard.add(new Card(true,1));
        mCard.add(new Card(true,2));
        mCard.add(new Card(true,3));
        mCard.add(new Card(true,4));
        mCard.add(new Card(true,5));
        mCard.add(new Card(true,6));
        mCard.add(new Card(true,7));
        mCard2 = new ArrayList<>();
        mCard2.add(new Card(false,-1));
        mCard2.add(new Card(false,0));
        mCard2.add(new Card(false,1));
        mCard2.add(new Card(false,2));
        mCard2.add(new Card(false,3));
        mCard2.add(new Card(false,4));
        mCard2.add(new Card(false,5));

        cardAdapter = new CardAdapter(MessageActivity.this, mCard);
        recyclerCardView1.setAdapter(cardAdapter);
        cardAdapter2 = new CardAdapter(MessageActivity.this, mCard2);
        recyclerCardView2.setAdapter(cardAdapter2);

        BoxCard = new ArrayList<>();
        BoxCard.add(new Box("None",-1));
        BoxCard.add(new Box("Fade In",0));
        BoxCard.add(new Box("Fade Out",1));
        BoxCard.add(new Box("Blink",2));
        BoxCard.add(new Box("Bounce",3));
        BoxCard.add(new Box("Zoom In",4));
        BoxCard.add(new Box("Zoom Out",5));
        BoxCard.add(new Box("Slide down",6));
        BoxCard.add(new Box("Move",7));
        BoxCard.add(new Box("Rotate",8));

        boxAdapter = new BoxAdapter(MessageActivity.this, BoxCard);
        BoxRecyclerView.setAdapter(boxAdapter);
    }


    private void readMessage(String myid, String userid, String imageurl){
        mChat = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                        chat.getReceiver().equals(userid)&&chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }

                recyclerView.scrollToPosition(mChat.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void status(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        databaseReference.updateChildren(hashMap);
    }
    private Button.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.fab:
                    anim();

                    break;
                case R.id.fab1:
                    //anim();
                    effectTog.setVisibility(View.INVISIBLE);

                    BoxRecyclerView.setVisibility(View.VISIBLE);
                    recyclerCardView1.setVisibility(View.INVISIBLE);
                    recyclerCardView2.setVisibility(View.INVISIBLE);

                    break;
                case R.id.fab2:
                    //anim();
                    recyclerCardView1.setVisibility(View.VISIBLE);
                    recyclerCardView2.setVisibility(View.INVISIBLE);
                    effectTog.setVisibility(View.VISIBLE);
                    BoxRecyclerView.setVisibility(View.INVISIBLE);

                    effectTog.setValue(0);
                    break;
                case R.id.fab3:

                    openColorPicker();
                    break;
            }
        }
    };

    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);

            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
            fab.setImageResource(R.drawable.magic_wand);
            OptionsBar.setVisibility(View.INVISIBLE);
            BoxRecyclerView.setVisibility(View.INVISIBLE);
            clearBack();
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);

            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);

            isFabOpen = true;
            fab.setImageResource(R.drawable.x);

            OptionsBar.setVisibility(View.VISIBLE);

            effectTog.setVisibility(View.VISIBLE);
            recyclerCardView1.setVisibility(View.VISIBLE);
            recyclerCardView2.setVisibility(View.INVISIBLE);
            BoxRecyclerView.setVisibility(View.INVISIBLE);

            effectTog.setValue(0);
        }
    }
    public void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(this);  // ColorPicker 객체 생성

        colorPicker.setColors(colors)  // 만들어둔 list 적용
                .setColumns(5)  // 5열로 설정
                .setRoundColorButton(true)  // 원형 버튼으로 설정
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        if(position<0||position>=colors.size()) return;
                        selectedColor=position;//[position];

                    }

                    @Override
                    public void onCancel() {
                        // Cancel 버튼 클릭 시 이벤트
                    }
                }).show();  // dialog 생성
    }


    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        databaseReference.removeEventListener(valueEventListener);
    }
}