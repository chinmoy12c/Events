package com.example.events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;


public class StoryDisplay extends AppCompatActivity {

    private JSONObject storyObject;
    private ImageView storyImage, profilePic;
    private TextView username,storyDescription;
    private RelativeLayout vanishingBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_display);

        storyImage = findViewById(R.id.storyImage);
        profilePic = findViewById(R.id.profilePicStory);
        username = findViewById(R.id.usernameStory);
        storyDescription = findViewById(R.id.storyDescription);
        vanishingBack = findViewById(R.id.vanishingBack);

        Intent currentIntent = getIntent();
        String storyData = currentIntent.getStringExtra("storyData");
        try {
            if (storyData != null) {
                Log.d("DATA" , storyData);
                storyObject = new JSONObject(storyData);
                setDetails(storyObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        vanishingBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                
                return true;
            }
        });
    }

    private void setDetails(JSONObject storyObject) throws JSONException {

        username.setText(storyObject.getString("username"));
        storyDescription.setText(storyObject.getString("description"));

        String imageUrl = Constants.IMAGE_BASE + storyObject.getString("postPath") + ".jpeg";
        Log.d("IMAGE URL" , imageUrl);
        final ImageRequest storyImageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                storyImage.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP,null,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getBaseContext(),"Something went wrong!",Toast.LENGTH_LONG).show();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(storyImageRequest);
    }
}
