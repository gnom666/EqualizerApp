package com.example.myapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapplication.Controller.TasksServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.Person;
import com.example.myapplication.Model.Task;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.myapplication.Model.Utils.amount2string;

public class TaskDetail extends AppCompatActivity {

    Person person;
    String personJson;
    Event event;
    String eventJson;
    Task task;
    String taskJson;

    EditText name;
    EditText description;
    EditText amount;

    Intent me;
    ObjectMapper mapper;
    Object mThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        me = getIntent();

        personJson = me.getStringExtra("person");
        if (personJson == null) finish();
        mapper = new ObjectMapper();
        try {
            person = mapper.readValue(personJson, Person.class);
        }   catch (IOException e) {
            e.printStackTrace();
        }
        if (person == null) finish();

        eventJson = me.getStringExtra("event");
        if (eventJson == null) finish();
        mapper = new ObjectMapper();
        try {
            event = mapper.readValue(eventJson, Event.class);
        }   catch (IOException e) {
            e.printStackTrace();
        }
        if (event == null) finish();

        taskJson = me.getStringExtra("task");
        if (taskJson == null) finish();
        mapper = new ObjectMapper();
        try {
            task = mapper.readValue(taskJson, Task.class);
        }   catch (IOException e) {
            e.printStackTrace();
        }
        if (task == null) finish();

        mThis = this;

        name = findViewById(R.id.addTaskNameEditText);
        description = findViewById(R.id.addTaskDescriptionEditText);
        amount = findViewById(R.id.addTaskAmountEditText);

        name.setText(task.name);
        description.setText(task.description);
        amount.setText(amount2string(task.ammount));
    }

    public void onCancelClick (View view) {
        setResult(RESULT_CANCELED, me);
        finish();
    }

    public void onOkClick (View view) {
        boolean modified = false;
        modified |= !(task.name.equals(name.getText().toString()));
        modified |= !(task.description.equals(description.getText().toString()));
        modified |= (task.ammount != Double.parseDouble(amount.getText().toString()));

        if (modified) {
            task.name = name.getText().toString();
            task.description = description.getText().toString();
            task.ammount = Double.valueOf(amount.getText().toString());

            try {
                modifyTask();
            }   catch (JsonProcessingException e) {
                e.printStackTrace();
            }   catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void modifyTask () throws JsonProcessingException, JSONException {
        TasksServices tasksServices = new TasksServices();

        tasksServices.modifyTask(this, "TaskOut",
                new JSONObject(mapper.writeValueAsString(task)),
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {

                        try {
                            Task modifiedTask = mapper.readValue(response, Task.class);

                            if (modifiedTask != null) {
                                if (modifiedTask.error == null) {
                                    me.putExtra("task", mapper.writeValueAsString(modifiedTask));
                                    finishWithOk ();
                                }   else {
                                    Toast.makeText(getApplicationContext(), modifiedTask.error.description, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
    }

    private void finishWithOk() {
        setResult(RESULT_OK, me);
        finish();
    }
}
