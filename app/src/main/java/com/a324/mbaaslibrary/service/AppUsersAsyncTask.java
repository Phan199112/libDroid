package com.a324.mbaaslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.a324.mbaaslibrary.util.AsyncStringResult;
import com.a324.mbaaslibrary.util.DeviceUtility;
import com.a324.mbaaslibrary.util.Keys;
import com.a324.mbaaslibrary.util.MD5;
import com.a324.mbaaslibrary.util.PropertiesUtil;

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
import khandroid.ext.apache.http.client.methods.HttpGet;
import khandroid.ext.apache.http.util.EntityUtils;

/**
 * Created by kevinfischer on 4/3/18.
 */

public class AppUsersAsyncTask  extends AsyncTask<String, Integer, String> {

    private static final long MAX_FILE_SIZE = PropertiesUtil.getLongProperty("keyUploadSizeLimit");
    private Context context;
    private Activity mActivity;
    AsyncStringResult callback;

    public AppUsersAsyncTask(Context context, Activity activity, AsyncStringResult callback) {
        this.context = context;
        this.mActivity = activity;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient client = null;
        HttpGet get = null;
        String responseText = null;

        try {
            String email = params[0];
            String sessionToken = params[1];
            String appName = params[2];

            String urlString = Keys.KEY_APP_USERS + appName;
            client = SecuredConnectionService.getSecureHttpClient(context);

            get = new HttpGet(urlString);
            get.addHeader("Authorization", SecuredConnectionService.buildBasicAuthorizationString(email, appName ,
                    DeviceUtility.getDevicePhoneNum(context), DeviceUtility.getImei(context), sessionToken));
            HttpResponse response = client.execute(get);
            responseText = EntityUtils.toString(response.getEntity());
            Log.d("UPLOAD_FINISHED", "Response Code: " + response.getStatusLine().getStatusCode() + " Message: " + responseText);



        } catch (NullPointerException | KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | KeyManagementException | NoSuchProviderException e) {
            e.printStackTrace();
            Log.e("Error", "Cancel async task to fail gracefully");
            this.cancel(true);
        }
        finally {
            //   if(connection != null)
            //    connection.disconnect();
        }
        return responseText ;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected void onCancelled(){
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        callback.onResult(result);
    }
}
