package com.example.forcatapp.Group;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.forcatapp.R;

import java.util.ArrayList;
import java.util.List;


public class GroupBoardFragment extends Fragment {
    List<String> groupBoard = new ArrayList<>();

    public static GroupBoardFragment newInstance() {
        return new GroupBoardFragment();
    }
    public GroupBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list,container,false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.group_list_fragment_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new GroupBoardFragment.GroupBoardFragmentRecyclerViewAdapter());
        return view;
    }
    class GroupBoardFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public GroupBoardFragmentRecyclerViewAdapter(){
            for(int i=1;i<=5;i++){
                groupBoard.add("그룹게시판"+i);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_board,parent,false);
            return new GroupBoardFragment.GroupBoardFragmentRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((GroupBoardFragment.GroupBoardFragmentRecyclerViewAdapter.CustomViewHolder) holder).textView.setText(groupBoard.get(position));
        }

        @Override
        public int getItemCount() {
            return 5;
        }
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public CustomViewHolder(View view){
                super(view);
                textView = (TextView) view.findViewById(R.id.item_group_list_board_title);
            }
        }
    }
}