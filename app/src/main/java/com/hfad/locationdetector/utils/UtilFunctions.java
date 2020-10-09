package com.hfad.locationdetector.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class UtilFunctions {
    /** Long, ugly function to parse volley error into meaningful messages. **/
    public static void parseVolleyError(Context context, VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            Log.e("[VOLLEY][REQUEST]", responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (error.getCause() instanceof MalformedURLException){
            Toast.makeText(context, "Bad Request.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                || error.getCause() instanceof JSONException
                || error.getCause() instanceof XmlPullParserException){
            Toast.makeText(context, "Parse Error (because of invalid json or xml).",
                    Toast.LENGTH_SHORT).show();
        }
        else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
            Toast.makeText(context, "Server error.", Toast.LENGTH_SHORT).show();
        }else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                || error.getCause() instanceof ConnectTimeoutException
                || error.getCause() instanceof SocketException
                || (error.getCause().getMessage() != null
                && error.getCause().getMessage().contains("Connection timed out"))) {
            Toast.makeText(context, "Connection timeout error",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "An unknown error occurred.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static Response.ErrorListener parseVolleyErrorResponse(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseVolleyError(context, error);
            }
        };
    }
}
