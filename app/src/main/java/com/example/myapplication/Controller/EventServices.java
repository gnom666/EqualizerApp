package com.example.myapplication.Controller;

import com.android.volley.Request;
import com.example.myapplication.Model.Constants;

public class EventServices {

    public void activitiesByParticipant (android.content.Context context, String pId, final VolleyCallback callback) {
        String URL = Constants.URL_ActivitiesByParticipant + "?pId=" + pId;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callListService(context, Request.Method.GET, URL, null, null, callback);
    }
}
