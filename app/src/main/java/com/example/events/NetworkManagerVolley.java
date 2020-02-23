package com.example.events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

class NetworkManagerVolley {
    private Context context;

    NetworkManagerVolley(Context context){
        this.context = context;
    }

    void createUser(final String usernameData, final String passwordData, final String emailData, final CircularImageView appLogo){
        Log.d("Signup","start");
        try {

            final HashMap<String,String> postData = new HashMap<>();

            postData.put("username",usernameData);
            postData.put("password", passwordData);
            postData.put("email" , emailData);

            JSONObject userData = new JSONObject(postData);

            JsonObjectRequest createUserRequest = new JsonObjectRequest(Request.Method.POST, Constants.CREATE_USER_URL, userData, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            switch (response.get("INFO").toString()){

                                case "USER CREATED":
                                    Constants.USERNAME = usernameData;
                                    Constants.EMAIL = emailData;
                                    Constants.USERID = response.getString("USERID");
                                    context.startActivity(new Intent(context,MainActivity.class));
                                    break;

                                case "EMAIL EXISTS":
                                    Toast.makeText(context,"Email already exists!",Toast.LENGTH_LONG).show();
                                    break;

                                case "USERNAME EXISTS":
                                    Toast.makeText(context,"USERNAME EXISTS",Toast.LENGTH_LONG).show();
                                    break;

                                default:
                                    Toast.makeText(context,"Somthing went wrong! Try again.",Toast.LENGTH_LONG).show();
                                    Log.d("data",response.getString("INFO"));
                            }

                            appLogo.clearAnimation();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR",error.getMessage());

                    }
                });

            MySingleton.getInstance(context).addToRequestQueue(createUserRequest);
        }
        catch (Exception e){Log.d("ERROR",e.getMessage());}
    }

    void signInUser(final String usernameData, String passwordData, final String emailData, final CircularImageView appLogo){
        Log.d("SIGN","start");
        try{
            HashMap<String,String> postData = new HashMap<>();
            postData.put("username",usernameData);
            postData.put("password",passwordData);
            postData.put("email",emailData);

            JSONObject userData = new JSONObject(postData);

            JsonObjectRequest signUserRequest = new JsonObjectRequest(Request.Method.POST, Constants.SIGN_USER_URL, userData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{

                        switch (response.getString("INFO")){

                            case "USER DOES NOT EXIST":
                                Toast.makeText(context,"Invalid username or password",Toast.LENGTH_LONG).show();
                                break;

                            case "USER SIGNED":
                                Constants.USERNAME = usernameData;
                                Constants.EMAIL = emailData;
                                Constants.USERID = response.getString("USERID");
                                context.startActivity(new Intent(context,MainActivity.class));
                                break;
                        }

                        appLogo.clearAnimation();

                    }catch (JSONException e){
                        Toast.makeText(context,"Somthing went wrong! Try again.",Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context,"Somthing went wrong! Try again.",Toast.LENGTH_LONG).show();
                }
            });

            MySingleton.getInstance(context).addToRequestQueue(signUserRequest);
        }catch (Exception e){
            Toast.makeText(context,"Somthing went wrong! Try again.",Toast.LENGTH_LONG).show();
        }
    }

    void addStory(final LatLng myPosition, Bitmap resultImage, final EditText postDescription, final View mapView, final ScrollView detailsView, final CircularImageView uploadEventButton) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resultImage.compress(Bitmap.CompressFormat.JPEG,60,stream);
        final byte[] resultImageBytes = stream.toByteArray();
        Log.d("SIZE",String.valueOf(resultImageBytes.length));



        VolleyMultipartRequest addStoryRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.UPLOAD_IMAGE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    JSONObject responseObject = new JSONObject(new String(response.data));

                    HashMap<String,String> storyDetails = new HashMap<>();
                    storyDetails.put("userId",Constants.USERID);
                    storyDetails.put("latitude",String.valueOf(myPosition.latitude));
                    storyDetails.put("longitude",String.valueOf(myPosition.longitude));
                    storyDetails.put("imagePath",responseObject.getString("INFO"));
                    storyDetails.put("postDescription",postDescription.getText().toString());

                    JSONObject postData = new JSONObject(storyDetails);

                    JsonObjectRequest addStoryDataRequest = new JsonObjectRequest(Request.Method.POST, Constants.ADD_STORY_URL,
                            postData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("RESPONSE","uploaded");
                            detailsView.setVisibility(View.INVISIBLE);
                            mapView.setVisibility(View.VISIBLE);
                            uploadEventButton.setVisibility(View.VISIBLE);
                            Toast.makeText(context,"Uploaded!",Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context,"Something went wrong! Try again.",Toast.LENGTH_LONG).show();
                        }
                    });

                    MySingleton.getInstance(context).addToRequestQueue(addStoryDataRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Something went wrong! Try again.",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("uploadedImage", new DataPart(imagename + ".jpeg", resultImageBytes));
                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(addStoryRequest);
    }

    void getTopStory(final LatLngBounds box, final GoogleMap googleMap) {
        Map<String,Double> fetchBounds = new HashMap<>();
        fetchBounds.put("upperLongitude",box.northeast.longitude);
        fetchBounds.put("upperLatitude",box.northeast.latitude);
        fetchBounds.put("lowerLongitude",box.southwest.longitude);
        fetchBounds.put("lowerLatitude",box.southwest.latitude);

        JSONObject fetchBoundsObject = new JSONObject(fetchBounds);

        JsonObjectRequest getStoryRequest = new JsonObjectRequest(Request.Method.POST, Constants.GET_STORY, fetchBoundsObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE",response.toString());
                try {
                    if (response.getString("INFO").equals("NEW DATA")){
                        LatLng storyLoc = new LatLng(response.getDouble("latitude"),response.getDouble("longitude"));
                        Marker newMarker = googleMap.addMarker(new MarkerOptions().position(storyLoc));
                        newMarker.setTag(response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Something went wrong! Try again",Toast.LENGTH_LONG).show();
                Log.d("ERROR",error.getMessage());
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(getStoryRequest);

    }
}
