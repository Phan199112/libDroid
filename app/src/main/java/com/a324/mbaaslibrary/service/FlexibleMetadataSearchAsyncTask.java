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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.HttpClient;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.entity.StringEntity;
import khandroid.ext.apache.http.util.EntityUtils;

/**
 * Created by kevinfischer on 7/17/18.
 */

public class FlexibleMetadataSearchAsyncTask extends AsyncTask<String, Integer, String> {


    private Context mContext;
    private AsyncStringResult mCallback;
    private Activity mActivity;
    private IllegalArgumentException exception;
    int start = 1;
    int results = 1000;
    int end = 1000;
    final int increment = 20;
    String userName = "";
    String sessionToken = "";
    String appName = "";
    String contentTypesString ="";
    ArrayList<String> contentTypes = new ArrayList<>();
    String query = "";

    public FlexibleMetadataSearchAsyncTask(Context context, Activity activity, AsyncStringResult callback){
        mContext = context;
        mActivity = activity;
        mCallback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpsURLConnection connection = null;
        String response = null;
        HttpClient client = null;
        HttpPost post = null;
        try {
            userName = params[0];
            sessionToken = params[1];
            appName = params[2];
            contentTypesString = params[3].replaceAll("\\[","")
                    .replaceAll("\\]","");
            contentTypes = new ArrayList<String>(Arrays.asList(contentTypesString.split(",")));
            query = params[4];
            if(params.length >= 7){
                start = Integer.valueOf(params[5]);
                //Marklogic recognizes 0 and 1 as the start but it messes up when paging
                if(start==0){
                    start = 1;
                }
                results = Integer.valueOf(params[6]);
            }
            if(params.length == 8){
                end = Integer.valueOf(params[7]);
            }else{
                end = start+results;
            }
            if(MetadataSearchQueryUtility.verifyValidFormattedString(query)) {
                String url = Keys.KEY_SEARCH_META;
                JSONObject requestBody = new JSONObject();
                requestBody.put("query", query);
                requestBody.put("start", start);
                requestBody.put("results", increment);
                requestBody.put("app", appName);
                requestBody.put("contentType", contentTypes.get(0).trim());
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
                response = EntityUtils.toString(responseHttp.getEntity());
            }else{
                Log.e("Error", "Malformed Query");
                throw new IllegalArgumentException("malformed search query");
            }
        }catch (JSONException | NullPointerException | KeyStoreException | CertificateException
                | NoSuchAlgorithmException | IOException | KeyManagementException |
                NoSuchProviderException | IllegalArgumentException e) {
            e.printStackTrace();
            Log.e("Error", "Cancel async task to fail gracefully");
            this.cancel(true);
        }
        finally {
            if(connection != null)
                connection.disconnect();
        }
        return response;
    }

    @Override
    protected void onCancelled(){
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        start+=increment;
        //have not returned enough results, call asynctask again
        if(start<end){
            FlexibleMetadataSearchAsyncTask task = new FlexibleMetadataSearchAsyncTask(mContext, mActivity, mCallback);
            task.execute(userName,sessionToken,appName, contentTypesString,query,Integer.toString(start),Integer.toString(results), Integer.toString(end));
        //move onto the next type
        }else{
            contentTypes.remove(0);
            if(contentTypes.size()>0){
                start = 0;
                FlexibleMetadataSearchAsyncTask task = new FlexibleMetadataSearchAsyncTask(mContext, mActivity, mCallback);
                task.execute(userName,sessionToken,appName, contentTypes.toString(),query,Integer.toString(start),Integer.toString(results));
            }
        }
        mCallback.onResult(result);

    }
}
