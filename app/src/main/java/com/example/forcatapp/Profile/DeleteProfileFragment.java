package com.example.forcatapp.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.forcatapp.R;

public class DeleteProfileFragment extends Fragment {
    private static final String TAG = "DeleteProfileFragment";
    
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_deleteprofile, container, false);
        return view;
    }
}
