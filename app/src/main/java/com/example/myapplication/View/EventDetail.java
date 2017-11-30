package com.example.myapplication.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapplication.Controller.EventServices;
import com.example.myapplication.Controller.PaymentServices;
import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Error;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.Payment;
import com.example.myapplication.Model.Person;
import com.example.myapplication.Model.Utils;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class EventDetail extends AppCompatActivity {

    Person person;
    ArrayList<Person> contacts;
    LinkedHashMap<Long, Boolean> selected;
    Event event;
    boolean notOwner;
    ArrayList<Payment> payments;

    ListView participantsListView;
    ParticipantsListAdapter participantsListAdapter;
    EditText name;
    EditText description;
    EditText date;

    Intent me;
    ObjectMapper mapper;
    Object mThis;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (notOwner) {
            getMenuInflater().inflate(R.menu.event_detail_notowner_menu, menu);
        }   else {
            getMenuInflater().inflate(R.menu.event_detail_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.calculateItem:
                Log.i("item", "calculate");
                setPayments(event.id);
                break;
            case R.id.addTaskItem:
                Log.i("item", "add task");
                break;
            case R.id.removeItem:
                Log.i("item", "remove");
                break;
            case R.id.getOutItem:
                Log.i("item", "get out");
                break;
            default:
                Log.i("item", "unknown");
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        init ();
    }

    private void init() {

        mThis = this;
        me = getIntent();
        String personJson = me.getStringExtra("person");
        String eventJson = me.getStringExtra("event");
        selected = new LinkedHashMap<>();

        mapper = new ObjectMapper();
        try {
            person = mapper.readValue(personJson, Person.class);
            event = mapper.readValue(eventJson, Event.class);
        }   catch (IOException e) {
            e.printStackTrace();
        }

        if (person.id != event.owner) {
            notOwner = true;
        }

        name = findViewById(R.id.detailedEventNameEditText);
        description = findViewById(R.id.detailedEventDescriptionEditText);
        date = findViewById(R.id.detailedEventDateEditText);

        name.setText(event.name);
        description.setText(event.description);
        date.setText(event.date);

        if (notOwner) {
            name.setFocusable(false);
            description.setFocusable(false);
            date.setFocusable(false);
        }

        setContactsList(person.id);
    }

    public void setContactsList (final long pId) {
        PersonServices personServices = new PersonServices();

        personServices.contacts(this, pId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            contacts = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Person.class));
                            if (contacts != null) {
                                for (int i = 0; i < contacts.size(); i++) {
                                    selected.put(contacts.get(i).id, false);
                                }

                                setParticipantsList(event.id);

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

    public void setParticipantsList (final long aId) {
        PersonServices personServices = new PersonServices();

        personServices.participants(this, aId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            ArrayList<Person> persons = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Person.class));
                            if (persons != null) {
                                for (int i = 0; i < persons.size(); i++) {
                                    if (persons.get(i).id != person.id) {
                                        selected.put(persons.get(i).id, true);
                                    }
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

    public void setPayments (final long aId) {

        PaymentServices paymentServices = new PaymentServices();

        paymentServices.testPayments(this, aId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            payments = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Payment.class));
                            if (payments != null) {

                                for (Payment p : payments) {
                                    Person from = (Person )Utils.byId(contacts, p.from, Person.class);
                                    if (from == null) from = person;
                                    Person to = (Person) Utils.byId(contacts, p.to, Person.class);
                                    if (to == null) to = person;
                                    Log.i("payment: ", "from " + from.lastName + " to " + to.lastName + " (" + p.ammount + " )");
                                }
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
        participantsListView = findViewById(R.id.detailedEventParticipantsListView);
        participantsListAdapter = new ParticipantsListAdapter((Context) mThis, 0, contacts);
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
                checkBox.setChecked(selected.get(contacts.get(position).id));
                if (notOwner) {
                    checkBox.setFocusable(false);
                    checkBox.setClickable(false);
                }
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

    public void updateEvent () throws JsonProcessingException, JSONException {
        EventServices eventServices = new EventServices();

        eventServices.modifyActivity(this, "PersonOut",
                new JSONObject(mapper.writeValueAsString(event)),
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {

                        if (!response.equals("OK")) {
                            try {
                                Error error = mapper.readValue(response, Error.class);
                                Toast.makeText(getApplicationContext(), error.description, Toast.LENGTH_SHORT).show();
                            }   catch (IOException e) {
                                e.printStackTrace();
                            }
                        }   else {
                            Toast.makeText(getApplicationContext(), "Event updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        });
    }

    public void onOkClick (View view) {
        //event = new Event();
        if (!notOwner) {
            event.owner = person.id;
            event.participants.clear();
            event.participants.add(person.id);
            event.name = name.getText().toString();
            event.description = description.getText().toString();
            event.date = date.getText().toString();
            for (Person p : contacts) {
                if (selected.get(p.id)) {
                    event.participants.add(p.id);
                }
            }
            try {
                Log.i("Event", mapper.writeValueAsString(event));
                updateEvent();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setResult(RESULT_OK, me);
        finish();
    }

    public void onCancelClick (View view) {
        setResult(RESULT_CANCELED, me);
        finish();
    }

}
