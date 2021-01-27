package com.example.forcatapp.Chat;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.forcatapp.R;
import com.example.forcatapp.model.ChatModel;
import com.example.forcatapp.model.NotificationModel;
import com.example.forcatapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity {

    private String destinationUid;
    private Button button;
    private EditText editText;


    private String uid;
    private String chatRoomUid;

    private RecyclerView recyclerView;


    private UserModel destinationUserModel;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private int peopleCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();//채팅 요구하는 아이디 (단말기에 로그인된 Uid)
        destinationUid = getIntent().getStringExtra("destinationUid");// 채팅 당하는 아이디
        button = (Button)findViewById(R.id.messageActivity_button);
        editText = (EditText)findViewById(R.id.messageActivity_editText);

        recyclerView = (RecyclerView)findViewById(R.id.messageActivity_recyclerview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid,true);
                chatModel.users.put(destinationUid,true);

//push 랜덤방이름 생성
                if(chatRoomUid == null){
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                }
                else{
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid= uid;
                    comment.profileImageUrl=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
                    comment.name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    comment.message = editText.getText().toString();
                    Date date = new Date();
                    String year = String.valueOf(date.getYear()+1900);

                    int monthT = date.getMonth()+1;
                    String month = (monthT<10) ? "0"+monthT : String.valueOf(monthT);

                    int dayT = date.getDate();
                    String day = (dayT<10) ? "0"+dayT : String.valueOf(dayT);

                    int hourT = date.getHours();
                    String hour = (hourT<10) ? "0"+hourT : String.valueOf(hourT);

                    int tM = date.getMinutes();
                    String minute = (tM<10) ? "0"+tM : String.valueOf(tM);

                    String time = year+"."+month+"."+day+" "+hour+":"+minute;
                    comment.timestamp = time;

                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendFcm();
                            editText.setText("");
                        }
                    });

                }

            }
        });
        checkChatRoom();
    }

    void sendFcm(){

        Gson gson = new Gson();
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = editText.getText().toString();
        notificationModel.data.text = editText.getText().toString();
        notificationModel.data.title = userName;



        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AAAAR-jvvt0:APA91bH0LOf1QqiIqhc6vJ7PYhx5ry5FLbXpHhg68FGgCHN6TSdDKe6CRf9byBJGJvajrIHSOW_75lj4_NkyUwug0e_jOKIS94yGYW9jLwACt5R-hR4It4Yei3wVuIXrK-ciEX4MHqnv")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });


    }


    void checkChatRoom(){//채팅방 중복체크
         FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destinationUid) &&chatModel.users.size() ==2){
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(com.example.forcatapp.Chat.MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter(){
            comments = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    destinationUserModel = dataSnapshot.getValue(UserModel.class);
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        void getMessageList(){

         databaseReference =  FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
                        comment_motify.readUsers.put(uid,true);

                        readUsersMap.put(key,comment_motify);
                        comments.add(comment_origin);

                    }
                    if(comments.size()>0) {
                        if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                            FirebaseDatabase.getInstance().getReference().child("chatrooms")
                                    .child(chatRoomUid).child("comments").updateChildren(readUsersMap)
                                    .addOnCompleteListener((task) -> {
                                        notifyDataSetChanged();
                                        recyclerView.scrollToPosition(comments.size() - 1);


                                    });
                        } else {
                            notifyDataSetChanged();
                            recyclerView.scrollToPosition(comments.size() - 1);
                        }
                    }
                    //메세지가 갱신

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
            return new MessageViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder)holder);

            //내가보낸메세지
            if(comments.get(position).uid.equals(uid)){//내가보낸 메세지
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.textView_message.setTextSize(25);
                //나일경우 채팅창에 아이디 안보이게
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(position,messageViewHolder.textView_readCounter_left);
            }

            //상대가 보낸 메세지
            else{
                Glide.with(holder.itemView.getContext())
                        .load(destinationUserModel.profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textView_name.setText(destinationUserModel.userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position,messageViewHolder.textView_readCounter_right);
            }
            messageViewHolder.textView_timestamp.setText(comments.get(position).timestamp.toString());



        }
        void setReadCounter(int position, TextView textView){
            if(peopleCount==0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        peopleCount = users.size();
                        int count = users.size() - comments.get(position).readUsers.size();
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else{
                int count = peopleCount - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }

            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_name;
            public TextView textView_message;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;
            public MessageViewHolder(View view) {
                super(view);
                textView_message = view.findViewById(R.id.messageItem_textView_message);
                textView_name = (TextView)view.findViewById(R.id.messageItem_textView_name);
                imageView_profile = (ImageView)view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = (TextView)view.findViewById(R.id.messageItem_textView_timestamp);
                textView_readCounter_left = (TextView)view.findViewById(R.id.messageItem_textView_readCounter_left);
                textView_readCounter_right = (TextView)view.findViewById(R.id.messageItem_textView_readCounter_right);
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
        finish();
    }
}