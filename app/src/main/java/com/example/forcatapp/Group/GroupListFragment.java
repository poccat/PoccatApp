package com.example.forcatapp.Group;

import android.content.Context;
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
import com.example.forcatapp.util.OracleDBUpload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class GroupListFragment extends Fragment {
    private static final String TAG = "GroupListFragment";
    List<String> groupName = new ArrayList<>();
    List<Map<String, Object>> groupList;

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        try {
            getGroupList();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.group_list_fragment_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new GroupListFragmentRecyclerViewAdapter());

        return view;
    }

    public void getGroupList() throws ExecutionException, InterruptedException {
        // 현재 접속한 유저의 이메일 받아옴
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String mem_email = user.getEmail();
        Map<String, Object> pMap = new HashMap<>();
        pMap.put("mem_email", mem_email);

        //member/group_mem_mygroup.foc 로 전송
        OracleDBUpload oracleDBUpload = new OracleDBUpload("getGroupList", getContext());
        oracleDBUpload.execute(pMap);
        oracleDBUpload.get();
        // 결과물 List로 반환
        groupList = oracleDBUpload.resultList;
        Log.d(TAG, "getGroupList: " + groupList);

        setGroupList(groupList);
    }

    public void setGroupList(List<Map<String, Object>> groupList) {
        this.groupList = groupList;
    }

    class GroupListFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public GroupListFragmentRecyclerViewAdapter() {
            Log.d(TAG, "GroupListFragmentRecyclerViewAdapter: groupList => " + groupList);
            for (int i=0 ; i<groupList.size() ; i++){
                groupName.add((String) groupList.get(i).get("GRP_NAME"));
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
            return groupList.size();
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
