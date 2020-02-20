package com.example.events;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkManagerVolley {
    private Context context;

    NetworkManagerVolley(Context context){
        this.context = context;
    }

    void createUser(final String usernameData, final String passwordData, final String emailData){
        try {

            final HashMap<String,String> postData = new HashMap<>();

            postData.put("username",usernameData);
            postData.put("password", passwordData);
            postData.put("email" , emailData);

            StringRequest createUserRequest = new StringRequest(Request.Method.POST, Constants.createUserUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("RESPONSE" , response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ERROR", error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return postData;
                }
            };

            MySingleton.getInstance(context).addToRequestQueue(createUserRequest);
        }
        catch (Exception e){Log.d("ERROR",e.getMessage());}
    }
}
