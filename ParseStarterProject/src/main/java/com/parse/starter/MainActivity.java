/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

//import android.support.constraint.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

  boolean loginModeActive=false;

  public void redirectIfLoggedIn(){
    if(ParseUser.getCurrentUser()!=null){
      Intent intent=new Intent(getApplicationContext(), UserListActivity.class);
      startActivity(intent);
        finish();
    }
  }

  public void toggleLoginMode(View view){
    Button loginSignUpButton=(Button)findViewById(R.id.loginSignUpButton);
    TextView toggleTextView=(TextView)findViewById(R.id.toggleTextView);
    if(view.getId()==R.id.toggleTextView) {
      if (loginModeActive) {
        loginModeActive = false;
        loginSignUpButton.setText("Sign Up");
        toggleTextView.setText("or, Log In");

      } else {
        loginModeActive = true;
        loginSignUpButton.setText("Log In");
        toggleTextView.setText("or, Sign Up");
      }
    }
  }

  public void signUpLogin(View view){
    EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
    EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

    if(loginModeActive){

      ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {
          if(e==null){

            Log.i("Info", "user logged in");
            redirectIfLoggedIn();

          }
          else{
            String message=e.getMessage();

            if(message.toLowerCase().contains("java"))
            {
              message=e.getMessage().substring(e.getMessage().indexOf(" "));
            }

            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

          }
        }
      });

    }
    else {

      ParseUser user = new ParseUser();
      user.setUsername(usernameEditText.getText().toString());
      user.setPassword(passwordEditText.getText().toString());

      user.signUpInBackground(new SignUpCallback() {
        @Override
        public void done(ParseException e) {
          if (e == null) {

            Log.i("info", "user signed up");
            redirectIfLoggedIn();

          } else {

            String message=e.getMessage();

            if(message.toLowerCase().contains("java"))
            {
              message=e.getMessage().substring(e.getMessage().indexOf(" "));
            }

            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

          }
        }
      });
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("whatsapp login");

    ConstraintLayout backgroundRelativeLayout=(ConstraintLayout) findViewById(R.id.backgroundRelativeLayout);
    backgroundRelativeLayout.setOnClickListener(this);
    redirectIfLoggedIn();
    
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  @Override
  public void onClick(View view) {
    if(view.getId()==R.id.backgroundRelativeLayout) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }
}