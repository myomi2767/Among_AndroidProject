package com.example.among.calendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.among.R;

public class CalendarFragment extends Fragment {

    public CalendarFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewGroup = (View)LayoutInflater.from(getContext()).inflate(R.layout.fragment_calendar, container, false);


        return viewGroup;
    }
}
