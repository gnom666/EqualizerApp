package com.example.myapplication.Controller;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.myapplication.Model.Constants;

import org.json.JSONObject;

public class EventServices {

    public void activitiesByParticipant (android.content.Context context, String pId, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_ActivitiesByParticipant + "?pId=" + pId;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callListService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void addActivity (android.content.Context context, String paramName, JSONObject paramJSON, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_AddActivity;

        GenericServices.callPostService(context, Request.Method.POST, URL, paramName, paramJSON, callback, errorListener);
    }

    public void modifyActivity (android.content.Context context, String paramName, JSONObject paramJSON, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_ModifyActivity;

        GenericServices.callPostService(context, Request.Method.POST, URL, paramName, paramJSON, callback, errorListener);
    }
}
