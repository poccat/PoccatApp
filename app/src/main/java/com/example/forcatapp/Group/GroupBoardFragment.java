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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class GroupBoardFragment extends Fragment {
    private static final String TAG = "GroupBoardFragment";
    List<String> groupBoard = new ArrayList<>();
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
    public void setgroupBoardList(List<Map<String, Object>> groupList) {
        this.groupBoardList = groupList;
    }

    class GroupBoardFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public GroupBoardFragmentRecyclerViewAdapter() {
            Log.d(TAG, "GroupBoardFragmentRecyclerViewAdapter: groupBoardList => " + groupBoardList);
            for (int i=0 ; i<groupBoardList.size() ; i++){
                groupBoard.add((String) groupBoardList.get(i).get("GRP_B_TITLE"));
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
            return groupBoardList.size();
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