package com.example.events;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {

    private static MySingleton instance;
    private RequestQueue requestQueue;
    private static Context context;


    MySingleton(Context context){
        this.context = context;

        requestQueue = getRequestQueue();
    }

    RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return requestQueue;
    }

    public static synchronized MySingleton getInstance(Context context){
        if (instance == null){
            instance = new MySingleton(context);
        }

        return instance;
    }

    public void addToRequestQueue(Request request){
        if (request != null){
            getRequestQueue().add(request);
        }
    }


}
