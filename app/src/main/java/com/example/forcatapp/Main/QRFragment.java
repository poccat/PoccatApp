package com.example.forcatapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.forcatapp.R;

/*
Activity안에 Fragment로 화면을 그린 경우에는
View view = getView();
createQR = view.findViewById(R.id.createQR);
액티비티내에 부분페이지로 프래그먼트를 사용했을때
프래그먼트에서 액티비티의 버튼이나 콤포넌트를 접근할 때 사용할것.
 */
public class QRFragment extends Fragment {
    private Button scanQR = null;
    private static final String TAG = "MypageFragment";
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        scanQR = view.findViewById(R.id.scanQR);
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScanQrActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
