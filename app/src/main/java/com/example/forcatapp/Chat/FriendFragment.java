package com.example.forcatapp.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FriendFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend,container,false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.friendfragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new com.example.forcatapp.Chat.FriendFragment.FriendFragmentRecyclerViewAdapter());

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.friendfragment_floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(view.getContext(), SelectFriendActivity.class));
            }
        });
        return view;

    }
    class FriendFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<FriendModel> friendModels;


        //아이디가 추가되면 리사이클러뷰에 view아답터 형식으로 아이디 추가하고 새로고침

        public FriendFragmentRecyclerViewAdapter() {
            friendModels= new ArrayList<>();
            final String myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("friends").child(myUid).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    friendModels.clear();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        FriendModel friendModel = snapshot.getValue(FriendModel.class);

                        friendModels.add(friendModel);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//            FirebaseDatabase.getInstance().getReference().child("friends").child(myUid).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    friendModels.clear();
//
//                    final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    for(DataSnapshot snapshot :dataSnapshot.getChildren()){
//
//                        FriendModel friendModel =  snapshot.getValue(FriendModel.class);
//
//                        friendModels.add(friendModel);
//                    }
//                    notifyDataSetChanged();//새로고침
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_list,parent,false);
            return new com.example.forcatapp.Chat.FriendFragment.FriendFragmentRecyclerViewAdapter.CustomViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if(friendModels.size()>0) {
                FirebaseDatabase.getInstance().getReference().child("users").child(friendModels.get(position).friendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            Glide.with
                                    (holder.itemView.getContext())
                                    //이미지넣기
                                    .load(userModel.profileImageUrl.toString())
                                    .apply(new RequestOptions().circleCrop())
                                    .into(((com.example.forcatapp.Chat.FriendFragment.FriendFragmentRecyclerViewAdapter.CustomViewHolder) holder).imageView);
                            //유저이름 넣기
                            ((com.example.forcatapp.Chat.FriendFragment.FriendFragmentRecyclerViewAdapter.CustomViewHolder) holder).textView.setText(userModel.userName);
                            //상태메세지 넣기
                            if (userModel.comment != null) {
                                ((com.example.forcatapp.Chat.FriendFragment.FriendFragmentRecyclerViewAdapter.CustomViewHolder) holder).textView_comment.setText(userModel.comment);
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }


            //이미지랑 유저이름 넣기


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid",friendModels.get(position).friendUid);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendModels.size();
        }
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView textView_comment;

            public CustomViewHolder(View view) {
                super(view);
                imageView=(ImageView) view.findViewById(R.id.friendlistitem_imageview);
                textView=(TextView) view.findViewById(R.id.friendlistitem_textview);
                textView_comment = (TextView)view.findViewById(R.id.friendlistitem_textview_comment);

            }
        }
    }
}