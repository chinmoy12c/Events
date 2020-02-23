package com.example.events;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

class StoryViewHandler implements GoogleMap.OnMarkerClickListener {

    private final Context context;

    public StoryViewHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
