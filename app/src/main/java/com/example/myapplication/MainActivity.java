package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public Person person;
    String URL = "http://192.168.1.109:9003/people/userbyid/?pId=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        person = new Person();
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
                    TextView textView = findViewById(R.id.textView);
                    textView.setText(person.firstName + " " + person.lastName);
                }   catch (Exception e) {
                    Log.e("Exception: ", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("info", "error response" + error.getMessage());
                VolleyLog.d("tag", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error while loading ... ", Toast.LENGTH_SHORT).show();
            }
        });
        Log.i("info", "enqueue...");
        AppController.getInstance(this).addToRequestQueue(jsonObjectRequest);
        Log.i("info", "...queued");
    }
}
