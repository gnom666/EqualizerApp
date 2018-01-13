package com.example.myapplication.Controller;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.myapplication.Model.Constants;

import org.json.JSONObject;

public class PersonServices {

    public void userById (android.content.Context context, long pId, final VolleyCallback callback, final Response.ErrorListener errorListener) {

        String URL = Constants.URL_UserById + "?pId=" + pId;

        GenericServices.callService(context, Request.Method.GET, URL, null,null, callback, errorListener);

    }

    public void userByEmail (android.content.Context context, String user, VolleyCallback callback, final Response.ErrorListener errorListener) {

        String URL = Constants.URL_CheckUser + "?u=" + user;

        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void setFriendByEmail (android.content.Context context, long pId, String email, VolleyCallback callback, final Response.ErrorListener errorListener) {

        String URL = Constants.URL_SetFriendByEmail + "?pId=" + pId + "&email=" + email;

        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void unsetFriends (android.content.Context context, long person1Id, long person2Id, VolleyCallback callback, final Response.ErrorListener errorListener) {

        String URL = Constants.URL_UnsetFriends + "?person1Id=" + person1Id + "&person2Id=" + person2Id;

        GenericServices.callListService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void userByEmailAndPass (android.content.Context context, String user, String password, final VolleyCallback callback, final Response.ErrorListener errorListener) {

        String URL = Constants.URL_CheckUserAndPass + "?uName=" + user + "&uPass=" + password;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void usersList(android.content.Context context, final VolleyCallback callback, final Response.ErrorListener errorListener) {

        String URL = Constants.URL_UsersList;

        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void contacts(android.content.Context context, long pId, final VolleyCallback callback, final Response.ErrorListener errorListener) {

        String URL = Constants.URL_Contacts + "?pId=" + pId;

        GenericServices.callListService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void participants(android.content.Context context, long aId, final VolleyCallback callback, final Response.ErrorListener errorListener) {

        String URL = Constants.URL_Participants + "?aId=" + aId;

        GenericServices.callListService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void addPerson (android.content.Context context, String paramName, JSONObject paramJSON, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_AddPerson;

        GenericServices.callPostService(context, Request.Method.POST, URL, paramName, paramJSON, callback, errorListener);
    }

    public void addGoogleUser (android.content.Context context, String paramName, JSONObject paramJSON, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_AddGoogleUser;

        GenericServices.callPostService(context, Request.Method.POST, URL, paramName, paramJSON, callback, errorListener);
    }

    public void sendMail (android.content.Context context, String user, final VolleyCallback callback, final Response.ErrorListener errorListener) {

        String URL = Constants.URL_SendMail + "?user=" + user;

        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void modifyPerson (android.content.Context context, String paramName, JSONObject paramJSON, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_ModifyPerson;

        GenericServices.callPostService(context, Request.Method.POST, URL, paramName, paramJSON, callback, errorListener);
    }
}
