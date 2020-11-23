package com.example.gossipmediaa;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    ParseUser user = new ParseUser();
    Boolean signupModeActive = false;
    TextView signupTextView;
    EditText usernameEditText;
    EditText passwordEditText;
    ImageView logoimageView;
    ConstraintLayout backgroundLayout;

    //List
    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(),UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == event.ACTION_DOWN) {
            loginClick(v);
        }

        return false;
    }

    //click text view
    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.signupTextView) {

            Button loginButton = findViewById(R.id.loginButton);

            if(signupModeActive) {

                usernameEditText.setText("");
                passwordEditText.setText("");
                signupModeActive = false;
                loginButton.setText("Login");
                signupTextView.setText("Or, Sign Up");

            } else {

                usernameEditText.setText("");
                passwordEditText.setText("");
                signupModeActive = true;
                loginButton.setText("Sign Up");
                signupTextView.setText("Or, Login");
            }
        } else if(v.getId() == R.id.logoImageView || v.getId() == R.id.backgroundLayout) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    //Click button
    public void loginClick(View view) {

        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
        } else {

            if(signupModeActive) {
                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());

                user.signUpInBackground(e -> {
                    if (e == null) {
//            Log.i("signup", "completed");
                        showUserList();
                    } else {
                        Toast.makeText(com.example.gossipmediaa.MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {

                user.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), (user, e) -> {
                    if(e == null) {
//              Log.i("successfully","Logged in");
                        showUserList();
                    } else {
                        Toast.makeText(com.example.gossipmediaa.MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Login");
        showUserList();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

}