package com.a324.mbaaslibrary.service;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.a324.mbaaslibrary.util.AsyncStringResult;
import com.a324.mbaaslibrary.util.DeviceUtility;
import com.a324.mbaaslibrary.util.Keys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;

import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.HttpClient;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.entity.StringEntity;
import khandroid.ext.apache.http.util.EntityUtils;


/**
 * Created by Jen Baker 06/2018.
 */

public class NotificationAsyncTask extends AsyncTask<String, Integer, String> {

    private Context mContext;
    private AsyncStringResult mCallback;

    public NotificationAsyncTask(Context context, AsyncStringResult callback) {
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
            String notificationJson = params[3];
            String url = Keys.KEY_NOTIFY + appName;
            JSONObject requestBody = new JSONObject(notificationJson);
            Log.d("Notification JSON:", requestBody.toString());

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
            response = EntityUtils.toString(responseHttp.getEntity());
        } catch (JSONException | NullPointerException | KeyStoreException | CertificateException
                | NoSuchAlgorithmException | IOException | KeyManagementException | NoSuchProviderException e) {
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
        mCallback.onResult(result);
    }
}