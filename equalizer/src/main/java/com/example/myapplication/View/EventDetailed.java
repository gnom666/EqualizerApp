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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.myapplication.Model.Constants;
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
    LinearLayout paymentsLayout;

    HashMap<Long, View> participantsViews;
    AbsListView.OnScrollListener onScrollListener;
    AbsListView.OnScrollListener onScrollParticipantsListener;
    AbsListView.OnScrollListener onScrollPaymentsListener;

    Intent me;
    ObjectMapper mapper;
    Object mThis;

    FloatingActionButton fabAddTask;
    FloatingActionButton fabUpdate;
    FloatingActionButton fabEdit;
    FloatingActionButton fabDelete;
    FloatingActionButton fabExpander;
    boolean expanded = false;
    boolean hidden = false;
    boolean addHidden = false;
    boolean actionsHidden = true;
    Animation fabOpen, fabClose, fabRotateCW, fabRotateACW, fabHide, fabUnHide, fabHideLeft, fabUnHideLeft, fabHideLeftDeep, fabUnHideLeftDeep;
    CountDownTimer timer;
    FloatingActionButton checkAB, doublecheckAB, exclAB, resetAB;

    TabTasks tabTasks;
    TabParticipants tabParticipants;
    TabPayments tabPayments;
    LayoutInflater layOutInflater;
    AlertDialog.Builder confirmationDialogBuilder;
    AlertDialog confirmationDialog;

    View.OnClickListener listenerAdd;
    View.OnClickListener listenerRemove;

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
    private AlertDialog confirmationAlertDialog;
    private AlertDialog.Builder confirmationAlertDialogBuilder;

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


        fabAddTask = findViewById(R.id.detailedEventAddTask);
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

        fabDelete = (FloatingActionButton) findViewById(R.id.detailedEventDelete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOwner) {
                    Toast.makeText(getApplicationContext(), R.string.owner_can_delete, Toast.LENGTH_SHORT).show();
                }   else {
                    deleteEventConfirmation();
                }
            }
        });


        fabEdit = findViewById(R.id.detailedEventEdit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOwner) {
                    Toast.makeText(getApplicationContext(), R.string.owner_can_edit, Toast.LENGTH_SHORT).show();
                }   else {
                    editEvent();
                }
            }
        });

        fabUpdate = findViewById(R.id.updateActionButton);
        fabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init ();
                setTasks(event.id, true);
                setPayments(event.id, true, true);
            }
        });

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRotateCW = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_cw);
        fabRotateACW = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_acw);
        fabHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide);
        fabUnHide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.unhide);
        fabHideLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_left);
        fabUnHideLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.unhide_left);
        fabHideLeftDeep = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_left_deep);
        fabUnHideLeftDeep = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.unhide_left_deep);

        fabExpander = findViewById(R.id.detailedEventExpand);
        fabExpander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expanded) {
                    closeFabs();
                }   else {
                    openFabs();
                    timer = new CountDownTimer(4000, 4000){
                        public void onTick(long millisUntilDone){}

                        public void onFinish() {
                            if (expanded) {
                                closeFabs();
                                expanded = false;
                            }
                            cancel();
                        }
                    }.start();
                }
                expanded = !expanded;
            }
        });

        expanded = false;
        fabEdit.setClickable(false);
        fabDelete.setClickable(false);

        checkAB = findViewById(R.id.checkActionButton);
        doublecheckAB = findViewById(R.id.doublecheckActionButton);
        exclAB = findViewById(R.id.exclamationActionButton);
        resetAB = findViewById(R.id.resetActionButton);

        checkAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideLeft();
                actionsVisible(false);
            }
        });

        doublecheckAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideLeft();
                actionsVisible(false);
            }
        });

        exclAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideLeft();
                actionsVisible(false);
            }
        });

        resetAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideLeft();
                actionsVisible(false);
            }
        });

        paymentsLayout = findViewById(R.id.paymentsListLayout);
        if (paymentsLayout != null) {
            paymentsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (actionsHidden) {
                        hideLeft();
                    }
                }
            });
        }

        onScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 1) {
                    if (!hidden) {
                        fabsVisibility(false);
                        hidden = true;
                    }
                    if (timer != null) timer.cancel();
                }
                if (scrollState == 0 && hidden){
                    startTimer(2000);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                /*Log.i("POSITION", String.valueOf(POSITION));
                int lastItem = firstVisibleItem + visibleItemCount;
                if (POSITION != TASKS_POSITION || (lastItem == totalItemCount && firstVisibleItem > 0)) {
                    fabAddTask.setVisibility(View.INVISIBLE);
                }   else {
                    fabAddTask.setVisibility(View.VISIBLE);
                }*/
            }
        };

        onScrollParticipantsListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 1) {
                    if (!hidden) {
                        fabsVisibility(false);
                        hidden = true;
                    }
                    if (timer != null) timer.cancel();
                }
                if (scrollState == 0 && hidden) {
                    startTimer(2000);
                }
                /*if (scrollState == 1) {
                    setEditLayoutVisibility(false);
                }   else {
                    startButtonsTimer(2000);
                }*/
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                /*Log.i("POSITION", String.valueOf(POSITION));
                int lastItem = firstVisibleItem + visibleItemCount;
                if (POSITION != TASKS_POSITION || (lastItem == totalItemCount && firstVisibleItem > 0)) {
                    fabAddTask.setVisibility(View.INVISIBLE);
                }   else {
                    fabAddTask.setVisibility(View.VISIBLE);
                }*/
            }
        };

        onScrollPaymentsListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 1) {
                    if (!hidden) {
                        fabsVisibility(false);
                        hidden = true;
                    }
                    if (timer != null) timer.cancel();
                }
                if (scrollState == 0 && hidden){
                    startTimer(2000);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        };

        init ();

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
                        if (POSITION == TASKS_POSITION) {
                            fabAddTask.startAnimation(fabHideLeft);
                            fabAddTask.setClickable(false);
                            addHidden = true;
                        }
                        fabClose.setAnimationListener(null);
                    }
                });
                closeFabs();
                expanded = false;

            }   else {
                fabExpander.startAnimation(fabHide);
                fabExpander.setClickable(false);
                if (POSITION == TASKS_POSITION) {
                    fabAddTask.startAnimation(fabHideLeft);
                    fabAddTask.setClickable(false);
                    addHidden = true;
                }
            }
        }   else {
            fabExpander.startAnimation(fabUnHide);
            fabExpander.setClickable(true);
            if (POSITION == TASKS_POSITION && addHidden) {
                fabAddTask.startAnimation(fabUnHideLeft);
                fabAddTask.setClickable(true);
                addHidden = false;
            }
        }
    }

    public void openFabs () {
        fabUpdate.startAnimation(fabOpen);
        fabEdit.startAnimation(fabOpen);
        fabDelete.startAnimation(fabOpen);
        fabExpander.startAnimation(fabRotateACW);
        fabUpdate.setClickable(true);
        fabEdit.setClickable(true);
        fabDelete.setClickable(true);
    }

    public void closeFabs () {
        fabUpdate.startAnimation(fabClose);
        fabEdit.startAnimation(fabClose);
        fabDelete.startAnimation(fabClose);
        fabExpander.startAnimation(fabRotateCW);
        fabUpdate.setClickable(false);
        fabEdit.setClickable(false);
        fabDelete.setClickable(false);
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

    public void hideLeft () {
        checkAB.startAnimation(fabHideLeftDeep);
        doublecheckAB.startAnimation(fabHideLeftDeep);
        exclAB.startAnimation(fabHideLeftDeep);
        resetAB.startAnimation(fabHideLeftDeep);
        actionsHidden = true;
    }

    public void unhideLeft () {
        checkAB.startAnimation(fabUnHideLeftDeep);
        doublecheckAB.startAnimation(fabUnHideLeftDeep);
        exclAB.startAnimation(fabUnHideLeftDeep);
        resetAB.startAnimation(fabUnHideLeftDeep);
        actionsHidden = false;
    }

    public void actionsVisible (boolean visible) {
        if (visible) {
            checkAB.setVisibility(View.VISIBLE);
            doublecheckAB.setVisibility(View.VISIBLE);
            exclAB.setVisibility(View.VISIBLE);
            resetAB.setVisibility(View.VISIBLE);
        }   else {
            checkAB.setVisibility(View.GONE);
            doublecheckAB.setVisibility(View.GONE);
            exclAB.setVisibility(View.GONE);
            resetAB.setVisibility(View.GONE);
        }
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
                if (addHidden) {
                    fabAddTask.startAnimation(fabUnHideLeft);
                    fabAddTask.setClickable(true);
                    addHidden = false;
                }
                break;
            case PARTICIPANTS_POSITION:
                if (!addHidden) {
                    fabAddTask.startAnimation(fabHideLeft);
                    fabAddTask.setClickable(false);
                    addHidden = true;
                }
                if (participantsListAdapter != null)
                    participantsListAdapter.notifyDataSetChanged();
                break;
            case PAYMENTS_POSITION:
                if (!addHidden) {
                    fabAddTask.startAnimation(fabHideLeft);
                    fabAddTask.setClickable(false);
                    addHidden = true;
                }
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
                                //setPayments(event.id, force);
                                setPayments(event.id, true, force);

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
                        Log.e("VolleyError", error.getMessage());
                    }
                });
    }

    public void setPayments (final long aId, final boolean redo, final boolean force) {

        PaymentServices paymentServices = new PaymentServices();

        paymentServices.calculatePayments(this, aId, redo,
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
                        Log.e("VolleyError", error.getMessage());
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
                        Log.e("VolleyError", error.getMessage());
                    }
                });
    }

    private void deleteTaskConfirmation(final long tId) {

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View confirmationView = li.inflate(R.layout.confirmation_layout, null);

        TextView question = confirmationView.findViewById(R.id.confirmationTextView);
        question.setText(R.string.sure);

        Button dialogNoButton = confirmationView.findViewById(R.id.genericNoButton);
        dialogNoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                confirmationAlertDialog.cancel();
                //confirmationAlertDialog.dismiss();
            }
        });

        Button dialogYesButton = confirmationView.findViewById(R.id.genericYeslButton);
        dialogYesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                deleteTask(tId);
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
                                    //setPayments(event.id, true);
                                    setPayments(event.id, true, true);

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

    private void deleteEventConfirmation() {

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View confirmationView = li.inflate(R.layout.confirmation_layout, null);

        TextView question = confirmationView.findViewById(R.id.confirmationTextView);
        question.setText(R.string.sure);

        Button dialogNoButton = confirmationView.findViewById(R.id.genericNoButton);
        dialogNoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                confirmationAlertDialog.cancel();
                //confirmationAlertDialog.dismiss();
            }
        });

        Button dialogYesButton = confirmationView.findViewById(R.id.genericYeslButton);
        dialogYesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                deleteEvent();
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_detailed, menu);
        return true;
    }*/

    /*@Override
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
                    deleteEventConfirmation();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    /*public void setFabVisibility (boolean visible) {
        if (visible) {
            fabAddTask.setVisibility(View.VISIBLE);
        }   else {
            fabAddTask.setVisibility(View.INVISIBLE);
        }
    }*/

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
            //setPayments(event.id, true);
            setPayments(event.id, true, true);

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
            //setPayments(event.id, true);
            setPayments(event.id, true, true);

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
            it = participantsViews.values().iterator();
            while (it.hasNext()) {
                ImageButton addRemove = ((View)it.next()).findViewById(R.id.contactGmailImageButton);
                addRemove.setImageResource(R.drawable.add_contact);
                addRemove.setOnClickListener(listenerAdd);
            }
            for (int i = 0; i < originals.size(); i++) {
                if (participantsViews.containsKey(originals.get(i).id)) {
                    ImageButton addRemove = ((View)it.next()).findViewById(R.id.contactGmailImageButton);
                    addRemove.setImageResource(R.drawable.delete_2);
                    addRemove.setOnClickListener(listenerRemove);
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


        public ParticipantsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Person> participants) {
            super(context, resource, participants);
            this.participants = participants;

            this.context = context;
            if (layOutInflater == null)
                layOutInflater = LayoutInflater.from(EventDetailed.this);

            listenerAdd = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageButton button = (ImageButton) view;
                    selected.put((long) button.getTag(), true);
                    onOkClick();
                }
            };

            listenerRemove = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageButton button = (ImageButton) view;
                    selected.put((long) button.getTag(), false);
                    onOkClick();
                }
            };

            participantsViews = new LinkedHashMap<>();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Person p = getItem(position);

            view = layOutInflater.inflate(R.layout.contacts_gmail_layout, null);
            TextView name = view.findViewById(R.id.contactGmailNameTextView);
            TextView email = view.findViewById(R.id.contactGmailEmailTextView);
            ImageButton addRemove = view.findViewById(R.id.contactGmailImageButton);
            name.setText(p.firstName + " " + p.lastName);
            email.setText(p.email);
            if (!isOwner) {
                addRemove.setClickable(false);
            }   else {
                if (!selected.get(contacts.get(position).id)) {
                    addRemove.setImageResource(R.drawable.add_contact);
                    addRemove.setTag(contacts.get(position).id);
                    addRemove.setOnClickListener(listenerAdd);
                }   else {
                    addRemove.setTag(contacts.get(position).id);
                    addRemove.setOnClickListener(listenerRemove);
                }
            }
            participantsViews.put(p.id, view);

            /*view = layOutInflater.inflate(R.layout.participants_layout, null);
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
            participantsViews.put(p.id, checkBox);*/

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
                    deleteTaskConfirmation((long) view.getTag());
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

        paymentsListView.setOnScrollListener(onScrollPaymentsListener);

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

        paymentsListView.setOnScrollListener(onScrollPaymentsListener);

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
                    if (p.status == Constants.PaymentStatus.REQUESTED)
                        direction.setImageResource(R.drawable.check);
                    else if (p.status == Constants.PaymentStatus.PAID)
                        direction.setImageResource(R.drawable.doublecheck);
                    else
                        direction.setImageResource(R.drawable.left_shadow_green);
                }   else {
                    if (p.status == Constants.PaymentStatus.REQUESTED)
                        direction.setImageResource(R.drawable.check_red);
                    else if (p.status == Constants.PaymentStatus.PAID)
                        direction.setImageResource(R.drawable.doublecheck_red);
                    else
                        direction.setImageResource(R.drawable.right_shadow_red);
                }
                if (p.status == Constants.PaymentStatus.CONFLICT)
                    direction.setImageResource(R.drawable.exclamation_orange);
            }   else {
                String text = "";
                String sep = " - ";

                if (p.status == Constants.PaymentStatus.REQUESTED)
                    direction.setImageResource(R.drawable.check_blue);
                else if (p.status == Constants.PaymentStatus.PAID)
                    direction.setImageResource(R.drawable.doublecheck_blue);
                else
                    direction.setImageResource(R.drawable.right_shadow_blue);

                Person from = findPerson(participants, p.from);
                if (from.id == person.id) {
                    if (p.status == Constants.PaymentStatus.REQUESTED)
                        direction.setImageResource(R.drawable.check_red);
                    else if (p.status == Constants.PaymentStatus.PAID)
                        direction.setImageResource(R.drawable.doublecheck_red);
                    else
                        direction.setImageResource(R.drawable.right_shadow_red);
                    sep = "";
                }   else {
                    text += from.firstName + " " + from.lastName;
                }

                Person to = findPerson(participants, p.to);
                if (to.id == person.id) {
                    if (p.status == Constants.PaymentStatus.REQUESTED)
                        direction.setImageResource(R.drawable.check);
                    else if (p.status == Constants.PaymentStatus.PAID)
                        direction.setImageResource(R.drawable.doublecheck);
                    else
                        direction.setImageResource(R.drawable.left_shadow_green );
                    sep = "";
                }   else {
                    text += sep + to.firstName + " " + to.lastName;
                }

                if (p.status == Constants.PaymentStatus.CONFLICT)
                    direction.setImageResource(R.drawable.exclamation_orange);

                name.setText(text);

                status.setText(p.status.toString());

                amount.setText(Utils.amount2string(p.amount));

            }

            direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onActionClick();
                }
            });

            return view;
        }

    }

    public void onActionClick () {
        actionsVisible(true);
        unhideLeft();
    }

    public void onOkClick () {
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
