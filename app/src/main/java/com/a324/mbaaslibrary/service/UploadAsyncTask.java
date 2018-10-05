package com.a324.mbaaslibrary.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.a324.mbaaslibrary.R;
import com.a324.mbaaslibrary.util.AsyncStringResult;
import com.a324.mbaaslibrary.util.Keys;
import com.a324.mbaaslibrary.util.DeviceUtility;
import com.a324.mbaaslibrary.util.MD5;
import com.a324.mbaaslibrary.util.PropertiesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.HttpClient;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.entity.mime.HttpMultipartMode;
import khandroid.ext.apache.http.entity.mime.MultipartEntity;
import khandroid.ext.apache.http.entity.mime.content.FileBody;
import khandroid.ext.apache.http.entity.mime.content.StringBody;
import khandroid.ext.apache.http.util.EntityUtils;


public class UploadAsyncTask extends AsyncTask<String, Integer, String> {

    private static final long MAX_FILE_SIZE = PropertiesUtil.getLongProperty("keyUploadSizeLimit");
    private Context context;
    private Activity mActivity;
    AsyncStringResult callback;

    public UploadAsyncTask(Context context, Activity activity, AsyncStringResult callback) {
        this.context = context;
        this.mActivity = activity;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient client = null;
        HttpPost post = null;
        String serverPath = "", md5 = "";

        try {
            String email = params[0];
            String sessionToken = params[1];
            String appName = params[2];
            String filePath = params[3];
            String fileName = params[4];
            String qualifier = params[5];
            String discriminator = params[6];
            String title = params[7];
            String description = params[8];
            String meta = params.length > 9 ? params[9] : "";

            File file = new File(filePath);
            Log.d("file size ", Double.toString(file.length()));
            long fileSize = file.length();
            if(fileSize > MAX_FILE_SIZE) {
                String msg = "Invalid file size - File size too large.";
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                return msg;
            }
            md5 = MD5.calculateMD5(file);
            //------------------ CLIENT REQUEST
            String urlString = Keys.KEY_UPLOAD_FILE + appName;
            client = SecuredConnectionService.getSecureHttpClient(context);

            post = new HttpPost(urlString);
            post.addHeader("Authorization", SecuredConnectionService.buildBasicAuthorizationString(email, appName ,
                    DeviceUtility.getDevicePhoneNum(context), DeviceUtility.getImei(context), sessionToken));
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("fileName", new StringBody(fileName));
            entity.addPart("file", new FileBody(file));
            entity.addPart("title", new StringBody(title));
            entity.addPart("discriminator", new StringBody(discriminator));
            entity.addPart("email", new StringBody(email));
            entity.addPart("md5", new StringBody(md5));
            entity.addPart("qualifier", new StringBody(qualifier));
            entity.addPart("description", new StringBody(description));
            if(!"".equals(meta)) {
                entity.addPart("meta", new StringBody(meta));
            }
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            String responseText = EntityUtils.toString(response.getEntity());
            Log.d("UPLOAD_FINISHED", "Response Code: " + response.getStatusLine().getStatusCode() + " Message: " + responseText);
            JSONObject json = new JSONObject(responseText);
            serverPath = json.getString("fileId");
        } catch (JSONException | NullPointerException | KeyStoreException | CertificateException
                | NoSuchAlgorithmException | IOException | KeyManagementException | NoSuchProviderException e) {
            e.printStackTrace();
            Log.e("Error", "Cancel async task to fail gracefully");
            this.cancel(true);
        } finally {
            post.releaseConnection();
        }
        return serverPath;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    protected void onProgressUpdate(Integer... progress) {
        //myProgressDialog.setProgress(progress[0]); //Since it's an inner class, Bar should be able to be called directly
    }

    @Override
    protected void onCancelled(){
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);

        if(result != null) {
            Log.d("result", result);
        } else {
            Log.d("result", "An error occurred while retrieving file");
        }
        callback.onResult(result);
    }
}
