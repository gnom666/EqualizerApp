package com.example.myapplication.Controller;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.myapplication.Model.Constants;

import org.json.JSONObject;

public class TasksServices {

    public void tasksByAct (android.content.Context context, long aId, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_TasksByActivity + "?aId=" + aId;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callListService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void deleteTask (android.content.Context context, long taskId, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_DeleteTask + "?tId=" + taskId;

        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void addTask (android.content.Context context, String paramName, JSONObject paramJSON, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_AddTask;

        GenericServices.callPostService(context, Request.Method.POST, URL, paramName, paramJSON, callback, errorListener);
    }

    public void modifyTask (android.content.Context context, String paramName, JSONObject paramJSON, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_ModifyTask;

        GenericServices.callPostService(context, Request.Method.POST, URL, paramName, paramJSON, callback, errorListener);
    }
}
