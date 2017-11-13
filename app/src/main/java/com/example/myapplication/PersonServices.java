package com.example.myapplication;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

public class PersonServices {

    Person person;
    public boolean ready;
    public android.content.Context parentContext;

    public void userById (long pId, android.content.Context context, final View view) {
        ready = false;
        person = null;
        String URL = Constants.URL_UserById + "?pId=" + pId;

        Log.i("info", "person created");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("response", response.toString());
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    person = mapper.readValue(response.toString(), Person.class);
                    Log.i("person", person.toString());

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
                Log.e("info", "error response" + error.getMessage());
                VolleyLog.d("tag", "Error: " + error.getMessage());
                ready = true;
            }
        });

        AppController.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    public void userById(long pId, android.content.Context context, final View tview, final View mltview) {
        ready = false;
        person = null;
        String URL = Constants.URL_UserById + "?pId=" + pId;

        Log.i("info", "person created");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("response", response.toString());
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    person = mapper.readValue(response.toString(), Person.class);
                    Log.i("person", person.toString());

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
                Log.e("info", "error response" + error.getMessage());
                VolleyLog.d("tag", "Error: " + error.getMessage());
                ready = true;
            }
        });

        AppController.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
