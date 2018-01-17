package com.example.myapplication.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.example.myapplication.Controller.EventServices;
import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Controller.TasksServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Controller.google.GoogleHelper;
import com.example.myapplication.Controller.google.GoogleListener;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.Person;
import com.example.myapplication.Model.Task;
import com.example.myapplication.Model.Utils;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserPage extends AppCompatActivity implements GoogleListener {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    ProgressBar progressBar;

    Person person;
    ArrayList<Event> events;
    ArrayList<Pair<Long, ArrayList<Task>>> tasksByAct;
    HashMap<Long, Boolean> loadedTasksByEvents;
    HashMap<Long, Person> persons;

    Intent me;
    ObjectMapper mapper;

    FloatingActionButton fabAdd;
    FloatingActionButton fabContacts;
    FloatingActionButton fabLogout;
    FloatingActionButton fabExpander;
    boolean expanded = false;
    boolean hidden = false;
    Animation fabOpen, fabClose, fabRotateCW, fabRotateACW, fabHide, fabUnHide;

    CountDownTimer timer;

    String personJSON;
    String activitiesJSON;
    private final int eventAddCode = 40;
    private final int eventDetailCode = 50;
    private final int contactsCode = 60;
    private final int readContactsRequest = 62;
    private final int gcontactsCode = 61;

    PersonServices personServices;
    VolleyCallback vc = null;
    Response.ErrorListener el = null;

    GoogleHelper googleHelper;
    GoogleSignInAccount acct;

    Context mthis;
    Activity mThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mthis = getApplicationContext();
        mThis = this;

        setContentView(R.layout.activity_user_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        personServices = new PersonServices();

        initialize();

        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        fabAdd = (FloatingActionButton) findViewById(R.id.fab);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventAddIntent = new Intent(getApplicationContext(), EventAdd.class);
                try {
                    eventAddIntent.putExtra("person", mapper.writeValueAsString(person));
                }   catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                startActivityForResult(eventAddIntent, eventAddCode);
            }
        });

        fabContacts = (FloatingActionButton) findViewById(R.id.fabContacts);
        fabContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent contactsIntent = new Intent(getApplicationContext(), Contacts.class);
                try {
                    contactsIntent.putExtra("person", mapper.writeValueAsString(person));
                }   catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                startActivityForResult(contactsIntent, contactsCode);*/
                checkPermissions();
            }
        });


        fabLogout = findViewById(R.id.logoutButton);
        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleHelper.performSignOut();
                setResult(RESULT_OK, me);
                finish();
            }
        });

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRotateCW = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_cw);
        fabRotateACW = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_acw);
        fabHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide);
        fabUnHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.unhide);

        fabExpander = findViewById(R.id.expander);
        fabExpander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (expanded) {
                closeFabs();
            }   else {
                openFabs();
            }
            expanded = !expanded;
            }
        });

        expanded = false;
        fabAdd.setClickable(false);
        fabContacts.setClickable(false);
        fabLogout.setClickable(false);

        googleHelper = new GoogleHelper(this, this, null);
    }

    /*@Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putCharSequence("Person", personJSON);
        state.putCharSequence("Activities", activitiesJSON);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        personJSON = String.valueOf(state.getCharSequence("Person"));
        activitiesJSON = String.valueOf(state.getCharSequence("Activities"));

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent loginIntent) {
        super.onActivityResult(requestCode, resultCode, loginIntent);
        if(requestCode == eventAddCode && resultCode == RESULT_OK) {
            if (person != null) {
                if (expandableListView != null) expandableListView.setVisibility(View.INVISIBLE);
                initialize();
            }
        }
        if(requestCode == eventDetailCode && resultCode == RESULT_OK) {
            if (person != null) {
                if (expandableListView != null) expandableListView.setVisibility(View.INVISIBLE);
                initialize();
            }
        }
        if(requestCode == eventDetailCode && resultCode == RESULT_CANCELED) {
            if (person != null) {
                if (expandableListView != null) expandableListView.setVisibility(View.INVISIBLE);
                initialize();
            }
        }
        if(requestCode == eventAddCode && resultCode == RESULT_CANCELED) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case readContactsRequest: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && acct != null) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    callNewContacts();

                }   else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    callOldContacts();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void callOldContacts () {
        Intent contactsIntent = new Intent(getApplicationContext(), Contacts.class);
        try {
            contactsIntent.putExtra("person", mapper.writeValueAsString(person));
        }   catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        startActivityForResult(contactsIntent, contactsCode);
    }

    void callNewContacts () {
        Intent contactsIntent = new Intent(getApplicationContext(), GmailContacts.class);
        try {
            contactsIntent.putExtra("person", mapper.writeValueAsString(person));
        }   catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        startActivityForResult(contactsIntent, gcontactsCode);
    }

    public void checkPermissions () {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mThis,
                    android.Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.i("checkPerm", "no");
                callOldContacts();

            }   else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(mThis,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        readContactsRequest);

                Log.i("checkPerm", "requested");

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }   else {
            Log.i("checkPerm", "granted");
            if (acct != null) {
                callNewContacts();
            }   else {
                callOldContacts();
            }
        }
    }





    public void initialize () {
        persons = new HashMap<>();
        /*nameTextView = findViewById(R.id.fullNameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        numpersTextView = findViewById(R.id.numpersTextView);
        idTextView = findViewById(R.id.idTextView);*/

        me = getIntent();
        String user = me.getStringExtra("user");
        String password = me.getStringExtra("password");

        progressBar = findViewById(R.id.userPageProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate();

        mapper = new ObjectMapper();

        vc = new VolleyCallback() {
            @Override
            public void onSuccessResponse(String response) throws IOException {
                try {
                    personJSON = response;

                    person = mapper.readValue(response, Person.class);

                    //idTextView.setText(String.valueOf(person.id));
                    //nameTextView.setText(person.firstName + " " + person.lastName);
                    //emailTextView.setText(person.email);
                    //numpersTextView.setText(String.valueOf(person.numpers));
                    Toolbar tb = findViewById(R.id.toolbar);
                    tb.setTitle(person.firstName + " " + person.lastName);
                    tb.setSubtitle(person.email);
                    //setActionBarTitle(nameTextView.getText().toString());

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
        };

        el = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "error response" + error.getMessage());
                VolleyLog.d("error", "Error: " + error.getMessage());
            }
        };

        if (personServices == null)
            personServices = new PersonServices();

        personServices.userByEmailAndPass(this, user, password, vc, el);
    }



    public void checkAllLoaded() {
        boolean allLoaded = true;
        for (boolean b : loadedTasksByEvents.values()) {
            allLoaded &= b;
        }
        if (allLoaded) {
            setListView();
            progressBar.setVisibility(View.INVISIBLE);
            expandableListView.setVisibility(View.VISIBLE);
        }

        //if (expandableListAdapter != null) expandableListAdapter.notifyAll();
    }

    public void setTasksByActivity (final long aId) {
        TasksServices tasksServices = new TasksServices();
        tasksServices.tasksByAct(this, aId,
        new VolleyCallback() {
            @Override
            public void onSuccessResponse(String response) {
                ArrayList<Task> tasks = null;
                try {
                    tasks = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Task.class));
                    if (tasks != null) {
                        for (Pair<Long, ArrayList<Task>> pair : tasksByAct) {
                            if (pair.first == aId) {
                                for (Task t : tasks) {
                                    (pair.second).add(t);
                                    if (!persons.containsKey(t.owner)) {
                                        setPersons(t.owner);
                                    }
                                }
                                loadedTasksByEvents.put(aId, true);
                                break;
                            }
                        }
                    }
                    checkAllLoaded();
                }   catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "error response" + error.getMessage());
                VolleyLog.d("error", "Error: " + error.getMessage());
            }
        });
    }

    public void setActivities () {
        EventServices eventServices = new EventServices();
        eventServices.activitiesByParticipant(this, String.valueOf(person.id),
        new VolleyCallback() {
            @Override
            public void onSuccessResponse(String response) {
                try {
                    events = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Event.class));
                    if (events != null) {
                        tasksByAct = new ArrayList<>();
                        loadedTasksByEvents = new HashMap<>();
                        for (Event e : events) {
                            loadedTasksByEvents.put(e.id, false);
                            tasksByAct.add(new Pair<>(e.id, new ArrayList<Task>()));
                            setTasksByActivity(e.id);
                        }
                        if (events.size() == 0) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "No events to show", Toast.LENGTH_SHORT).show();
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
                Log.e("error", "error response" + error.getMessage());
                VolleyLog.d("error", "Error: " + error.getMessage());
            }
        });
    }

    public void setListView () {
        if (events != null) {
            expandableListView = findViewById(R.id.eventsExpandableListView);
            expandableListAdapter = new EventsExpandableListAdapter(events, tasksByAct);
            expandableListView.setAdapter(expandableListAdapter);

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                    return false;
                }
            });

            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return false;
                }
            });

            expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == 1) {
                        if (!hidden) {
                            fabsVisibility(false);
                            hidden = true;
                        }
                    }   else {
                        startTimer(2000);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    /*int lastItem = firstVisibleItem + visibleItemCount;
                    if (lastItem <= totalItemCount-1 && firstVisibleItem > 0) {
                        fabsVisibility(false);
                    }   else {
                        fabsVisibility(true);
                    }*/
                }
            });
        }
    }

    public void fabsVisibility (boolean visibility) {
        hidden = !visibility;
        if (!visibility) {
            if (expanded) {
                fabClose.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationRepeat(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fabExpander.startAnimation(fabHide);
                        fabExpander.setClickable(false);
                        fabClose.setAnimationListener(null);
                    }
                });
                closeFabs();
                expanded = false;

            }   else {
                fabExpander.startAnimation(fabHide);
                fabExpander.setClickable(false);
            }
        }   else {
            fabExpander.startAnimation(fabUnHide);
            fabExpander.setClickable(true);
        }
    }

    public void openFabs () {
        if (acct != null)
            fabLogout.startAnimation(fabOpen);
        fabAdd.startAnimation(fabOpen);
        fabContacts.startAnimation(fabOpen);
        fabExpander.startAnimation(fabRotateACW);
        fabAdd.setClickable(true);
        fabContacts.setClickable(true);
        fabLogout.setClickable(true);
    }

    public void closeFabs () {
        if (acct != null)
            fabLogout.startAnimation(fabClose);
        fabAdd.startAnimation(fabClose);
        fabContacts.startAnimation(fabClose);
        fabExpander.startAnimation(fabRotateCW);
        fabAdd.setClickable(false);
        fabContacts.setClickable(false);
        fabLogout.setClickable(false);
    }

    private void startTimer(long time){
        if (timer != null)
            timer.cancel();

        timer = new CountDownTimer(time, time){
            public void onTick(long millisUntilDone){

            }

            public void onFinish() {
                fabsVisibility(true);
                cancel();
            }
        }.start();
    }


    public void setPersons (final long pId) {
        PersonServices personServices = new PersonServices();
        personServices.userById(this, pId,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {
                        try {
                            Person person = mapper.readValue(response, Person.class);

                            if (person != null && person.id != 0) {
                                persons.put(pId, person);
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
                        Log.e("error", "error response" + error.getMessage());
                        VolleyLog.d("error", "Error: " + error.getMessage());
                    }
                });
    }

    @Override
    public void onGoogleAuthSignIn(String authToken, String userId) {

    }

    @Override
    public void onGoogleAuthSignInFailed(String errorMessage) {

    }

    @Override
    public void onGoogleAuthSignOut() {

    }


    public class EventsExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<Event> groups;
        //private ArrayList<ArrayList<Task>> children;
        private ArrayList<Pair<Long, ArrayList<Task>>> children;
        private LayoutInflater layOutInflater;
        View.OnClickListener detailOnClickListener;
        View.OnClickListener detailTaskOnClickListener;

        public EventsExpandableListAdapter () {
            layOutInflater = LayoutInflater.from(UserPage.this);
        }

        public EventsExpandableListAdapter (ArrayList<Event> groups, ArrayList<Pair<Long, ArrayList<Task>>> children) {
            this.groups = groups;
            this.children = children;
            layOutInflater = LayoutInflater.from(UserPage.this);

            detailOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageButton detail = (ImageButton) view;
                    long id =  Long.parseLong(detail.getTag().toString());
                    Log.i("event", detail.getTag().toString());
                    Event eventClicked = getEventById(id);
                    if (eventClicked != null) {
                        //Intent eventDetailIntent = new Intent(getApplicationContext(), EventDetail.class);
                        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                            expandableListView.collapseGroup(i);
                        }

                        Intent eventDetailIntent = new Intent(getApplicationContext(), EventDetailed.class);
                        try {
                            eventDetailIntent.putExtra("person", mapper.writeValueAsString(person));
                            eventDetailIntent.putExtra("event", mapper.writeValueAsString(eventClicked));
                        }   catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        startActivityForResult(eventDetailIntent, eventDetailCode);
                    }
                }
            };

            detailTaskOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("task", view.getTag().toString());
                }
            };
        }

        private Event getEventById (long id) {
            for (Event e : groups) {
                if (e.id == id) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int i) {
            if (i >= 0 && i < groups.size()) {
                return children.get(i).second.size();
            }
            return 0;
        }

        @Override
        public Object getGroup(int i) {
            if (i >= 0 && i < groups.size()) {
                return groups.get(i);
            }
            return null;
        }

        @Override
        public Object getChild(int i, int i1) {
            if (i >= 0 && i < groups.size()) {
                if (i1 >= 0 && i1 < children.get(i).second.size()) {
                    return children.get(i).second.get(i1);
                }
            }
            return null;
        }

        @Override
        public long getGroupId(int i) {
            if (i >= 0 && i < groups.size()) {
                return i;
            }
            return 0;
        }

        @Override
        public long getChildId(int i, int i1) {
            if (i >= 0 && i < groups.size()) {
                if (i1 >= 0 && i1 < children.get(i).second.size()) {
                    return i1;
                }
            }
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            Event e = (Event) getGroup(i);
            view = layOutInflater.inflate(R.layout.events_layout, null);

            ((TextView) view.findViewById(R.id.eventNameTextView)).setText(e.name);
            ((TextView) view.findViewById(R.id.eventIdTextView)).setText(String.valueOf(e.id));
            (view.findViewById(R.id.eventIdTextView)).setVisibility(View.INVISIBLE);
            ((TextView) view.findViewById(R.id.eventDateTextView)).setText(e.date);
            ((TextView) view.findViewById(R.id.eventDescriptionTextView)).setText(e.description);
            ImageButton detail = (view.findViewById(R.id.eventInfoImageButton));
            detail.setFocusable(false);
            detail.setClickable(true);
            detail.setTag(String.valueOf(e.id));
            detail.setOnClickListener(detailOnClickListener);

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            Task t = (Task) getChild(i, i1);
            view = layOutInflater.inflate(R.layout.tasks_layout, null);

            ((TextView) view.findViewById(R.id.taskNameTextView)).setText(t.name);
            ((TextView) view.findViewById(R.id.taskIdTextView)).setText(String.valueOf(t.id));
            (view.findViewById(R.id.taskIdTextView)).setVisibility(View.INVISIBLE);
            ((TextView) view.findViewById(R.id.taskOwnerTextView)).setText(persons.get(t.owner).firstName + " " + persons.get(t.owner).lastName);
            ((TextView) view.findViewById(R.id.taskAmountTextView)).setText(Utils.amount2string(t.amount));
            view.setTag(t.id);

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            if (i >= 0 && i < groups.size()) {
                if (i1 >= 0 && i1 < children.get(i).second.size()) {
                    return true;
                }
            }
            return false;
        }
    }
}
