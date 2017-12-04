package com.example.myapplication.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapplication.Controller.EventServices;
import com.example.myapplication.Controller.PaymentServices;
import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Controller.TasksServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Error;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.Payment;
import com.example.myapplication.Model.Person;
import com.example.myapplication.Model.Task;
import com.example.myapplication.Model.Utils;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class EventDetailed extends AppCompatActivity {

    Person person;
    ArrayList<Person> contacts;
    LinkedHashMap<Long, Boolean> selected;
    Event event;
    boolean notOwner;
    ArrayList<Payment> payments;
    ArrayList<Task> tasks;

    ListView participantsListView;
    ParticipantsListAdapter participantsListAdapter;
    ListView tasksListView;
    TasksListAdapter tasksListAdapter;

    Intent me;
    ObjectMapper mapper;
    Object mThis;
    FloatingActionButton fab;

    TabTasks tabTasks;
    TabParticipants tabParticipants;
    TabPayments tabPayments;
    LayoutInflater layOutInflater;

    final int TASKS_POSITION = 0;
    final int PARTICIPANTS_POSITION = 1;
    final int PAYMENTS_POSITION = 2;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detailed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                super.onTabSelected(tab);

                tabSelected(tab);

            }
        });


        fab = (FloatingActionButton) findViewById(R.id.detailedEventAddTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        init ();

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // return the current tab
            Log.i("position", String.valueOf(position));
            switch (position) {
                case TASKS_POSITION:
                    if (tabTasks != null)
                        return tabTasks;
                    else
                        return new TabTasks();
                case PARTICIPANTS_POSITION:
                    if (tabParticipants != null)
                        return tabParticipants;
                    else
                        return new TabParticipants();
                case PAYMENTS_POSITION:
                    if (tabPayments != null)
                        return tabPayments;
                    else
                        return new TabPayments();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public void tabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        if (position == TASKS_POSITION) {
            setFabVisibility(true);
        }   else {
            setFabVisibility(false);
        }

        switch (position) {
            case TASKS_POSITION:
                setTasksListView();
                break;
            case PARTICIPANTS_POSITION:
                setParticipantsListView();
                break;
            case PAYMENTS_POSITION:
                //setTasksListView();
                break;
            default:
                break;
        }

    }





    private void init() {

        mThis = this;
        me = getIntent();
        String personJson = me.getStringExtra("person");
        String eventJson = me.getStringExtra("event");
        selected = new LinkedHashMap<>();

        tabTasks = new TabTasks();
        tabParticipants = new TabParticipants();
        tabPayments = new TabPayments();

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
                                setTasks(event.id);

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

                                setParticipantsListView();

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
                                    Person from = (Person ) Utils.byId(contacts, p.from, Person.class);
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

    public void setTasks (final long aId) {

        TasksServices tasksServices = new TasksServices();

        tasksServices.tasksByAct(this, aId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            tasks = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Task.class));
                            if (tasks != null) {
                                for (Task t : tasks) {
                                    Person owner = (Person ) Utils.byId(contacts, t.owner, Person.class);
                                    if (owner == null) owner = person;
                                    Log.i("task", t.name + " by " + owner.lastName);
                                }

                                setTasksListView();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_detailed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFabVisibility (boolean visible) {
        if (visible) {
            fab.setVisibility(View.VISIBLE);
        }   else {
            fab.setVisibility(View.INVISIBLE);
        }
    }



    public void setParticipantsListView () {
        if (participantsListView == null) {
            participantsListView = findViewById(R.id.detailedEventParticipantsListView);
        }
        if (participantsListAdapter == null) {
            participantsListAdapter = new ParticipantsListAdapter((Context) mThis, 0, contacts);
            participantsListView.setAdapter(participantsListAdapter);
        }

    }

    public class ParticipantsListAdapter extends ArrayAdapter<Person> {

        private ArrayList<Person> participants;
        private Context context;
        View.OnClickListener listener;

        public ParticipantsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Person> participants) {
            super(context, resource, participants);
            this.participants = participants;

            this.context = context;
            if (layOutInflater == null)
                layOutInflater = LayoutInflater.from(EventDetailed.this);

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

    }




    public void setTasksListView () {
        if (tasksListView == null) {
            tasksListView = findViewById(R.id.detailedEventTasksListView);
        }
        if (tasksListAdapter == null) {
            tasksListAdapter = new TasksListAdapter((Context) mThis, 0, tasks);
            tasksListView.setAdapter(tasksListAdapter);
        }

    }

    public class TasksListAdapter extends ArrayAdapter<Task> {

        private ArrayList<Task> tasks;
        private Context context;
        View.OnClickListener deleteListener;
        View.OnClickListener editListener;

        public TasksListAdapter(@NonNull final Context context, int resource, @NonNull ArrayList<Task> tasks) {
            super(context, resource, tasks);
            this.tasks = tasks;

            this.context = context;
            if (layOutInflater == null)
                layOutInflater = LayoutInflater.from(EventDetailed.this);

            editListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "editing " + String.valueOf((long)view.getTag()), Toast.LENGTH_SHORT).show();
                }
            };

            deleteListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "deleting " + String.valueOf((long)view.getTag()), Toast.LENGTH_SHORT).show();
                }
            };
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Task t = getItem(position);

            view = layOutInflater.inflate(R.layout.tasks_tab_layout, null);

            TextView name = view.findViewById(R.id.taskTabNameTextView);
            name.setText(t.name);
            name.setTag(t.id);

            TextView owner = view.findViewById(R.id.taskTabOwnerTextView);
            Person ownerPerson = (Person ) Utils.byId(contacts, t.owner, Person.class);
            if (ownerPerson == null) ownerPerson = person;
            owner.setText(ownerPerson.firstName + " " + ownerPerson.lastName);
            owner.setTag(ownerPerson.id);

            TextView amount = view.findViewById(R.id.taskTabAmmountTextView);
            amount.setText(String.valueOf(t.ammount));

            ImageButton edit = view.findViewById(R.id.taskTabEditTabImageButton);
            edit.setOnClickListener(editListener);
            edit.setTag(t.id);

            ImageButton delete = view.findViewById(R.id.taskTabDeleteTabImageButton);
            delete.setOnClickListener(deleteListener);
            delete.setTag(t.id);

            return view;
        }

    }

}
