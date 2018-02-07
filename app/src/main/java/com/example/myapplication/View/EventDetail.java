package com.example.myapplication.View;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapplication.Controller.EventServices;
import com.example.myapplication.Controller.VolleyCallback;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

public class EventDetail extends AppCompatActivity {

    Person person;
    ArrayList<Person> contacts;
    LinkedHashMap<Long, Boolean> selected;
    static Event event;
    boolean notOwner;

    EditText name;
    EditText description;
    static EditText date;
    static EditText time;

    Intent me;
    ObjectMapper mapper;
    Object mThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.eventDetailToolbar);
        toolbar.setTitle("Edit Event");
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
        time = findViewById(R.id.detailedEventTimeEditText);

        name.setText(event.name);
        description.setText(event.description);
        date.setText(event.date.split("T")[0]);
        time.setText(event.date.split("T")[1]);

        if (notOwner) {
            name.setFocusable(false);
            description.setFocusable(false);
            date.setFocusable(false);
        }
    }

    public void updateEvent () throws JsonProcessingException, JSONException {
        EventServices eventServices = new EventServices();

        eventServices.modifyActivity(this, "PersonOut",
                new JSONObject(mapper.writeValueAsString(event)),
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String response) {


                        try {
                            Event updatedEvent = mapper.readValue(response, Event.class);

                            if (updatedEvent != null) {
                                if (updatedEvent.error == null) {
                                    event = updatedEvent;
                                    me.putExtra("event", mapper.writeValueAsString(event));
                                }   else {
                                    Toast.makeText(getApplicationContext(), updatedEvent.error.description, Toast.LENGTH_SHORT).show();
                                }
                            }   else {
                                Toast.makeText(getApplicationContext(), "null Event returned", Toast.LENGTH_SHORT).show();
                            }

                        }   catch (IOException e) {
                            e.printStackTrace();
                        }

                        finishWithOk();

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
            event.name = name.getText().toString();
            event.description = description.getText().toString();
            event.date = date.getText().toString() + "T" + time.getText().toString();
            try {
                Log.i("Event", mapper.writeValueAsString(event));
                updateEvent();
            }   catch (JsonProcessingException e) {
                e.printStackTrace();
                return;
            }   catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void finishWithOk () {
        setResult(RESULT_OK, me);
        finish();
    }

    public void onCancelClick (View view) {
        setResult(RESULT_CANCELED, me);
        finish();
    }


    public static String formatFill (int value, int total) {
        String result = String.valueOf(value);
        for (int i = result.length(); i < total; i++) {
            result = "0" + result;
        }
        return result;
    }

    public static class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
            Log.i("time", "Selected Time: " + String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
            time.setText(formatFill(hourOfDay, 2) + ":" + formatFill(minute, 2) + ":00");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            String itime = event.date.split("T")[1];
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            if (!itime.isEmpty() && itime.length() == 8) {
                hour = Integer.valueOf(itime.split(":")[0]);
                minute = Integer.valueOf(itime.split(":")[1]);
            }
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            String idate = event.date.split("T")[0];
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            if (!idate.isEmpty() && idate.length() == 10) {
                year = Integer.valueOf(idate.split("-")[0]);
                month = Integer.valueOf(idate.split("-")[1]) - 1;
                day = Integer.valueOf(idate.split("-")[2]);
            }
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.i("time", "Selected Time: " + String.valueOf(year) + ":" + formatFill(month, 2) + ":" + formatFill(day, 2));
            date.setText(String.valueOf(year) + "-" + formatFill(month + 1, 2) + "-" + formatFill(day, 2));
        }
    }

    public void onTimeClick(View v) {
        TimePicker mTimePicker = new TimePicker();
        mTimePicker.show(getFragmentManager(), "Select time");
    }

    public void onDateClick(View v) {
        DatePickerFragment mDatePicker = new DatePickerFragment();
        mDatePicker.show(getFragmentManager(), "Select date");
    }

}
