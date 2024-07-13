package com.example.wifilocation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class SelfFragment extends Fragment {
    private View view;

    public SelfFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_self, container, false);

        RelativeLayout useHelpBtn = view.findViewById(R.id.usehelp_btn);
        useHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "本APP提供室内导航功能", Toast.LENGTH_SHORT).show();
            }
        });


        RelativeLayout aboutBtn = view.findViewById(R.id.about_btn);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "制作团队：Tian Ruiming, Xin Yuzhe, Rong Hanji, Su Shiq.", Toast.LENGTH_SHORT).show();
            }
        });


        RelativeLayout settingBtn = view.findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "尽情期待", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}