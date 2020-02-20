package com.example.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class NetworkManagerFirebase {

    private FirebaseFirestore db;
    private Context context;

    NetworkManagerFirebase(Context context){
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    void initiateSignUp(final String usernameData, final String passwordData, final CircularImageView appLogo) {
        db.collection("users")
                .whereEqualTo("username",usernameData)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().isEmpty()){
                                createUser(usernameData,passwordData,appLogo);
                            }
                            else{
                                appLogo.clearAnimation();
                                Toast.makeText(context,"User already exists!Try another username.",Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            appLogo.clearAnimation();
                            Toast.makeText(context,"Sign up failed!Please try again.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void createUser(final String usernameData, String passwordData, final CircularImageView appLogo) {
        String uuid = UUID.randomUUID().toString();
        Map<String,String> userDoc = new HashMap<>();
        userDoc.put("username",usernameData);
        userDoc.put("password",passwordData);

        db.collection("users").document(uuid).set(userDoc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Activity loginActivity = (Activity)context;
                            SharedPreferences userData = loginActivity.getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = userData.edit();
                            editor.putString("username",usernameData);
                            editor.apply();

                            context.startActivity(new Intent(context,MainActivity.class));
                        }
                        else{
                            appLogo.clearAnimation();
                            Toast.makeText(context,"Sign up failed!Please try again.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    void initiateLogin(final String usernameData, final String passwordData, String emailData, final CircularImageView appLogo) {
        db.collection("users")
                .whereEqualTo("username",usernameData)
                .whereEqualTo("password",passwordData)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("data",task.getResult().getDocuments().toString());
                    if (task.getResult().isEmpty()){
                        appLogo.clearAnimation();

                        Toast.makeText(context,"User does not exist! Please Sign up.",Toast.LENGTH_LONG).show();
                    }
                    else{

                        Activity loginActivity = (Activity)context;
                        SharedPreferences userData = loginActivity.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = userData.edit();
                        editor.putString("username",usernameData);
                        editor.apply();

                        context.startActivity(new Intent(context,MainActivity.class));
                    }
                }
                else{
                    appLogo.clearAnimation();
                    Toast.makeText(context,"Login failed!Please try again.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
