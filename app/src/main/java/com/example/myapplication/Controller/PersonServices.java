package com.example.myapplication.Controller;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.myapplication.Model.Constants;
import com.example.myapplication.Model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PersonServices {

    Person person;
    ArrayList<Person> persons;
    public boolean ready;
    public android.content.Context parentContext;

    public void userById (long pId, android.content.Context context, final View view) {
        ready = false;
        person = null;
        String URL = Constants.URL_UserById + "?pId=" + pId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    person = mapper.readValue(response.toString(), Person.class);

                    TextView textView = (TextView) view;
                    textView.setText(person.firstName + " " + person.lastName);

                }   catch (Exception e) {
                    Log.e("Exception: ", e.getMessage());
                    e.printStackTrace();
                }
                ready = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "error response" + error.getMessage());
                VolleyLog.d("error", "Error: " + error.getMessage());
                ready = true;
            }
        });

        AppController.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    public void userById(long pId, android.content.Context context, final View tview, final View mltview) {
        ready = false;
        person = null;
        String URL = Constants.URL_UserById + "?pId=" + pId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    person = mapper.readValue(response.toString(), Person.class);

                    TextView textView = (TextView) tview;
                    textView.setText(person.firstName + " " + person.lastName);

                    TextView multiLineTextView =  (TextView) mltview;
                    multiLineTextView.setText(person.toString());

                }   catch (Exception e) {
                    Log.e("Exception: ", e.getMessage());
                    e.printStackTrace();
                }
                ready = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "error response" + error.getMessage());
                VolleyLog.d("error", "Error: " + error.getMessage());
                ready = true;
            }
        });

        AppController.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void usersList(android.content.Context context, final View mltview) {
        ready = false;
        person = null;
        String URL = Constants.URL_UsersList;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    persons = mapper.readValue(response.toString(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, Person.class));

                    TextView multiLineTextView =  (TextView) mltview;
                    for (Person p : persons) {
                        multiLineTextView.setText(multiLineTextView.getText().toString() + "\n" + p.toString());
                    }

                }   catch (Exception e) {
                    Log.e("Exception: ", e.getMessage());
                    e.printStackTrace();
                }
                ready = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "error response" + error.getMessage());
                VolleyLog.d("error", "Error: " + error.getMessage());
                ready = true;
            }
        });

        AppController.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
}
