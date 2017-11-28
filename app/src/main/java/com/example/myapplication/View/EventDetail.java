package com.example.myapplication.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class EventDetail extends AppCompatActivity {

    Person person;
    String personJson;
    ArrayList<Person> persons;
    LinkedHashMap<Long, Boolean> selected;
    Event event;

    ListView participantsListView;
    ParticipantsListAdapter participantsListAdapter;
    EditText name;
    EditText description;
    EditText date;

    Intent me;
    ObjectMapper mapper;
    Object mThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init ();

    }

    private void init() {

        me = getIntent();
        String personJson = me.getStringExtra("person");
        String eventJson = me.getStringExtra("event");

        mapper = new ObjectMapper();
        try {
            person = mapper.readValue(personJson, Person.class);
            event = mapper.readValue(eventJson, Event.class);
        }   catch (IOException e) {
            e.printStackTrace();
        }

        name = findViewById(R.id.detailedEventNameEditText);
        description = findViewById(R.id.detailedEventDescriptionEditText);
        date = findViewById(R.id.detailedEventDateEditText);

        name.setText(event.name);
        description.setText(event.description);
        date.setText(event.date);

        //setParticipantsList(person.id);
    }

    public void setParticipantsList (final long pId) {
        PersonServices personServices = new PersonServices();

        personServices.contacts(this, pId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            persons = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Person.class));
                            if (persons != null) {
                                for (int i = 0; i < persons.size(); i++) {
                                    selected.put(persons.get(i).id, false);
                                }

                                setListView();

                            }
                        }   catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
        });
    }

    public void setListView () {
        participantsListView = findViewById(R.id.addEventParticipantsListView);
        participantsListAdapter = new ParticipantsListAdapter((Context) mThis, 0, persons);
        participantsListView.setAdapter(participantsListAdapter);
    }

    public class ParticipantsListAdapter extends ArrayAdapter<Person> {

        private ArrayList<Person> participants;
        private Context context;
        private LayoutInflater layOutInflater;
        View.OnClickListener listener;

        public ParticipantsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Person> participants) {
            super(context, resource, participants);
            this.participants = participants;
            selected = new LinkedHashMap<>();
            for (Person p : participants) {
                selected.put(p.id, false);
            }
            this.context = context;
            this.layOutInflater = LayoutInflater.from(EventDetail.this);

            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox checkBox = (CheckBox) view;
                    selected.put((long)checkBox.getTag(), checkBox.isChecked());
                }
            };

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Person p = getItem(position);
            if (view == null) {
                view = layOutInflater.inflate(R.layout.participants_layout, null);
                CheckBox checkBox = view.findViewById(R.id.participantCheckBox);
                checkBox.setText(p.lastName);
                checkBox.setTag(p.id);
                checkBox.setOnClickListener(listener);
                checkBox.setChecked(selected.get(persons.get(position).id));
            }   else {
                view.setSelected(selected.get(p.id));
            }

            return view;
        }

/*
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText((Context) mThis, "sdfasfd", Toast.LENGTH_SHORT);
            Log.i("sdfasdfasdf", "asdaSCASDC");
            selected.put(getItem(i).id, ((CheckBox)view).isSelected());
        }*/
    }

}
