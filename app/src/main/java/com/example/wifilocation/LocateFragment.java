package com.example.wifilocation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


@SuppressLint("ValidFragment")
public class LocateFragment extends Fragment {
    private String number;

    @SuppressLint("ValidFragment")
    LocateFragment(String number) {
        this.number = number;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locate, container, false);
        return view;
    }
}

