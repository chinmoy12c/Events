package com.example.events;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback{


    private View rootView,mapView;
    private GoogleMap googleMap;
    private boolean mLocationPermission = false;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PostUpdateHandler postUpdateHandler;
    private CircularImageView uploadEventButton;
    private int PICK_REQUEST_CODE = 500;
    private static final int REQUEST_IMAGE_CAPTURE = 400;
    private File capturedImageFile;
    private ImageView previewImage;
    private Bitmap resultImage;
    private Button uploadButton, cancelButton;
    private EditText postDescription;
    private ScrollView detailsView;
    private NetworkManagerVolley networkManagerVolley;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_map, container, false);

        uploadEventButton = rootView.findViewById(R.id.addStoryButton);
        uploadButton = rootView.findViewById(R.id.uploadButton);
        cancelButton = rootView.findViewById(R.id.cancelButton);
        detailsView  = rootView.findViewById(R.id.uploadDetailsView);
        mapView = rootView.findViewById(R.id.mapFragment);
        previewImage = rootView.findViewById(R.id.previewImage);
        postDescription = rootView.findViewById(R.id.postDescription);

        networkManagerVolley = new NetworkManagerVolley(getContext());

        TooltipCompat.setTooltipText(uploadEventButton,"Upload an event.");

        uploadEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEventUploadDialog();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelUpload();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStory();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;

    }

    private void uploadStory() {
        try {
            if (mLocationPermission) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) rootView.getContext(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()){
                            mLastKnownLocation = task.getResult();

                            LatLng myPosition = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                            networkManagerVolley.addStory(myPosition,resultImage,postDescription,mapView,detailsView,uploadEventButton);

                        }
                        else{
                            Log.d("ERROR", "Current location is null. Using defaults.");
                            Log.e("ERROR", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void cancelUpload() {
        uploadEventButton.setVisibility(View.VISIBLE);
        mapView.setVisibility(View.VISIBLE);
        detailsView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.setOnMarkerClickListener(new StoryViewHandler(getContext()));

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

                            postUpdateHandler = new PostUpdateHandler(getContext(),googleMap);

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
        Intent uploadImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        uploadImageIntent.setType("image/*");
        uploadImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(uploadImageIntent,PICK_REQUEST_CODE);
    }

    private void takePictureEvent(){
        Intent takeImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takeImageIntent.resolveActivity(rootView.getContext().getPackageManager()) != null){
            try {
                capturedImageFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(),"Oops! Something went wrong",Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (capturedImageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.example.events",capturedImageFile);
                takeImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takeImageIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
            try {
                resultImage = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(data.getData()));
                previewImage.setImageBitmap(resultImage);
                mapView.setVisibility(View.INVISIBLE);
                uploadEventButton.setVisibility(View.INVISIBLE);
                detailsView.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Bitmap capturedImage =  BitmapFactory.decodeFile(capturedImageFile.getAbsolutePath());
            resultImage = checkRotated(capturedImage);

            previewImage.setImageBitmap(resultImage);
            mapView.setVisibility(View.INVISIBLE);
            uploadEventButton.setVisibility(View.INVISIBLE);
            detailsView.setVisibility(View.VISIBLE);
        }
    }

    private Bitmap checkRotated(Bitmap bitmap){
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(capturedImageFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    private Bitmap rotateImage(Bitmap source,float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
