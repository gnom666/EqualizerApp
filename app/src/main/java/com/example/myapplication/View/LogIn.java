package com.example.myapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication.R;

public class LogIn extends AppCompatActivity {

    public EditText userEditText;
    public EditText passwordEditText;
    public String user;
    public String password;
    int rCode = 20;

    public void loginClick (View view) {
        user = userEditText.getText().toString();
        password = passwordEditText.getText().toString();
        //Log.i("u:p", user + ":" + password);
        Intent me = getIntent();
        me.putExtra("user", user);
        me.putExtra("password", password);
        setResult(RESULT_OK, me);
        finish();
    }

    public void registerClick (View view) {
        Intent registerIntent = new Intent(getApplicationContext(), Register.class);
        startActivityForResult(registerIntent, rCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userEditText = findViewById(R.id.userEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent registerIntent) {
        super.onActivityResult(requestCode, resultCode, registerIntent);
        if(requestCode == rCode && resultCode == RESULT_OK) {
            Log.i("u:p", registerIntent.getStringExtra("user")
                    + ":" + registerIntent.getStringExtra("password")
                    + " " + registerIntent.getStringExtra("firstName")
                    + " " + registerIntent.getStringExtra("lastName")
                    + " " + registerIntent.getStringExtra("numpers"));
            user = registerIntent.getStringExtra("user");
            password = registerIntent.getStringExtra("password");
            if (user != null && password != null) {
                userEditText.setText(user);
                passwordEditText.setText(password);
            }
        }
    }

}
