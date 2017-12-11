package com.example.myapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String user;
    String firstName;
    String lastName;
    String password;
    String rpassword;
    String numpers;
    Person newPerson;

    EditText userEditText;
    EditText passwordEditText;
    EditText repeatedPasswordEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;
    Spinner numpersSpinner;

    Intent me;
    ObjectMapper mapper;

    public void okClick (View view) {
        user = userEditText.getText().toString();
        password = passwordEditText.getText().toString();
        rpassword = repeatedPasswordEditText.getText().toString();
        firstName = firstNameEditText.getText().toString();
        lastName = lastNameEditText.getText().toString();

        if (password.equals(rpassword)) {

            me.putExtra("user", user);
            me.putExtra("password", password);
            me.putExtra("firstName", firstName);
            me.putExtra("lastName", lastName);
            me.putExtra("numpers", numpers);

            newPerson = new Person();
            newPerson.firstName = firstName;
            newPerson.lastName = lastName;
            newPerson.email = user;
            newPerson.password = password;
            newPerson.numpers = Integer.valueOf(numpers);

            try {
                register();
            }   catch (JsonProcessingException e) {
                e.printStackTrace();
            }   catch (JSONException e) {
                e.printStackTrace();
            }

        }   else {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
    }

    public void register () throws JsonProcessingException, JSONException {
        user = userEditText.getText().toString();
        password = passwordEditText.getText().toString();
        //Log.i("u:p", user + ":" + password);
        me = getIntent();
        me.putExtra("user", user);
        me.putExtra("password", password);

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

    public void cancelClick (View view) {
        Intent me = getIntent();
        me.putExtra("user", "");
        me.putExtra("password", "");
        me.putExtra("firstName", "");
        me.putExtra("lastName", "");
        me.putExtra("numpers", "");
        setResult(RESULT_CANCELED, me);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        me = getIntent();
        mapper = new ObjectMapper();

        numpers = "1";
        userEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatedPasswordEditText = findViewById(R.id.repeatedPasswordEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        numpersSpinner = findViewById(R.id.numpersSpinner);

        List<String> list = new ArrayList<String>();
        for (int i = 1; i < 10; i++) list.add(String.valueOf(i));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numpersSpinner.setAdapter(dataAdapter);
        numpersSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        numpers = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        numpers = "1";
    }
}
