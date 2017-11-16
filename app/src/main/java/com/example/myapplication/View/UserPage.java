package com.example.myapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.Controller.EventServices;
import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

public class UserPage extends AppCompatActivity {
    TextView nameTextView;
    TextView emailTextView;
    TextView numpersTextView;
    TextView idTextView;
    EditText activitiesEditText;
    Person person;
    ArrayList<Event> events;
    String personJSON;
    String activitiesJSON;

    public void setActivities () {
        EventServices eventServices = new EventServices();
        eventServices.activitiesByParticipant(this, String.valueOf(person.id), new VolleyCallback() {
            @Override
            public void onSuccessResponse(String response) {
                activitiesJSON = response;
                ObjectMapper mapper = new ObjectMapper();
                try {
                    events = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Event.class));
                    if (events != null) {
                        for (Event e : events) {
                            activitiesEditText.setText(activitiesEditText.getText().toString() + "\n" + e.toString());
                        }
                    }
                }   catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        person = null;
        events = null;
        nameTextView = findViewById(R.id.fullNameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        numpersTextView = findViewById(R.id.numpersTextView);
        idTextView = findViewById(R.id.idTextView);
        activitiesEditText = findViewById(R.id.activitiesEditText);

        Intent me = getIntent();
        String user = me.getStringExtra("user");
        String password = me.getStringExtra("password");

        PersonServices personServices = new PersonServices();
        personServices.userByEmailAndPass(this, user, password, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String response) {
                try {
                    personJSON = response;
                    ObjectMapper mapper = new ObjectMapper();
                    person = mapper.readValue(response, Person.class);

                    idTextView.setText(String.valueOf(person.id));
                    nameTextView.setText(person.firstName + " " + person.lastName);
                    emailTextView.setText(person.email);
                    numpersTextView.setText(String.valueOf(person.numpers));

                    if (person != null) {
                        setActivities();
                    }

                }   catch (JsonParseException e) {
                    e.printStackTrace();
                }   catch (JsonMappingException e) {
                    e.printStackTrace();
                }   catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
}
