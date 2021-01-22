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
        List<Map<String, Object>> groupList;

        public GroupListFragment() {
            // Required empty public constructor
        }
        //0
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_group_list, container, false);

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.group_list_fragment_recycle);
            recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
            recyclerView.setAdapter(new GroupListFragmentRecyclerViewAdapter());
            //5
            return view;
        }

        class GroupListFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
            //1
            public GroupListFragmentRecyclerViewAdapter() {
                try {
                    groupList= getGroupList();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }

            //4
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_list, parent, false);
                return new CustomViewHolder(view);
            }

            //3
            //get Item COunt 수만큼 실행됨 실행될떄마다 position이 0부터 1씩올라감
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((CustomViewHolder) holder).textView.setText(groupList.get(position).get("GRP_NAME").toString());
                String grp_no = groupList.get(position).get("GRP_NO").toString();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(),"a",Toast.LENGTH_LONG);
                        //=============================================group board로 넘어감
                        Log.d(TAG, "onClick: 선택한 그룹번호 :: " + grp_no);
                        GroupBoardFragment groupBoardFragment = GroupBoardFragment.newInstance();
                        groupBoardFragment.setGrpNo(grp_no);
                        ((GroupActivity) getActivity()).replaceFragment(groupBoardFragment);
                    }
                });
            }

            //2
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
        public List<Map<String,Object>> getGroupList() throws ExecutionException, InterruptedException {
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
            List<Map<String,Object>> groupList = oracleDBUpload.resultList;
            Log.d(TAG, "getGroupList: " + groupList);

            return groupList;

        }
    }
