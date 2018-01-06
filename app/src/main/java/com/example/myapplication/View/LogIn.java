package com.example.myapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class LogIn extends AppCompatActivity {

    public EditText userEditText;
    public EditText passwordEditText;
    public String user;
    public String password;
    int rCode = 20;
    Intent me;

    public void loginClick (View view) {
        user = userEditText.getText().toString();
        password = passwordEditText.getText().toString();
        //Log.i("u:p", user + ":" + password);
        me = getIntent();
        me.putExtra("user", user);
        me.putExtra("password", password);

        PersonServices personServices = new PersonServices();
        personServices.userByEmailAndPass(this, user, password,
        new VolleyCallback() {
            @Override
            public void onSuccessResponse(String response) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Person person = mapper.readValue(response, Person.class);

                    if (person != null && person.id != 0) {
                        me.putExtra("person", response);
                        setResult(RESULT_OK, me);
                        finish();
                    }   else {
                        Toast.makeText(getBaseContext(), "Unknown User or Password", Toast.LENGTH_SHORT).show();
                    }

                }   catch (JsonParseException e) {
                    e.printStackTrace();
                }   catch (JsonMappingException e) {
                    e.printStackTrace();
                }   catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "Unknown User or Password", Toast.LENGTH_SHORT).show();
                Log.e("error", "error response: " + error.getMessage());
                VolleyLog.d("error", "Error: " + error.getMessage());
            }
        });
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
            /*Log.i("u:p", registerIntent.getStringExtra("user")
                    + ":" + registerIntent.getStringExtra("password")
                    + " " + registerIntent.getStringExtra("firstName")
                    + " " + registerIntent.getStringExtra("lastName")
                    + " " + registerIntent.getStringExtra("numpers"));*/
            user = registerIntent.getStringExtra("user");
            password = registerIntent.getStringExtra("password");
            if (user != null && password != null) {
                userEditText.setText(user);
                passwordEditText.setText(password);
            }
            sendMail(user, password);
        }
    }

    private void sendMail(String user, String password) {
        PersonServices personServices = new PersonServices();
        personServices.sendMail(this, user,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            Person person = mapper.readValue(response, Person.class);

                            if (person != null) {
                                if (person.error != null) {
                                    Toast.makeText(getBaseContext(), person.error.description, Toast.LENGTH_SHORT).show();
                                }   else {
                                    Toast.makeText(getBaseContext(), "Check your mail to activate", Toast.LENGTH_SHORT).show();
                                }
                            }   else {
                                Toast.makeText(getBaseContext(), "Unknown User", Toast.LENGTH_SHORT).show();
                            }

                        }   catch (JsonParseException e) {
                            e.printStackTrace();
                        }   catch (JsonMappingException e) {
                            e.printStackTrace();
                        }   catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "Check your mail", Toast.LENGTH_SHORT).show();
                        Log.e("error", "error response: " + error.getMessage());
                        VolleyLog.d("error", "Error: " + error.getMessage());
                    }
                });
    }

}
