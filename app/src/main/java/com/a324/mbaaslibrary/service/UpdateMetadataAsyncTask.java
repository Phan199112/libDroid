package com.a324.mbaaslibrary.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by kevinfischer on 5/31/18.
 */

public class UpdateMetadataAsyncTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private JSONObject requestBody;
    private final String JSONHASH= "jsonHash";

    /**
     * Save meta data to the sever
     * @param context The application Context
     * @param jsonObject The data you want to save in JSON format
     */
    public UpdateMetadataAsyncTask(Context context, JSONObject jsonObject) {
        this.context = context;
        this.requestBody = jsonObject;
    }

    @Override
    protected void onPreExecute(){
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
            String jsonHash = params[4];
            if(jsonHash == null || jsonHash.equals("")){
                Log.e("Error", "Invalid json hash; either it is null or empty");
                throw new NullPointerException();
            }
            requestBody.put(JSONHASH, jsonHash);
            String url = Keys.KEY_UPLOAD_META.concat(appName).concat("/").concat(metaType);

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


        } catch (NullPointerException | KeyStoreException | CertificateException
                | NoSuchAlgorithmException | IOException | KeyManagementException | NoSuchProviderException e) {
            e.printStackTrace();
            Log.e("Error", "Cancel async task to fail gracefully");
            this.cancel(true);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Error", "Could not add json hash to object");
            this.cancel(true);
        } finally {
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
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }
}
