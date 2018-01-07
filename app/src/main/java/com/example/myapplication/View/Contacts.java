package com.example.myapplication.View;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;

public class Contacts extends AppCompatActivity {

    Person person;
    String personJson;
    ArrayList<Person> contacts;
    String addEmail;

    Intent me;
    ObjectMapper mapper;
    AbsListView.OnScrollListener onScrollListener;
    FloatingActionButton fab;
    LayoutInflater layOutInflater;
    ListView contactsListView;
    ContactsListAdapter contactsListAdapter;
    Object mThis;
    Dialog addContactDialog;
    AlertDialog.Builder addContactDialogBuilder;
    AlertDialog addContactAlertDialog;
    AlertDialog.Builder confirmationDialogBuilder;
    AlertDialog confirmationAlertDialog;

    CountDownTimer timer;

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
                showContactDialog();
            }
        });

        onScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 1) {
                    fab.setVisibility(View.INVISIBLE);
                }   else {
                    startTimer(2000);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        };

        init ();

    }

    private void startTimer(long time){
        if (timer != null)
            timer.cancel();

        timer = new CountDownTimer(time, time){
            public void onTick(long millisUntilDone){

            }

            public void onFinish() {
                fab.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void showContactDialog() {

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View addContactView = li.inflate(R.layout.contact_add_layout, null);

        EditText email = addContactView.findViewById(R.id.contactEmailEditText);
        email.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);

        Button dialogCancelButton = addContactView.findViewById(R.id.genericCancelButton);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //addContactDialog.dismiss();
                addContactAlertDialog.cancel();
            }
        });

        Button dialogAddButton = addContactView.findViewById(R.id.genericAddlButton);
        dialogAddButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText email = addContactAlertDialog.findViewById(R.id.contactEmailEditText);
                //EditText email = addContactDialog.findViewById(R.id.contactEmailEditText);

                addEmail = email.getText().toString().trim();
                if (email != null && !addEmail.isEmpty()){
                    addContact();
                }
            }
        });

        addContactDialogBuilder = new AlertDialog.Builder((Context)mThis);
        addContactDialogBuilder.setView(addContactView);
        addContactDialogBuilder.setCancelable(false);
        addContactDialogBuilder.setTitle("Contact email");

        addContactAlertDialog = addContactDialogBuilder.create();
        addContactAlertDialog.show();

        /*addContactDialog = new Dialog((Context) mThis);
        addContactDialog.setContentView(addContactView);
        addContactDialog.show();*/


    }

    private void showConfirmationDialog(final long pId) {

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View confirmationView = li.inflate(R.layout.confirmation_layout, null);

        TextView question = confirmationView.findViewById(R.id.confirmationTextView);
        question.setText("Sure?");

        Button dialogNoButton = confirmationView.findViewById(R.id.genericNoButton);
        dialogNoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                confirmationAlertDialog.cancel();
            }
        });

        Button dialogYesButton = confirmationView.findViewById(R.id.genericYeslButton);
        dialogYesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                removeContact(pId);
                confirmationAlertDialog.cancel();
            }
        });

        confirmationDialogBuilder = new AlertDialog.Builder((Context)mThis);
        confirmationDialogBuilder.setView(confirmationView);
        confirmationDialogBuilder.setCancelable(false);
        confirmationDialogBuilder.setTitle("");

        confirmationAlertDialog = confirmationDialogBuilder.create();
        confirmationAlertDialog.show();

        /*addContactDialog = new Dialog((Context) mThis);
        addContactDialog.setContentView(addContactView);
        addContactDialog.show();*/


    }

    private void addContact() {
        PersonServices personServices = new PersonServices();
        personServices.setFriendByEmail(this, person.id, addEmail,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            Person newPerson = mapper.readValue(response, Person.class);

                            if (newPerson != null) {
                                if (newPerson.error == null) {

                                    setContactsList(person.id, true);

                                    addContactAlertDialog.cancel();
                                    //addContactDialog.dismiss();

                                }   else {
                                    Toast.makeText(getBaseContext(), newPerson.error.description, Toast.LENGTH_SHORT).show();
                                }

                            }   else {
                                Toast.makeText(getBaseContext(), "Unknown error", Toast.LENGTH_SHORT).show();
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


    private void removeContact(long toUnset) {
        PersonServices personServices = new PersonServices();
        personServices.unsetFriends(this, person.id, toUnset,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            ArrayList<Person> result = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Person.class));

                            if (result != null && result.size() == 2) {
                                if (result.get(0).error == null && result.get(1).error == null) {

                                    setContactsList(person.id, true);

                                    Toast.makeText(getBaseContext(), result.get(1).lastName + " unset as contact", Toast.LENGTH_SHORT).show();

                                }   else {
                                    if (result.get(0).error != null) {
                                        Toast.makeText(getBaseContext(), result.get(0).error.description, Toast.LENGTH_SHORT).show();
                                    }
                                    if (result.get(1).error != null) {
                                        Toast.makeText(getBaseContext(), result.get(0).error.description, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }   else {
                                Toast.makeText(getBaseContext(), "Unknown error", Toast.LENGTH_SHORT).show();
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

        setContactsList(person.id, false);
    }



    public void setContactsList (final long pId, final boolean force) {
        PersonServices personServices = new PersonServices();

        personServices.contacts(this, pId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            contacts = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Person.class));
                            if (contacts != null) {

                                if (!force) setContactsListView();
                                else forceContactsListView();

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
        contactsListView = findViewById(R.id.contactsListView);

        contactsListAdapter = new ContactsListAdapter((Context) mThis, 0, contacts);

        contactsListView.setAdapter(contactsListAdapter);

        contactsListView.setOnScrollListener(onScrollListener);
    }

    public void setContactsListView () {
        if (contactsListView == null) {
            contactsListView = findViewById(R.id.contactsListView);
        }
        if (contactsListAdapter == null) {
            contactsListAdapter = new ContactsListAdapter((Context) mThis, 0, contacts);
            contactsListView.setAdapter(contactsListAdapter);
            contactsListView.setOnScrollListener(onScrollListener);
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

                    showConfirmationDialog(pId);
                    //removeContact(pId);
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
