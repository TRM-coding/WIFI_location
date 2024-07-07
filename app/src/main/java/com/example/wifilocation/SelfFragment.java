package com.example.wifilocation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

@SuppressLint("ValidFragment")
public class SelfFragment extends Fragment {
    private String number;

    @SuppressLint("ValidFragment")
    SelfFragment(String number) {
        this.number = number;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_self, container, false);
        return view;
    }
}