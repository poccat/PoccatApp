package com.example.forcatapp.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.forcatapp.R;
import com.example.forcatapp.model.FriendModel;
import com.example.forcatapp.model.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people,container,false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.peoplefragment_floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(view.getContext(),SelectFriendActivity.class));
            }
        });
        return view;

    }
    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<UserModel> userModels;
        //아이디가 추가되면 리사이클러뷰에 view아답터 형식으로 아이디 추가하고 새로고침
        public PeopleFragmentRecyclerViewAdapter() {
            userModels= new ArrayList<>();
            //값을 뺴올떄 씀
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userModels.clear();
                   final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    for(DataSnapshot snapshot :dataSnapshot.getChildren()){

                        UserModel userModel =  snapshot.getValue(UserModel.class);

                        //내아이디가 목록에 안담김
                        if(userModel.uid.equals(myUid)){
                            continue;
                        }
                        userModels.add(userModel);
                    }
                    notifyDataSetChanged();//새로고침
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,parent,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            //이미지 넣기


            Glide.with
                    (holder.itemView.getContext())
                    .load(userModels.get(position).profileImageUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(userModels.get(position).userName);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ///이미 추가한 회원이면
                    String myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String friendUid=userModels.get(position).uid;
                    FirebaseDatabase.getInstance().getReference().child("friends").child(myUid).child(friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //이미 친구추가된 회원이면
                            if(snapshot.exists()){
                                Toast.makeText(getContext(), " 이미추가된 회원", Toast.LENGTH_SHORT).show();

                            }
                            //친구가 아니면
                            else{
                                FriendModel friendModel = new FriendModel();
                                friendModel.friendUid=friendUid;
                                FirebaseDatabase.getInstance().getReference().child("friends").child(myUid).child(userModels.get(position).uid).setValue(friendModel);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });









                   // Intent intent = new Intent(view.getContext(), MessageActivity.class);
                   // intent.putExtra("destinationUid",userModels.get(position).uid);
                   // startActivity(intent);
                }
            });
            if(userModels.get(position).comment!=null){
                ((CustomViewHolder)holder).textView_comment.setText(userModels.get(position).comment);
            }
        }


        @Override
        public int getItemCount() {
            return userModels.size();
        }
        private class CustomViewHolder extends RecyclerView.ViewHolder {
           public ImageView imageView;
           public TextView textView;
           public TextView textView_comment;

            public CustomViewHolder(View view) {
                super(view);
                imageView=(ImageView) view.findViewById(R.id.user_imageview);
                textView=(TextView) view.findViewById(R.id.user_textview);
                textView_comment = (TextView)view.findViewById(R.id.user_textview_comment);

            }
        }
    }
}
