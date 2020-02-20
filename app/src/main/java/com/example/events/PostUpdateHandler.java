package com.example.events;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.ArrayList;

public class PostUpdateHandler implements GoogleMap.OnCameraIdleListener{

    private GoogleMap googleMap;
    private VisibleRegion initialVisibleRegion;
    private double initialZoom;
    private double scalableScreenLatitude;
    private double scalableScreenLongitude;
    private int BOX_DIVISIONS = 3;
    private ArrayList<LatLng> loadedCentres = new ArrayList<>();
    private NetworkManagerRealtimeDatabase networkManagerRealtimeDatabase;

    PostUpdateHandler(GoogleMap googleMap){

        this.googleMap = googleMap;

        networkManagerRealtimeDatabase = new NetworkManagerRealtimeDatabase();

        updateScaleValues();
        ArrayList<LatLngBounds> initialBounds = getUpdatedCoords(googleMap.getProjection().getVisibleRegion());
        setMapUpdates(initialBounds);

        googleMap.setOnCameraIdleListener(this);
    }

    @Override
    public void onCameraIdle() {


        //networkManagerRealtimeDatabase.addPost(googleMap.getCameraPosition().target);


        ArrayList<LatLngBounds> updatedBounds;

        if (googleMap.getCameraPosition().zoom != initialZoom) {
            googleMap.clear();
            loadedCentres.clear();
        }

        updatedBounds = getUpdates();

        if (updatedBounds.size() != 0)
            setMapUpdates(updatedBounds);
    }

    private ArrayList<LatLngBounds> getUpdates(){

        VisibleRegion newVisibleRegion = googleMap.getProjection().getVisibleRegion();

        updateScaleValues();
        return getUpdatedCoords(newVisibleRegion);
    }

    private ArrayList<LatLngBounds> getUpdatedCoords(VisibleRegion newVisibleRegion){

        int boxCounterLong,boxCounterLat;
        ArrayList<LatLngBounds> updateBoxes = new ArrayList<>();
        double latPosition;
        double longPosition;

        for(boxCounterLat=0,latPosition = newVisibleRegion.nearLeft.latitude;boxCounterLat<BOX_DIVISIONS;boxCounterLat++,latPosition+=scalableScreenLatitude){

            for(boxCounterLong=0,longPosition=newVisibleRegion.nearLeft.longitude;boxCounterLong<BOX_DIVISIONS;boxCounterLong++,longPosition+=scalableScreenLongitude){

                LatLng southWest = new LatLng(latPosition,longPosition);
                LatLng northEast = new LatLng(latPosition+scalableScreenLatitude,longPosition+scalableScreenLongitude);
                LatLngBounds newBox = new LatLngBounds(southWest,northEast);

                if(isNewCentre(newBox.getCenter())){
                    updateBoxes.add(newBox);
                    loadedCentres.add(newBox.getCenter());
                }

            }
        }
        return updateBoxes;
    }

    private void updateScaleValues(){

        initialVisibleRegion = googleMap.getProjection().getVisibleRegion();
        initialZoom = googleMap.getCameraPosition().zoom;
        scalableScreenLatitude = Math.abs(initialVisibleRegion.farLeft.latitude - initialVisibleRegion.nearLeft.latitude)/BOX_DIVISIONS;
        scalableScreenLongitude = Math.abs(initialVisibleRegion.farLeft.longitude - initialVisibleRegion.farRight.longitude)/BOX_DIVISIONS;

        if(initialZoom <= 3)
            BOX_DIVISIONS = 4;
        else
            BOX_DIVISIONS = 3;
    }

    private void setMapUpdates(ArrayList<LatLngBounds> updatedBoxes){

        for (LatLngBounds box : updatedBoxes) {
            networkManagerRealtimeDatabase.getDataTopStory(box,googleMap);
        }
    }

    private boolean isNewCentre(LatLng newBoxCentre){

        for (LatLng oldCentre : loadedCentres) {

            double x1 = oldCentre.latitude;
            double y1 = oldCentre.longitude;
            double x2 = newBoxCentre.latitude;
            double y2 = newBoxCentre.longitude;

            double centreDistance = Math.sqrt(Math.pow(x1-x2,2)  + Math.pow(y1-y2,2));

            if(centreDistance < scalableScreenLongitude)
                return false;
        }
        return true;
    }
}
