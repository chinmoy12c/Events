package com.example.events;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mikhaellopez.circularimageview.CircularImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {


    private View rootView;
    private GoogleMap googleMap;
    private boolean mLocationPermission = false;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PostUpdateHandler postUpdateHandler;
    private CircularImageView uploadEventButton;
    private int PICK_REQUEST_CODE = 500;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_map, container, false);
        uploadEventButton = rootView.findViewById(R.id.addStoryButton);

        TooltipCompat.setTooltipText(uploadEventButton,"Upload an event.");

        uploadEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEventUploadDialog();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(rootView.getContext());

        getLocationPermissions();

        updateMapUi();

        getDeviceLocation();
    }

    private void updateMapUi(){

        try{
            if(mLocationPermission){
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            else{
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermissions();
            }
        }
        catch (SecurityException e){
            Log.d("Error",e.getMessage());
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(rootView.getContext(),R.raw.map_style));

        googleMap.clear();

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mDefaultLocation));
    }

    private void getLocationPermissions(){
        if(ContextCompat.checkSelfPermission(rootView.getContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermission = true;
        }
        else{
            ActivityCompat.requestPermissions((Activity) rootView.getContext(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermission = false;
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermission = true;
                    updateMapUi();
                    getDeviceLocation();
                }
            }
        }

    }

    private void getDeviceLocation(){


        try {
            if (mLocationPermission) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) rootView.getContext(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()){
                            mLastKnownLocation = task.getResult();

                            LatLng myPosition = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, DEFAULT_ZOOM));

                            postUpdateHandler = new PostUpdateHandler(googleMap);

                        }
                        else{
                            Log.d("ERROR", "Current location is null. Using defaults.");
                            Log.e("ERROR", "Exception: %s", task.getException());
                            googleMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    private void showEventUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select choice")
                .setItems(new String[]{"Upload from gallery","Take a picture"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0)
                            galleryUpload();
                        else
                            takePictureEvent();
                    }
                });
        builder.show();
    }

    private void galleryUpload(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickIntent,PICK_REQUEST_CODE);
    }

    private void takePictureEvent(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            uploadEventButton.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }
}
