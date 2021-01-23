package com.example.forcatapp.Group;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.forcatapp.R;
import com.example.forcatapp.util.OracleDBUpload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class GroupBoardFragment extends Fragment {
    private static final String TAG = "GroupBoardFragment";
    List<Map<String, Object>> groupBoardList;

    public static GroupBoardFragment newInstance() {
        return new GroupBoardFragment();
    }
    public GroupBoardFragment() {
        // Required empty public constructor
    }

    private String mGrpNo;
    public void setGrpNo(String grpNo)
    {
        this.mGrpNo = grpNo;
        Log.d(TAG, "onCreateView: setGrpNo" + grpNo);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /// Bundle extras = getArguments();
        // String grpNo = extras.getBundle("grp_no").toString();
        // setGrpNo(grpNo);
        try {
            getgroupBoardList();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        View view = inflater.inflate(R.layout.fragment_group_board, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.group_board_fragment_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new GroupBoardFragmentRecyclerViewAdapter());

        return view;
    }

    public void setgroupBoardList(List<Map<String, Object>> groupList) {
        this.groupBoardList = groupList;
    }
    class GroupBoardFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public GroupBoardFragmentRecyclerViewAdapter() {
            Log.d(TAG, "GroupBoardFragmentRecyclerViewAdapter: groupBoardList => " + groupBoardList);

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_board,parent,false);
            return new GroupBoardFragment.GroupBoardFragmentRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder) holder).group_board_title.setText(groupBoardList.get(position).get("GRP_B_TITLE").toString());
            ((CustomViewHolder) holder).group_board_cnt.setText(groupBoardList.get(position).get("GRP_B_CNT").toString());
            //grp_b_no, grp_no,grp_b_title,grp_b_cnt,grp_b_type,grp_b_date

        }

        @Override
        public int getItemCount() {
            return groupBoardList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public TextView group_board_title;
            public TextView group_board_cnt;
            public CustomViewHolder(View view){
                super(view);
                group_board_title = (TextView) view.findViewById(R.id.item_group_list_board_title);
                group_board_cnt = (TextView) view.findViewById(R.id.item_group_list_board_cnt);
            }

        }
    }
    //db연동
    public void getgroupBoardList() throws ExecutionException, InterruptedException {
        // 해당 그룹의 번호를 맵에 담기
        Map<String, Object> pMap = new HashMap<>();
        pMap.put("grp_no", mGrpNo);

        //member/group_board_list.foc 로 전송
        OracleDBUpload oracleDBUpload = new OracleDBUpload("getGroupBoardList", getContext());
        oracleDBUpload.execute(pMap);
        oracleDBUpload.get();

        // 결과물 List로 반환
        groupBoardList = oracleDBUpload.resultList;
        Log.d(TAG, "getgroupBoardList: " + groupBoardList);

        setgroupBoardList(groupBoardList);
    }
}