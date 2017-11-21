package com.example.myapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.example.myapplication.Controller.EventServices;
import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Controller.TasksServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.Person;
import com.example.myapplication.Model.Task;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserPage extends AppCompatActivity {
    TextView nameTextView;
    TextView emailTextView;
    TextView numpersTextView;
    TextView idTextView;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    Person person;
    ArrayList<Event> events;
    ArrayList<Pair<Long, ArrayList<Task>>> tasksByAct;
    HashMap<Long, Boolean> loadedTasksByEvents;

    Intent me;

    String personJSON;
    String activitiesJSON;

    public void showToast (String text) {
        Toast.makeText(this, "text", Toast.LENGTH_SHORT).show();
    }

    public void checkAllLoaded() {
        boolean allLoaded = true;
        for (boolean b : loadedTasksByEvents.values()) {
            allLoaded &= b;
        }
        if (allLoaded)
            setListView();
    }

    public void setTasksByActivity (final long aId) {
        TasksServices tasksServices = new TasksServices();
        tasksServices.tasksByAct(this, String.valueOf(aId),
        new VolleyCallback() {
            @Override
            public void onSuccessResponse(String response) {
                ObjectMapper mapper = new ObjectMapper();
                ArrayList<Task> tasks = null;
                try {
                    tasks = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(ArrayList.class, Task.class));
                    if (tasks != null) {
                        for (Pair<Long, ArrayList<Task>> pair : tasksByAct) {
                            if (pair.first == aId) {
                                for (Task t : tasks) {
                                    (pair.second).add(t);
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
                activitiesJSON = response;
                ObjectMapper mapper = new ObjectMapper();
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
                    Event e = events.get(groupPosition);

                    /*if (parent.isGroupExpanded(groupPosition)) {
                        parent.collapseGroup(groupPosition);
                    }   else {
                        parent.expandGroup(groupPosition);
                    }*/

                    return false;
                }
            });
        }
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

        me = getIntent();
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

    @Override
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

    }

    public class EventsExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<Event> groups;
        //private ArrayList<ArrayList<Task>> children;
        private ArrayList<Pair<Long, ArrayList<Task>>> children;
        private LayoutInflater layOutInflater;

        public EventsExpandableListAdapter () {
            layOutInflater = LayoutInflater.from(UserPage.this);
        }

        public EventsExpandableListAdapter (ArrayList<Event> groups, ArrayList<Pair<Long, ArrayList<Task>>> children) {
            this.groups = groups;
            this.children = children;
            layOutInflater = LayoutInflater.from(UserPage.this);
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
            (view.findViewById(R.id.eventInfoImageButton)).setFocusable(false);

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            Task t = (Task) getChild(i, i1);
            view = layOutInflater.inflate(R.layout.tasks_layout, null);

            ((TextView) view.findViewById(R.id.taskNameTextView)).setText(t.name);
            ((TextView) view.findViewById(R.id.taskIdTextView)).setText(String.valueOf(t.id));
            (view.findViewById(R.id.taskIdTextView)).setVisibility(View.INVISIBLE);
            ((TextView) view.findViewById(R.id.taskOwnerTextView)).setText(String.valueOf(t.owner));
            ((TextView) view.findViewById(R.id.taskAmmountTextView)).setText(String.valueOf(t.ammount));
            (view.findViewById(R.id.taskInfoImageButton)).setFocusable(false);

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
