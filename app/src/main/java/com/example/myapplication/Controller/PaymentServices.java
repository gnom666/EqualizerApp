package com.example.myapplication.Controller;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.myapplication.Model.Constants;

public class PaymentServices {

    public void testPayments (android.content.Context context, long aId, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_TestPayments + "?aId=" + aId;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callListService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void generatePayments (android.content.Context context, long aId, long pId, boolean reDo, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_GeneratePayments + "?aId=" + aId + "&pId=" + pId + "&redo=" + reDo;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callListService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void calculatePayments (android.content.Context context, long aId, boolean reDo, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_CalculatePayments + "?aId=" + aId + "&redo=" + reDo;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callListService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void MakePayment (android.content.Context context, long fId, long pId, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_MakePayment + "?fId=" + fId + "&pId=" + pId;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void AcceptPayment (android.content.Context context, long tId, long pId, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_AcceptPayment + "?tId=" + tId + "&pId=" + pId;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void SuePayment (android.content.Context context, long tId, long pId, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_SuePayment + "?tId=" + tId + "&pId=" + pId;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void ResetPayment (android.content.Context context, long pId, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_ResetPayment + "?pId=" + pId;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

    public void ForceResetPayment (android.content.Context context, long pId, final VolleyCallback callback, final Response.ErrorListener errorListener) {
        String URL = Constants.URL_ForceResetPayment + "?pId=" + pId;

        //GenericServices.TOKEN = "273425c6-fdee-4f22-8f50-5abe86af2313";
        GenericServices.callService(context, Request.Method.GET, URL, null, null, callback, errorListener);
    }

}
