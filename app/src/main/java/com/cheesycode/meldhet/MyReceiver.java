package com.cheesycode.meldhet;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cheesycode.meldhet.helper.ConfigHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                CharSequence message = remoteInput.getCharSequence(MessagingService.NOTIFICATION_REPLY);
                assert message != null;
                sendMessage(context, intent.getStringExtra(context.getString(R.string.missue)), message.toString(),
                        intent.getStringExtra(context.getString(R.string.msender)));
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MessagingService.CHANNNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_menu_send)
                        .setContentTitle(context.getString(R.string.messageSend));
                NotificationManager notificationManager = (NotificationManager) context.
                        getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(MessagingService.NOTIFICATION_ID, mBuilder.build());
            }
        }

        private void sendMessage(Context context, String issue, String message, String lastRecipient){
                String postUrl = ConfigHelper.getConfigValue(context, context.getString(R.string.messageurl)) + context.getString(R.string.create);
                try {
                    RequestQueue requestQueue = Volley.newRequestQueue(context);

                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put(context.getString(R.string.mid), MessagingService.getToken(context));
                    jsonBody.put(context.getString(R.string.missue), issue);
                    jsonBody.put(context.getString(R.string.mbody), message);
                    jsonBody.put(context.getString(R.string.mrecipient),lastRecipient );

                    final String mRequestBody = jsonBody.toString();
                    Log.d("AWESOMEJSON",mRequestBody);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, postUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("BACKGROUND", error.toString());
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                Log.d("ENCODING", uee.toString());
                                throw new AuthFailureError();
                            }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString;
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                            }
                            throw new NullPointerException();
                        }
                    };

                    requestQueue.add(stringRequest);
                } catch (JSONException e) {
                    Log.d("JSON", e.toString());
                    e.printStackTrace();
                }
        }
    }
