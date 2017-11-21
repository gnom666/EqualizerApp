package com.example.myapplication.Controller;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GenericServices {

    public static String TOKEN;

    public static void callService (android.content.Context context, int method, String URL, final String paramName, final JSONObject paramJSON, final VolleyCallback callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    callback.onSuccessResponse(response.toString());

                }   catch (Exception e) {
                    Log.e("Exception: ", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "error response" + error.getMessage());
                VolleyLog.d("error", "Error: " + error.getMessage());
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                Map <String, String> params = new HashMap<>();
                params.put("Authorization: Basic", TOKEN);
                if (paramName != null && paramJSON != null) {
                    params.put(paramName, paramJSON.toString());
                }
                return params;
            }
        };

        AppController.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void callListService (android.content.Context context, int method, String URL, final String paramName, final JSONObject paramJSON, final VolleyCallback callback, final Response.ErrorListener errorListener) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    callback.onSuccessResponse(response.toString());

                }   catch (Exception e) {
                    Log.e("Exception: ", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, errorListener)
        {
            @Override
            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                Map <String, String> params = new HashMap<>();
                params.put("Authorization: Basic", TOKEN);
                if (paramName != null && paramJSON != null) {
                    params.put(paramName, paramJSON.toString());
                }
                return params;
            }
        };

        AppController.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
}
