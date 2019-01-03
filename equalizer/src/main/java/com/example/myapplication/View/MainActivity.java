package com.example.myapplication.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

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

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Intent userPageIntent = new Intent(getApplicationContext(), UserPage.class);
            userPageIntent.putExtra("user", acct.getEmail());
            userPageIntent.putExtra("password", acct.getId());
            startActivityForResult(userPageIntent, userPageRequestCode);
        }   else {

            Intent loginIntent = new Intent(getApplicationContext(), LogIn.class);
            startActivityForResult(loginIntent, rCode);
        }
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
            loginIntent = new Intent(getApplicationContext(), LogIn.class);
            startActivityForResult(loginIntent, rCode);
        }
        if (requestCode == userPageRequestCode && resultCode == RESULT_CANCELED) {
            Log.i("info", "UserPage returned");
            finish();
        }
    }
}
