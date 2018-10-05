package com.a324.mbaaslibrary.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.a324.mbaaslibrary.util.AsyncJsonResult;
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
import khandroid.ext.apache.http.client.methods.HttpGet;
import khandroid.ext.apache.http.util.EntityUtils;

/**
 * Created by kevinfischer on 9/14/17.
 */

public class AppInformationAsyncTask extends AsyncTask<String, Integer, String> {

    private Context context;
    AsyncJsonResult callback;

    public AppInformationAsyncTask(Context context, AsyncJsonResult callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpClient client = null;
        HttpGet get = null;
        String response = null;
        HttpsURLConnection connection = null;
        try {
            String userName = strings[0];
            String sessionToken = strings[1];
            String appName = strings[2];
            String url = Keys.KEY_APP_REG.concat(appName);

            client = SecuredConnectionService.getSecureHttpClient(context);
            get = new HttpGet(url);
            get.addHeader("Authorization", SecuredConnectionService.buildBasicAuthorizationString(userName, appName,
                    DeviceUtility.getDevicePhoneNum(context), DeviceUtility.getImei(context), sessionToken));



            HttpResponse responseHttp = client.execute(get);
            response = EntityUtils.toString(responseHttp.getEntity());

        }  catch (NullPointerException | KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | KeyManagementException | NoSuchProviderException e) {
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

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            callback.onResult(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
