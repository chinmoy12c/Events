package com.example.events;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private RecyclerView messagesRecycler;
    private MessagesRecyclerAdapter messagesRecyclerAdapter;

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages , container , false);

        messagesRecycler = rootView.findViewById(R.id.messagesRecyclerView);
        messagesRecyclerAdapter = new MessagesRecyclerAdapter(rootView.getContext());

        messagesRecycler.setAdapter(messagesRecyclerAdapter);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        return rootView;
    }

}
