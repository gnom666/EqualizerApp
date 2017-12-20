package com.example.myapplication.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

public class Contacts extends AppCompatActivity {

    Person person;
    String personJson;
    ArrayList<Person> contacts;

    Intent me;
    ObjectMapper mapper;
    AbsListView.OnScrollListener onScrollListener;
    FloatingActionButton fab;
    LayoutInflater layOutInflater;
    ListView contactsListView;
    ContactsListAdapter contactsListAdapter;
    Object mThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        onScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && firstVisibleItem > 0) {
                    fab.setVisibility(View.INVISIBLE);
                }   else {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        };

        init();
    }



    private void init() {

        mThis = this;
        me = getIntent();
        personJson = me.getStringExtra("person");

        mapper = new ObjectMapper();
        try {
            person = mapper.readValue(personJson, Person.class);
        }   catch (IOException e) {
            e.printStackTrace();
        }

        if (person == null) {
            finish();
        }

        Toolbar tb = findViewById(R.id.contactsToolbar);
        tb.setTitle("Contacts");
        tb.setSubtitle(person.firstName + " " + person.lastName);

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

                                setContactsListView();

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

    public void forceContactsListView () {
        contactsListView = findViewById(R.id.detailedEventParticipantsListView);

        contactsListAdapter = new ContactsListAdapter((Context) mThis, 0, contacts);

        contactsListView.setAdapter(contactsListAdapter);
    }

    public void setContactsListView () {
        if (contactsListView == null) {
            contactsListView = findViewById(R.id.contactsListView);
        }
        if (contactsListAdapter == null) {
            contactsListAdapter = new ContactsListAdapter((Context) mThis, 0, contacts);
            contactsListView.setAdapter(contactsListAdapter);
        }
    }

    public class ContactsListAdapter extends ArrayAdapter<Person> {

        View.OnClickListener listener;

        public ContactsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Person> contacts) {
            super(context, resource, contacts);

            if (layOutInflater == null)
                layOutInflater = LayoutInflater.from(Contacts.this);

            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long pId = (long)((ImageButton) view).getTag();
                    Toast.makeText(getApplicationContext(), "deleting user with id: " + pId, Toast.LENGTH_SHORT).show();
                }
            };
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Person p = getItem(position);
            if (view == null) {
                view = layOutInflater.inflate(R.layout.contacts_layout, null);

                TextView name = view.findViewById(R.id.contactNameTextView);
                name.setText(p.firstName + " " + p.lastName + " (" + p.numpers + ")");

                ImageButton delete = view.findViewById(R.id.contactDeleteImageButton);
                delete.setTag(p.id);
                delete.setOnClickListener(listener);
            }
            return view;
        }

    }

}
