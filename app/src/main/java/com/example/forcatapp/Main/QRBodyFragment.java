package com.example.forcatapp.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.forcatapp.R;

/*
Activity의 setContentView가 onCreateView이다.
JFrame - Activity - 단독으로 화면을 그릴 수 있다.
JPanel - Fragment - 단독으로 출력 불가함.
그런데 화면을 부분적으로 갱신해야 된다면
웹에서는 ajax로 처리했지만
안드로이드에서는 ajax사용이 불가함.
그래서 Fragment로 화면을 갱신할 수 있다.

 */
public class QRBodyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrbody, container, false);
        EditText et = view.findViewById(R.id.et_url);
        return view;
    }
}