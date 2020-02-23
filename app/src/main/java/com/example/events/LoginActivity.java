package com.example.events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;

public class LoginActivity extends AppCompatActivity {

    NetworkManagerFirebase networkManagerFirebase;
    NetworkManagerVolley networkManagerVolley;
    EditText usernameEditText, passwordEditText ,emailEditText;
    TextView signInPrompt, signUpPrompt;
    Button loginButton, signUpButton;
    String usernameData, passwordData, emailData;
    Animation rotateLogo;
    CircularImageView appLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.enter_username_edittext);
        passwordEditText = findViewById(R.id.enter_password_edittext);
        emailEditText = findViewById(R.id.enter_email_edittext);
        signInPrompt = findViewById(R.id.sign_in_prompt);
        signUpPrompt = findViewById(R.id.sign_up_prompt);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.register_button);
        appLogo = findViewById(R.id.appLogo);

        rotateLogo = AnimationUtils.loadAnimation(this,R.anim.rotate_objects);

        if (checkLoggedUser()){
            startActivity(new Intent(this,MainActivity.class));
        }
        else{
            networkManagerFirebase = new NetworkManagerFirebase(this);
            networkManagerVolley = new NetworkManagerVolley(this);
        }
    }

    private boolean checkLoggedUser() {
        SharedPreferences userData = this.getPreferences(MODE_PRIVATE);
        return userData.getString("username",null) != null;
    }

    public void prepareSignUpData(View view){
        usernameData = usernameEditText.getText().toString().trim();
        passwordData = passwordEditText.getText().toString().trim();
        emailData = emailEditText.getText().toString().trim();

        if(usernameData.length() <= 4){
            Toast.makeText(this,"Username too short!",Toast.LENGTH_LONG).show();
            return;
        }

        if(passwordData.length() <= 8){
            Toast.makeText(this,"Password too short!",Toast.LENGTH_LONG).show();
            return;
        }

        if (!emailData.contains("@")){
            Toast.makeText(this,"Enter a valid email!",Toast.LENGTH_LONG).show();
            return;
        }

        appLogo.startAnimation(rotateLogo);
        //networkManagerFirebase.initiateSignUp(usernameData,passwordData,appLogo);
        networkManagerVolley.createUser(usernameData,passwordData,emailData,appLogo);
    }

    public void prepareLoginData(View view){
        usernameData = usernameEditText.getText().toString().trim();
        passwordData = passwordEditText.getText().toString().trim();
        emailData = emailEditText.getText().toString().trim();


        appLogo.startAnimation(rotateLogo);
        //networkManagerFirebase.initiateLogin(usernameData,passwordData,emailData,appLogo);
        networkManagerVolley.signInUser(usernameData,passwordData,emailData,appLogo);
    }

    public void showLoginForm(View view){
        signInPrompt.setVisibility(View.INVISIBLE);
        signUpButton.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        signUpPrompt.setVisibility(View.VISIBLE);
    }

    public void showSignUpForm(View view){
        signInPrompt.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        signUpPrompt.setVisibility(View.INVISIBLE);
    }

}
