package com.example.events;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class NetworkManagerRealtimeDatabase {

    private DatabaseReference rootRef;
    private int ACCURACY_VALUE = 150;

    NetworkManagerRealtimeDatabase(){
       rootRef = FirebaseDatabase.getInstance().getReference();
    }

    void addPost(LatLng coords){
        Map<String,String> postData = new HashMap<>();
        String uid = UUID.randomUUID().toString();
        DatabaseReference postRef = rootRef.child("posts");
        String geoHashUser = getGeoHash(coords);
        postData.put("geoHashUser",geoHashUser);
        postData.put("name","chinmoy");
        postData.put("latitude",String.valueOf(coords.latitude));
        postData.put("longitude",String.valueOf(coords.longitude));
        postRef.child(uid).setValue(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //TODO:change
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO:change
            }
        });

    }

    void getDataTopStory(final LatLngBounds boxBounds, final GoogleMap googleMap){
        String startCoords = getGeoHash(boxBounds.southwest);
        String endCoords = getGeoHash(boxBounds.northeast);
        Query mBoxTopPost = rootRef.child("posts").orderByChild("geoHashUser").startAt(startCoords).endAt(endCoords).limitToFirst(1);

        mBoxTopPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Log.d("Post data",dataSnapshot.getValue().toString());
                    googleMap.addMarker(new MarkerOptions().position(boxBounds.getCenter()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Error",databaseError.getMessage());  //TODO: CHANGE
            }
        });
    }

    private String getGeoHash(LatLng latLng){

        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        StringBuilder geoHash = new StringBuilder();
        double minLng = -180, maxLng = 180, minLat = -90, maxLat = 90, midLong, midLat;

        for(int x = 0; x < ACCURACY_VALUE; x++){
            if (x % 2 == 0){
                midLong = (minLng + maxLng)/2;

                if (longitude > midLong){
                    geoHash.append("1");
                    minLng = midLong;
                }
                else{
                    geoHash.append("0");
                    maxLng = midLong;
                }

                Log.d("midLong",String.valueOf(midLong));
            }
            else{
                midLat = (minLat + maxLat)/2;

                if(latitude > midLat){
                    geoHash.append("1");
                    minLat = midLat;
                }

                else{
                    geoHash.append("0");
                    maxLat = midLat;
                }

                Log.d("midLat",String.valueOf(midLat));
            }
        }

        return geoHash.toString();
    }
}
