package com.example.myapplication.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class EventDetailed extends AppCompatActivity {

    Person person;
    String personJson;
    ArrayList<Person> contacts;
    ArrayList<Person> participants;
    ArrayList<Person> originals;
    LinkedHashMap<Long, Boolean> selected;
    Event event;
    String eventJson;
    boolean isOwner = false;
    ArrayList<Payment> payments;
    ArrayList<Task> tasks;

    ListView participantsListView;
    ParticipantsListAdapter participantsListAdapter;
    ListView tasksListView;
    TasksListAdapter tasksListAdapter;
    ListView paymentsListView;
    PaymentsListAdapter paymentsListAdapter;
    LinearLayout buttonsLayout;
    HashMap<Long, CheckBox> participantsCheckboxes;
    AbsListView.OnScrollListener onScrollListener;
    AbsListView.OnScrollListener onScrollParticipantsListener;

    Intent me;
    ObjectMapper mapper;
    Object mThis;
    FloatingActionButton fab;
    CountDownTimer timer;

    TabTasks tabTasks;
    TabParticipants tabParticipants;
    TabPayments tabPayments;
    LayoutInflater layOutInflater;

    final int TASKS_POSITION = 0;
    final int PARTICIPANTS_POSITION = 1;
    final int PAYMENTS_POSITION = 2;
    int POSITION = 0;
    private int eventDetailCode = 10;
    private int taskAddCode = 20;
    private int taskEditCode = 30;
    private int taskDeleteCode = 40;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.eventDetailedToolbar);
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


        fab = findViewById(R.id.detailedEventAddTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

        onScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 1) {
                    fab.setVisibility(View.INVISIBLE);
                }   else {
                    startFabsTimer(2000);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                Log.i("POSITION", String.valueOf(POSITION));
                int lastItem = firstVisibleItem + visibleItemCount;
                if (POSITION != TASKS_POSITION || (lastItem == totalItemCount && firstVisibleItem > 0)) {
                    fab.setVisibility(View.INVISIBLE);
                }   else {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        };

        onScrollParticipantsListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 1) {
                    setEditLayoutVisibility(false);
                }   else {
                    startButtonsTimer(2000);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                Log.i("POSITION", String.valueOf(POSITION));
                int lastItem = firstVisibleItem + visibleItemCount;
                if (POSITION != TASKS_POSITION || (lastItem == totalItemCount && firstVisibleItem > 0)) {
                    fab.setVisibility(View.INVISIBLE);
                }   else {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        };

        buttonsLayout = findViewById(R.id.buttonsLinearLayout);
        if (buttonsLayout != null) {
            setEditLayoutVisibility(false);
            setEditLayoutEnabled(false);
        }

        init ();

    }

    private void startFabsTimer(long time){
        if (timer != null)
            timer.cancel();

        timer = new CountDownTimer(time, time){
            public void onTick(long millisUntilDone){

            }

            public void onFinish() {
                if (POSITION == TASKS_POSITION)
                    fab.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void startButtonsTimer(long time){
        if (timer != null)
            timer.cancel();

        timer = new CountDownTimer(time, time){
            public void onTick(long millisUntilDone){

            }

            public void onFinish() {
                if (POSITION == PARTICIPANTS_POSITION)
                    setEditLayoutVisibility(true);
            }
        }.start();
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
        POSITION = position;
        switch (position) {
            case TASKS_POSITION:
                setEditLayoutVisibility(false);
                //setTasksListView();
                setFabVisibility(true);
                break;
            case PARTICIPANTS_POSITION:
                if (isOwner)
                    setEditLayoutVisibility(true);
                //setParticipantsListView();
                if (participantsListAdapter != null)
                    participantsListAdapter.notifyDataSetChanged();
                setFabVisibility(false);
                break;
            case PAYMENTS_POSITION:
                setEditLayoutVisibility(false);
                //setTasksListView();
                setFabVisibility(false);
                break;
            default:
                break;
        }

    }






    private void init() {

        mThis = this;
        me = getIntent();
        personJson = me.getStringExtra("person");
        eventJson = me.getStringExtra("event");
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

        if (person == null || event == null) {
            finish();
        }

        Toolbar tb = findViewById(R.id.eventDetailedToolbar);
        tb.setTitle(event.name);
        tb.setSubtitle(event.description + " (" + event.date + ")");

        if (person.id == event.owner) {
            isOwner = true;
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

                                setParticipantsList(event.id, false);

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

    public void setParticipantsList (final long aId, final boolean force) {
        PersonServices personServices = new PersonServices();

        personServices.participants(this, aId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            participants = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Person.class));
                            originals = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Person.class));

                            if (participants != null) {
                                for (int i = 0; i < participants.size(); i++) {
                                    if (participants.get(i).id != person.id) {
                                        selected.put(participants.get(i).id, true);
                                    }
                                }

                                setTasks(event.id, force);
                                setPayments(event.id, force);

                                if (!force) setParticipantsListView();
                                else forceParticipantsListView();

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

    public void setPayments (final long aId, final boolean force) {

        PaymentServices paymentServices = new PaymentServices();

        paymentServices.testPayments(this, aId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            payments = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Payment.class));
                            if (payments != null) {

                                for (Payment p : payments) {
                                    //Person from = (Person ) Utils.byId(participants, p.from, Person.class);
                                    Person from = findPerson(participants, p.from);
                                    if (from == null) from = person;
                                    //Person to = (Person) Utils.byId(participants, p.to, Person.class);
                                    Person to = findPerson(participants, p.to);
                                    if (to == null) to = person;
                                    Log.i("payment: ", "from " + from.lastName + " to " + to.lastName + " (" + p.amount + " )");
                                }

                                if (!force) setPaymentsListView();
                                else forcePaymentsListView();

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

    public void setTasks (final long aId, final boolean force) {

        TasksServices tasksServices = new TasksServices();

        tasksServices.tasksByAct(this, aId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            tasks = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Task.class));
                            if (tasks != null) {
                                for (Task t : tasks) {
                                    //Person owner = (Person ) Utils.byId(participants, t.owner, Person.class);
                                    Person owner = findPerson(participants, t.owner);
                                    if (owner == null) owner = person;
                                    Log.i("task", t.name + " by " + owner.lastName);
                                }

                                if (!force) setTasksListView();
                                else forceTasksListView();

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

    public void deleteTask (final long tId) {

        TasksServices tasksServices = new TasksServices();

        tasksServices.deleteTask(this, tId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            Task task = mapper.readValue(response, Task.class);
                            if (task != null) {
                                if (task.error == null) {

                                    Toast.makeText(getApplicationContext(), "Task " + task.name + " deleted", Toast.LENGTH_SHORT).show();

                                    setTasks(event.id, true);
                                    setPayments(event.id, true);

                                    setResult(RESULT_OK, me);
                                }   else {
                                    Toast.makeText(getApplicationContext(), task.error.description, Toast.LENGTH_SHORT).show();
                                }
                            }   else {
                                Toast.makeText(getApplicationContext(), "null Task returned", Toast.LENGTH_SHORT).show();
                            }

                        }   catch (IOException e) {
                            e.printStackTrace();
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

    public void updateEvent (Event toUpdate) throws JsonProcessingException, JSONException {
        EventServices eventServices = new EventServices();

        eventServices.modifyActivity(this, "PersonOut",
                new JSONObject(mapper.writeValueAsString(toUpdate)),
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {


                        try {
                            Event updatedEvent = mapper.readValue(response, Event.class);

                            if (updatedEvent != null) {
                                if (updatedEvent.error == null) {
                                    event = updatedEvent;
                                    me.putExtra("event", mapper.writeValueAsString(updatedEvent));
                                }   else {
                                    Toast.makeText(getApplicationContext(), updatedEvent.error.description, Toast.LENGTH_SHORT).show();
                                }
                            }   else {
                                Toast.makeText(getApplicationContext(), "null Event returned", Toast.LENGTH_SHORT).show();
                            }

                        }   catch (IOException e) {
                            e.printStackTrace();
                        }

                        participants.clear();
                        originals.clear();
                        Iterator it = selected.keySet().iterator();
                        while (it.hasNext()) selected.put((Long) it.next(), false);
                        setParticipantsList(event.id, true);
                        forceBackParticipantsListView();
                        participantsListAdapter.notifyDataSetChanged();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteEvent () {

        EventServices eventServices = new EventServices();

        eventServices.deleteActivity(this, event.id,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            Event deletedEvent = mapper.readValue(response, Event.class);
                            if (deletedEvent != null) {
                                if (deletedEvent.error == null) {

                                    Toast.makeText(getApplicationContext(), "Event " + deletedEvent.name + " deleted", Toast.LENGTH_SHORT).show();

                                    setResult(RESULT_OK, me);
                                    finish();

                                }   else {
                                    Toast.makeText(getApplicationContext(), deletedEvent.error.description, Toast.LENGTH_SHORT).show();
                                }
                            }   else {
                                Toast.makeText(getApplicationContext(), "null Event returned", Toast.LENGTH_SHORT).show();
                            }

                        }   catch (IOException e) {
                            e.printStackTrace();
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

        int id = item.getItemId();

        switch (id) {
            case R.id.action_edit:
                if (!isOwner) {
                    Toast.makeText(this, R.string.owner_can_edit, Toast.LENGTH_SHORT).show();
                }   else {
                    editEvent();
                }
                return true;
            case R.id.action_calculate:
                Toast.makeText(this, "calculate", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_delete:
                if (!isOwner) {
                    Toast.makeText(this, R.string.owner_can_delete, Toast.LENGTH_SHORT).show();
                }   else {
                    deleteEvent();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setFabVisibility (boolean visible) {
        if (visible) {
            fab.setVisibility(View.VISIBLE);
        }   else {
            fab.setVisibility(View.INVISIBLE);
        }
    }

    public void setEditLayoutVisibility (boolean visible) {
        if (visible) {
            buttonsLayout.setVisibility(View.VISIBLE);
        }   else {
            buttonsLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void setEditLayoutEnabled (boolean enabled) {
        Button ok = findViewById(R.id.editParticipantsOkButton);
        Button cancel = findViewById(R.id.editParticipantsCancelButton);
        if (ok != null && cancel != null) {
            ok.setEnabled(enabled);
            cancel.setEnabled(enabled);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == eventDetailCode && resultCode == RESULT_OK) {
            eventJson = intent.getStringExtra("event");
            try {
                event = mapper.readValue(eventJson, Event.class);
            }   catch (IOException e) {
                e.printStackTrace();
            }

            setResult(RESULT_OK, me);

            Toolbar tb = findViewById(R.id.eventDetailedToolbar);
            tb.setTitle(event.name);
            tb.setSubtitle(event.description + " (" + event.date + ")");
            
        }
        if(requestCode == eventDetailCode && resultCode == RESULT_CANCELED) {

        }

        if(requestCode == taskAddCode && resultCode == RESULT_OK) {
            eventJson = intent.getStringExtra("task");
            try {
                Task task = mapper.readValue(eventJson, Task.class);
                tasks.add(task);
            }   catch (IOException e) {
                e.printStackTrace();
            }

            setTasks(event.id, true);
            setPayments(event.id, true);

            setResult(RESULT_OK, me);

        }
        if(requestCode == taskAddCode && resultCode == RESULT_CANCELED) {

        }

        if(requestCode == taskEditCode && resultCode == RESULT_OK) {
            eventJson = intent.getStringExtra("task");
            try {
                Task task = mapper.readValue(eventJson, Task.class);
                Task toChange = Utils.byId(tasks, task.id, Task.class);
                toChange = new Task(task);
            }   catch (IOException e) {
                e.printStackTrace();
            }

            setTasks(event.id, true);
            setPayments(event.id, true);

            setResult(RESULT_OK, me);

        }
        if(requestCode == taskEditCode && resultCode == RESULT_CANCELED) {

        }
    }




    public void forceBackParticipantsListView () {

        if (originals != null && isOwner) {
            Iterator it = selected.keySet().iterator();
            while (it.hasNext()) {
                selected.put((Long) it.next(), false);
            }
            for (int i = 0; i < originals.size(); i++) {
                if (originals.get(i).id != person.id) {
                    selected.put(originals.get(i).id, true);
                }
            }
            it = participantsCheckboxes.values().iterator();
            while (it.hasNext()) {
                ((CheckBox)it.next()).setChecked(false);
            }
            for (int i = 0; i < originals.size(); i++) {
                if (participantsCheckboxes.containsKey((Long) originals.get(i).id)) {
                    participantsCheckboxes.get((Long) originals.get(i).id).setChecked(true);
                }
            }
        }

        participantsListAdapter.notifyDataSetChanged();
    }

    public void forceParticipantsListView () {
        participantsListView = findViewById(R.id.detailedEventParticipantsListView);

        if (!isOwner) {
            participantsListAdapter = new ParticipantsListAdapter((Context) mThis, 0, participants);
        }   else {
            participantsListAdapter = new ParticipantsListAdapter((Context) mThis, 0, contacts);
        }
        participantsListView.setAdapter(participantsListAdapter);

        participantsListView.setOnScrollListener(onScrollParticipantsListener);
    }

    public void setParticipantsListView () {
        if (participantsListView == null) {
            participantsListView = findViewById(R.id.detailedEventParticipantsListView);
        }
        if (participantsListAdapter == null) {
            if (!isOwner) {
                participantsListAdapter = new ParticipantsListAdapter((Context) mThis, 0, participants);
            }   else {
                participantsListAdapter = new ParticipantsListAdapter((Context) mThis, 0, contacts);
            }
            participantsListView.setAdapter(participantsListAdapter);
        }

        participantsListView.setOnScrollListener(onScrollParticipantsListener);
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
                    setEditLayoutEnabled(true);
                }
            };

            participantsCheckboxes = new LinkedHashMap<>();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Person p = getItem(position);
            if (view == null) {
                view = layOutInflater.inflate(R.layout.participants_layout, null);
                CheckBox checkBox = view.findViewById(R.id.participantCheckBox);
                checkBox.setText(p.firstName + " " + p.lastName + " (" + p.numpers + ")");
                checkBox.setTag(p.id);
                checkBox.setOnClickListener(listener);
                if (!isOwner) {
                    checkBox.setChecked(true);
                    checkBox.setFocusable(false);
                    checkBox.setClickable(false);
                }   else {
                    checkBox.setChecked(selected.get(contacts.get(position).id));
                }
                participantsCheckboxes.put(p.id, checkBox);
            }   else {
                CheckBox checkBox = view.findViewById(R.id.participantCheckBox);
                boolean checked = (selected.get(p.id) != null)? selected.get(p.id): true;
                checkBox.setSelected(checked);
                participantsCheckboxes.put(p.id, checkBox);
            }

            return view;
        }

    }



    public void forceTasksListView () {

        tasksListView = findViewById(R.id.detailedEventTasksListView);

        tasksListAdapter = new TasksListAdapter((Context) mThis, 0, tasks);
        tasksListView.setAdapter(tasksListAdapter);

        tasksListView.setOnScrollListener(onScrollListener);

    }

    public void setTasksListView () {
        if (tasksListView == null) {
            tasksListView = findViewById(R.id.detailedEventTasksListView);
        }
        if (tasksListAdapter == null) {
            tasksListAdapter = new TasksListAdapter((Context) mThis, 0, tasks);
            tasksListView.setAdapter(tasksListAdapter);
        }

        tasksListView.setOnScrollListener(onScrollListener);
    }

    public class TasksListAdapter extends ArrayAdapter<Task> {

        private ArrayList<Task> tasks;
        private Context context;
        View.OnClickListener deleteListener;
        View.OnClickListener editListener;

        public TasksListAdapter(@NonNull final Context context, int resource, @NonNull final ArrayList<Task> tasks) {
            super(context, resource, tasks);
            this.tasks = tasks;

            this.context = context;
            if (layOutInflater == null)
                layOutInflater = LayoutInflater.from(EventDetailed.this);

            editListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editTask((long) view.getTag());
                }
            };

            deleteListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteTask((long) view.getTag());
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
            Person ownerPerson = findPerson(participants, t.owner);
            if (ownerPerson == null) ownerPerson = person;
            owner.setText(ownerPerson.firstName + " " + ownerPerson.lastName);
            owner.setTag(ownerPerson.id);

            TextView amount = view.findViewById(R.id.taskTabAmountTextView);
            amount.setText(Utils.amount2string(t.amount));

            ImageButton edit = view.findViewById(R.id.taskTabEditTabImageButton);
            edit.setOnClickListener(editListener);
            edit.setTag(t.id);

            ImageButton delete = view.findViewById(R.id.taskTabDeleteTabImageButton);
            delete.setOnClickListener(deleteListener);
            delete.setTag(t.id);

            if (!isOwner && t.owner != person.id) {
                edit.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
            }

            return view;
        }

    }




    public ArrayList<Payment> reducePayments (ArrayList<Payment> ps) {
        ArrayList<Payment> newPs = new ArrayList<>();
        for (Payment p : ps) {
            if (p.from == person.id || p.to == person.id) {
                newPs.add(p);
            }
        }
        return newPs;
    }

    public void forcePaymentsListView () {
        paymentsListView = findViewById(R.id.detailedEventPaymentsListView);
        if (!isOwner) {
            paymentsListAdapter = new PaymentsListAdapter((Context) mThis, 0, reducePayments(payments));
        }   else {
            paymentsListAdapter = new PaymentsListAdapter((Context) mThis, 0, payments);
        }
        paymentsListView.setAdapter(paymentsListAdapter);
    }

    public void setPaymentsListView () {
        if (paymentsListView == null) {
            paymentsListView = findViewById(R.id.detailedEventPaymentsListView);
        }
        if (paymentsListAdapter == null) {
            if (!isOwner) {
                paymentsListAdapter = new PaymentsListAdapter((Context) mThis, 0, reducePayments(payments));
            }   else {
                paymentsListAdapter = new PaymentsListAdapter((Context) mThis, 0, payments);
            }
            paymentsListView.setAdapter(paymentsListAdapter);
        }

    }

    public class PaymentsListAdapter extends ArrayAdapter<Payment> {

        private ArrayList<Payment> payments;
        private Context context;
        View.OnClickListener deleteListener;
        View.OnClickListener editListener;

        public PaymentsListAdapter(@NonNull final Context context, int resource, @NonNull ArrayList<Payment> payments) {
            super(context, resource, payments);
            this.payments = payments;

            this.context = context;
            if (layOutInflater == null)
                layOutInflater = LayoutInflater.from(EventDetailed.this);

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Payment p = getItem(position);
            boolean mustPay = true;

            if (p != null && person != null && p.from != person.id) {
                mustPay = false;
            }

            view = layOutInflater.inflate(R.layout.payments_layout, null);
            view.setTag(p.id);

            TextView name = view.findViewById(R.id.paymentNameTextView);
            TextView status = view.findViewById(R.id.paymentStatusTextView);
            TextView amount = view.findViewById(R.id.paymentAmountTextView);
            ImageButton direction = view.findViewById(R.id.paymentDirectionImageButton);

            if (!isOwner) {
                if (mustPay) {
                    Person to = findPerson(contacts, p.to);
                    if (to == null) {
                        to = findPerson(participants, p.to);
                    }
                    name.setText(to.firstName + " " + to.lastName);
                }   else {
                    Person from = findPerson(contacts, p.from);
                    if (from == null) {
                        from = findPerson(participants, p.from);
                    }
                    name.setText(from.firstName + " " + from.lastName);
                }

                status.setText(p.status.toString());

                amount.setText(Utils.amount2string(p.amount));

                if (!mustPay) {
                    direction.setImageResource(R.drawable.left_shadow_green);
                }
            }   else {
                String text = "";
                String sep = " - ";

                direction.setImageResource(R.drawable.right_shadow_blue);

                Person from = findPerson(participants, p.from);
                if (from.id == person.id) {
                    direction.setImageResource(R.drawable.right_shadow_red);
                    sep = "";
                }   else {
                    text += from.firstName + " " + from.lastName;
                }

                Person to = findPerson(participants, p.to);
                if (to.id == person.id) {
                    direction.setImageResource(R.drawable.left_shadow_green );
                    sep = "";
                }   else {
                    text += sep + to.firstName + " " + to.lastName;
                }

                name.setText(text);

                status.setText(p.status.toString());

                amount.setText(Utils.amount2string(p.amount));

            }

            return view;
        }

    }

    public void onOkClick (View view) {
        Event newEvent = new Event(event);
        newEvent.participants.clear();
        Iterator it = selected.keySet().iterator();
        while (it.hasNext()) {
            Long id = (Long) it.next();
            if (selected.get(id)) {
                newEvent.participants.add(id);
            }
        }
        newEvent.participants.add(person.id);
        try {
            updateEvent(newEvent);
        }   catch (JsonProcessingException e) {
            e.printStackTrace();
        }   catch (JSONException e) {
            e.printStackTrace();
        }
        setEditLayoutEnabled(false);
    }

    public void onCancelClick (View view) {
        forceBackParticipantsListView();
        setEditLayoutEnabled(false);
    }

    public void editEvent () {
        Intent eventDetailIntent = new Intent(getApplicationContext(), EventDetail.class);
        try {
            eventDetailIntent.putExtra("person", mapper.writeValueAsString(person));
            eventDetailIntent.putExtra("event", mapper.writeValueAsString(event));
        }   catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        startActivityForResult(eventDetailIntent, eventDetailCode);
    }

    public void addTask () {
        Intent taskAddIntent = new Intent(getApplicationContext(), TaskAdd.class);
        try {
            taskAddIntent.putExtra("person", mapper.writeValueAsString(person));
            taskAddIntent.putExtra("event", mapper.writeValueAsString(event));
        }   catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        startActivityForResult(taskAddIntent, taskAddCode);
    }

    public void editTask (long id) {
        Intent taskEditIntent = new Intent(getApplicationContext(), TaskDetail.class);
        try {
            taskEditIntent.putExtra("person", mapper.writeValueAsString(person));
            taskEditIntent.putExtra("event", mapper.writeValueAsString(event));
            Task task = Utils.byId(tasks, id, Task.class);
            taskEditIntent.putExtra("task", mapper.writeValueAsString(task));
        }   catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        startActivityForResult(taskEditIntent, taskEditCode);
    }

    public Person findPerson (ArrayList<Person> list, long id) {
        return Utils.byId(list, id, Person.class);
    }
}
