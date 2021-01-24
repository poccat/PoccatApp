package com.example.forcatapp.Map;


import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.forcatapp.R;


public class DialogFragment extends androidx.fragment.app.DialogFragment {
    String name;
    Uri imgUrl;
    String title, cmt;
    int no;

    public DialogFragment(String name, Uri imgUrl, String title, int no, String cmt) {
        this.name=name;
        this.imgUrl=imgUrl;
        this.title = title;
        this.cmt = cmt;
        this.no = no;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_dialog, null);
        TextView tv_name= view.findViewById(R.id.cat_name);
        ImageView iv_cat= view.findViewById(R.id.iv_cat_photo);
        tv_name.setText(name);
        Glide.with
                (getContext())
                .load(imgUrl)
                .apply(new RequestOptions().circleCrop())
                .into(iv_cat);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("프로필", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("a","a");
                    }
                })
                .setNegativeButton("나가기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();

    }
    public static String TAG = "dialog";
}
