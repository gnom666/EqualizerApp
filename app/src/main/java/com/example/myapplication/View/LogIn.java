package com.example.myapplication.View;

import android.content.Intent;
import android.net.Uri;
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
import com.example.myapplication.Controller.google.GoogleHelper;
import com.example.myapplication.Controller.google.GoogleListener;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LogIn extends AppCompatActivity implements GoogleListener{

    public EditText userEditText;
    public EditText passwordEditText;
    public String user;
    public String password;
    int rCode = 20;
    Intent me;
    GoogleHelper googleHelper;
    ObjectMapper mapper;
    com.google.android.gms.common.SignInButton googleLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapper = new ObjectMapper();
        me = getIntent();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            Log.i("signIn:data", "name->" + personName + "  email->" + personEmail);
        }

        googleHelper=new GoogleHelper(this, this, null);

        googleLoginButton = findViewById(R.id.googleLoginButton);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLoginClick(v);
            }
        });

        userEditText = findViewById(R.id.userEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == rCode && resultCode == RESULT_OK) {
            /*Log.i("u:p", registerIntent.getStringExtra("user")
                    + ":" + registerIntent.getStringExtra("password")
                    + " " + registerIntent.getStringExtra("firstName")
                    + " " + registerIntent.getStringExtra("lastName")
                    + " " + registerIntent.getStringExtra("numpers"));*/
            user = data.getStringExtra("user");
            password = data.getStringExtra("password");
            if (user != null && password != null) {
                userEditText.setText(user);
                passwordEditText.setText(password);
            }
            sendMail(user, password);
        }   else {
            googleHelper.onActivityResult(requestCode, resultCode, data);
        }
    }


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
                        if (person.error != null) {
                            Toast.makeText(getBaseContext(), person.error.description, Toast.LENGTH_SHORT).show();
                        }   else {
                            me.putExtra("person", response);
                            setResult(RESULT_OK, me);
                            finish();
                        }
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

    public void googleLoginClick (View view) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {

            me = getIntent();
            me.putExtra("user", acct.getEmail());
            me.putExtra("password", acct.getId());

        }   else {
            googleHelper.performSignIn(this);
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

    @Override
    public void onGoogleAuthSignIn(String authToken, String userId) {

        Person newPerson = new Person();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            //String personName = acct.getDisplayName();
            newPerson.firstName = acct.getGivenName();
            newPerson.lastName = acct.getFamilyName();
            newPerson.email = user = acct.getEmail();
            newPerson.numpers = 1;
            newPerson.password = password = userId;
            newPerson.enabled = true;
            //String personId = acct.getId();
            //Uri personPhoto = acct.getPhotoUrl();
            me.putExtra("user", newPerson.email);
            me.putExtra("password", newPerson.password);
            me.putExtra("firstName", newPerson.firstName);
            me.putExtra("lastName", newPerson.lastName);
            me.putExtra("numpers", 1);
        }

        try {
            register(newPerson);
        }   catch (JsonProcessingException e) {
            e.printStackTrace();
        }   catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void register (final Person newPerson) throws JsonProcessingException, JSONException {
        PersonServices personServices = new PersonServices();
        personServices.addPerson(this, "PersonOut",
                new JSONObject(mapper.writeValueAsString(newPerson)),
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {

                        try {
                            Person person = mapper.readValue(response, Person.class);

                            if (person != null) {
                                if (person.error != null) {
                                    Toast.makeText(getApplicationContext(), person.error.description, Toast.LENGTH_SHORT).show();
                                }   else {

                                    setResult(RESULT_OK, me);
                                    finish();

                                }
                            }   else {
                                Toast.makeText(getApplicationContext(), "person=null", Toast.LENGTH_SHORT).show();
                            }

                        }   catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
    }

    @Override
    public void onGoogleAuthSignInFailed(String errorMessage) {

    }

    @Override
    public void onGoogleAuthSignOut() {

    }
}
