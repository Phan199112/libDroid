package com.a324.mbaaslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.a324.mbaaslibrary.util.AsyncStringResult;
import com.a324.mbaaslibrary.util.DeviceUtility;
import com.a324.mbaaslibrary.util.Keys;
import com.a324.mbaaslibrary.util.MetadataSearchQueryUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.HttpClient;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.entity.StringEntity;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by kevinfischer on 8/30/18.
 */

public class GeofenceCrossNotificationAsyncTask  extends AsyncTask<String, Integer, String> {


    private Context mContext;
    private AsyncStringResult mCallback;

    public GeofenceCrossNotificationAsyncTask(Context context, AsyncStringResult callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpsURLConnection connection = null;
        String response = null;
        HttpClient client = null;
        HttpPost post = null;
        try {
            String userName = params[0];
            String sessionToken = params[1];
            String appName = params[2];
            String lat = params[3];
            String lng = params[4];
                String url = Keys.KEY_GEOFENCE_NOTIFY;
                JSONObject requestBody = new JSONObject();
                requestBody.put("appName", appName);
                requestBody.put("userName", userName);
                requestBody.put("lat", lat);
                requestBody.put("lng", lng);
                Log.d("Async filter JSON:", requestBody.toString());

                client = SecuredConnectionService.getSecureHttpClient(mContext);
                post = new HttpPost(url);
                post.addHeader("Authorization", SecuredConnectionService.buildBasicAuthorizationString(userName, appName,
                        DeviceUtility.getDevicePhoneNum(mContext), DeviceUtility.getImei(mContext), sessionToken));

                Log.d("writing body: ", requestBody.toString());
                String request = requestBody.toString();
                StringEntity input = new StringEntity(request);
                input.setContentType("application/json");
                post.setEntity(input);
                HttpResponse responseHttp = client.execute(post);
                response = Integer.toString(responseHttp.getStatusLine().getStatusCode());
        } catch (JSONException | NullPointerException | KeyStoreException | CertificateException
                | NoSuchAlgorithmException | IOException | KeyManagementException |
                NoSuchProviderException | IllegalArgumentException e) {
            e.printStackTrace();
            Log.e("Error", "Cancel async task to fail gracefully");
            this.cancel(true);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return response;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(mCallback != null){
            mCallback.onResult(result);
        }

    }
}