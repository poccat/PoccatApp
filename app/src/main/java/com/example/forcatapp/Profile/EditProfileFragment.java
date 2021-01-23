package com.example.forcatapp.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.forcatapp.R;
import com.example.forcatapp.util.UniversalImageLoader;

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";
    private ImageView mProfilePhoto;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        mProfilePhoto = view.findViewById(R.id.iv_profile_photo);

        setProfileImage();

        ImageView backArrow = view.findViewById(R.id.common_back);
        backArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;

    }

    private void setProfileImage(){
        String imgURL = "img.favpng.com/16/9/17/cat-whiskers-pictogram-clip-art-illustration-png-favpng-JXWYm3gnsxz49QfU9D07GbiNV.jpg";
        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "https://");
    }
}
