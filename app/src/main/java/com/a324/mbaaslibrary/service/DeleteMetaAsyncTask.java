package com.a324.mbaaslibrary.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.a324.mbaaslibrary.util.Keys;
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


public class DeleteMetaAsyncTask extends AsyncTask<String, Integer, String> {


    private Context context;
    private JSONObject requestBody;

    public DeleteMetaAsyncTask(Context context) {
        this.context = context;
    }

    /**
     * Get Meta data information
     *
     * @param context  The application Context

     */
    public DeleteMetaAsyncTask(Context context, JSONObject jsonObject ) {
        this.context = context;
        this.requestBody = jsonObject;
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
            String metaType = params[3];
            String alias = params[4];
            String url = Keys.KEY_DELETE_META.concat(appName).concat("/").concat(metaType);

            client = SecuredConnectionService.getSecureHttpClient(context);
            post = new HttpPost(url);
            post.addHeader("Authorization", SecuredConnectionService.buildBasicAuthorizationString(userName, appName,
                    DeviceUtility.getDevicePhoneNum(context), DeviceUtility.getImei(context), sessionToken));

            Log.d("writing body: ", requestBody.toString());
            String request = requestBody.toString();
            StringEntity input = new StringEntity(request);
            input.setContentType("application/json");
            post.setEntity(input);

            HttpResponse responseHttp = client.execute(post);
            response = EntityUtils.toString(responseHttp.getEntity());

        } catch ( NullPointerException | KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | KeyManagementException | NoSuchProviderException e) {
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
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

}
