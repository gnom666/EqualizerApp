package com.example.myapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    public Person person;
    long id = 1;
    TextView textView;
    Button checkButton;
    EditText editText;
    PersonServices personServices;
    EditText multiLineTextView;
    int rCode = 10;
    int userPageRequestCode = 30;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent loginIntent = new Intent(getApplicationContext(), LogIn.class);
        startActivityForResult(loginIntent, rCode);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent loginIntent) {
        super.onActivityResult(requestCode, resultCode, loginIntent);
        if(requestCode == rCode && resultCode == RESULT_OK) {
            String user = loginIntent.getStringExtra("user");
            String password = loginIntent.getStringExtra("password");
            Log.i("u:p", user + ":" + password);

            //Intent userPageIntent = new Intent(getApplicationContext(), UserPage.class);
            Intent userPageIntent = new Intent(getApplicationContext(), UserPage.class);
            userPageIntent.putExtra("user", user);
            userPageIntent.putExtra("password", password);
            startActivityForResult(userPageIntent, userPageRequestCode);
        }
        if(requestCode == rCode && resultCode == RESULT_CANCELED) {
            if (loginIntent == null)
                finish();
            else
                startActivityForResult(loginIntent, rCode);
        }
        if (requestCode == userPageRequestCode && resultCode == RESULT_OK) {
            Log.i("info", "UserPage returned");
            finish();
        }
        if (requestCode == userPageRequestCode) {
            Log.i("info", "UserPage returned");
            finish();
        }
    }
}
