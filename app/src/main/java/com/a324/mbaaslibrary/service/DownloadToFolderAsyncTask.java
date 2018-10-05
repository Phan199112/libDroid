package com.a324.mbaaslibrary.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.a324.mbaaslibrary.R;
import com.a324.mbaaslibrary.util.AsyncStringResult;
import com.a324.mbaaslibrary.util.Keys;
import com.a324.mbaaslibrary.util.DeviceUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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


public class DownloadToFolderAsyncTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private String fileType = null;
    public static final String A324 = "A324";
    private String fileName = "";
    private String directoryName = "";
    private File file;
    //ProgressDialog myProgressDialog;
    private Activity mActivity;
    private boolean mOpenWhenDone;
    private AsyncStringResult callback;

    public DownloadToFolderAsyncTask(Context context) {
        this.context = context;
    }

    public DownloadToFolderAsyncTask(Context context, String fileType, Activity activity,
                                     boolean openWhenDone, AsyncStringResult callback) {
        this.context = context;
        this.fileType = fileType;
        this.mActivity = activity;
        this.mOpenWhenDone = openWhenDone;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        int count = 0;
        String filePath = null;
        HttpsURLConnection connection = null;

        HttpClient client = null;
        HttpPost post = null;
        try {
            String userName = params[0];
            String sessionToken = params[1];
            String appName = params[2];
            String pathName = params[3];
             fileName = params[4];
            directoryName = params[5];
            String url = Keys.KEY_DOWNLOAD_FILE;

            //add the JSON Request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("appName", appName);
            requestBody.put("emailAddress", userName);
            requestBody.put("sessionToken", sessionToken);
            requestBody.put("path", pathName);
            Log.d("Async Download JSON:", requestBody.toString());

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
             file = new File(context.getFilesDir(), fileName);
            filePath = file.getAbsolutePath();
            FileOutputStream fos = context.openFileOutput(file.getName(), Context.MODE_PRIVATE); //root + "/a324/" +
            InputStream in = responseHttp.getEntity().getContent();
//
//            File file = new File(context.getFilesDir(), fileName);
//            filePath = file.getAbsolutePath();
//            FileOutputStream fos = context.openFileOutput(file.getName(), Context.MODE_WORLD_READABLE); //root + "/a324/" +
            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            while ((bytesRead = in.read(buffer)) > 0) {
                fos.write(buffer, 0, bytesRead);
                count++;
                publishProgress(count); //Here I am doing the update of my progress bar
            }

            fos.flush();
            fos.close();
            in.close();

        } catch (JSONException | NullPointerException | KeyStoreException | CertificateException
                | NoSuchAlgorithmException | IOException | KeyManagementException | NoSuchProviderException e) {
            e.printStackTrace();
            Log.e("Error", "Cancel async task to fail gracefully");
            this.cancel(true);
        }
        finally {
            //   if(connection != null)
            //    connection.disconnect();
        }
        return filePath;
    }


    protected void onProgressUpdate(Integer... progress) {
        //myProgressDialog.setProgress(progress[0]); //Since it's an inner class, Bar should be able to be called directly
    }

    @Override
    protected void onCancelled(){
        super.onCancelled();
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        LocalFolderDownloadService.saveFileToFolder(file, fileName, directoryName);
        if (mOpenWhenDone) {
            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + Environment.DIRECTORY_DOWNLOADS
                    + File.separator +  A324+ File.separator + directoryName
                    + File.separator + fileName;
            LocalFolderDownloadService.openFileWithIntent(mActivity, new File(fullPath));
        }else{
            callback.onResult(result);
        }
    }

}
