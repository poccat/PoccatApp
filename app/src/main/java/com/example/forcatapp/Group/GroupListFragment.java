package com.example.forcatapp.Group;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forcatapp.Chat.ChatActivity;
import com.example.forcatapp.Main.MainActivity;
import com.example.forcatapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupListFragment extends Fragment {
    List<String> groupName = new ArrayList<>();

    public GroupListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.group_list_fragment_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new GroupListFragmentRecyclerViewAdapter());
        return view;
    }

    class GroupListFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public GroupListFragmentRecyclerViewAdapter() {
            for (int i = 1; i <= 5; i++) {
                groupName.add("그룹" + i);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_list, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder) holder).textView.setText(groupName.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"a",Toast.LENGTH_LONG);
                    ((GroupActivity) getActivity()).replaceFragment(GroupBoardFragment.newInstance());


                }
            });
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public CustomViewHolder(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.item_group_list_name);
            }
        }

    }
}
