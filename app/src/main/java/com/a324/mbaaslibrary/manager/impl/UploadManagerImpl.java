package com.a324.mbaaslibrary.manager.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.a324.mbaaslibrary.service.UploadAsyncTask;
import com.a324.mbaaslibrary.manager.RegistrationManager;
import com.a324.mbaaslibrary.model.XDomainDocumentInfo;
import com.a324.mbaaslibrary.util.AsyncStringResult;
import com.a324.mbaaslibrary.util.DeviceUtility;


public class UploadManagerImpl {

    String phone = "";
    String imei = "";

    /**
     * Upload the file with the documentInfo object where required fields are validated
     * and then invokes the Volley REST Service and passes from one activity to the next activity
     *
     * @param documentInfo
     * @param currentActivity
     * @param appName
     */
    public String upload(XDomainDocumentInfo documentInfo, Activity currentActivity, Class<?> transitionActivity, String appName) {


        String serverPath = "";
        try {

            //Validate document information
            validateXDomainDocumentInfo(documentInfo);

            //Get the email from application context
            Context context = currentActivity.getApplicationContext();
            String email = DeviceUtility.getEmail(context);

            // Get the token from the registration manager
            RegistrationManager rm = new RegistrationManagerImpl(context);
            String token = rm.getSessionId();
            phone = DeviceUtility.getDevicePhoneNum(context);
            imei = DeviceUtility.getImei(context);
            //Create a request from Document Info
            UploadAsyncTask task = new UploadAsyncTask(context, currentActivity, new AsyncStringResult() {
                @Override
                public void onResult(String result) {
                    if (result != null) {
                        Log.d("result",result);
                    }}});

            task.execute(email, token, appName, documentInfo.getQualifiedPath(), documentInfo.getFileName(),
                    documentInfo.getQualifier(), documentInfo.getDiscriminators(), documentInfo.getTitle(), documentInfo.getDescription());

        } catch (Exception e) {
            Log.e("Upload Exception: ", e.getMessage());
        }
        return serverPath;
    }


    /**
     * Validate that the document info object contains data.  For use when transferring across domain.
     *
     * @param documentInfo The XDomainDocumentInfo object to check
     * @throws Exception if an error occurs
     */
    private void validateXDomainDocumentInfo(XDomainDocumentInfo documentInfo) throws Exception {
        //validate parameters
        if (documentInfo.getFileName() == null) {
            throw new Exception("File Name is a required field.");
        }
        if (documentInfo.getQualifiedPath() == null) {
            throw new Exception("Path to the file is a required field.");
        }
        if (documentInfo.getQualifier() == null) {
            throw new Exception("Qualifier is a required field.");
        }


    }


}
