package com.todo.todolistapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TODOListFragment extends Fragment {


    public TODOListFragment() {
        // Required empty public constructor
    }

    public static TODOListFragment newInstance() {
        return new TODOListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }
}