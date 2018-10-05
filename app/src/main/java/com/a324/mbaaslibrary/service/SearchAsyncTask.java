package com.a324.mbaaslibrary.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.a324.mbaaslibrary.util.Keys;
import com.a324.mbaaslibrary.util.AsyncStringResult;
import com.a324.mbaaslibrary.util.DeviceUtility;

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


public class SearchAsyncTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private String fileType = null;
    ProgressDialog myProgressDialog;
    private Activity mActivity;
    AsyncStringResult callback;

    public SearchAsyncTask(Context context) {
        this.context = context;
    }

    /**
     * Get Meta data information
     *
     * @param context  The application Context
     * @param activity The current activity
     * @param callback The Async call back, you must implement the onResult method to convert the
     *                 string result into a usable format
     */
    public SearchAsyncTask(Context context, Activity activity, AsyncStringResult callback) {
        this.context = context;
        this.mActivity = activity;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient client = null;
        HttpPost post = null;
        String response = null;
        HttpsURLConnection connection = null;
        try {

            String userName = params[0];
            String sessionToken = params[1];
            String appName = params[2];
            String searchQuery = params[3];
            String contentType = params[4];
            String url = Keys.KEY_SEARCH;

            JSONObject requestBody = createSearchQueryBody(appName,  searchQuery, contentType);

            client = SecuredConnectionService.getSecureHttpClient(context);
            post = new HttpPost(url);
            post.addHeader("Authorization", SecuredConnectionService.buildBasicAuthorizationString(userName, appName,
                    DeviceUtility.getDevicePhoneNum(context), DeviceUtility.getImei(context), sessionToken));

            Log.d("SearchContent: ", requestBody.toString());
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
    protected void onCancelled(){
        super.onCancelled();
    }

    private JSONObject createSearchQueryBody(String appName, String searchQuery, String contentType) throws JSONException {
        //add the JSON Request body - these are all flex fields
        JSONObject requestBody = new JSONObject();
        requestBody.put("appName", appName);
        //this is for the 1020M app alias and a flex field
        if (searchQuery != null) {
            if(!searchQuery.isEmpty()) {
                requestBody.put("searchQuery", searchQuery);
                Log.d("**adding Query**", searchQuery);
            }
        } else {
           throw new JSONException("Please enter valid query term. It cannot be empty.");
        }

        if(contentType != null && !contentType.isEmpty()) {
            requestBody.put("contentType", contentType);
        }

        Log.d("SearchReqBody:", requestBody.toString());
        return requestBody;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        callback.onResult(result);
    }

}
