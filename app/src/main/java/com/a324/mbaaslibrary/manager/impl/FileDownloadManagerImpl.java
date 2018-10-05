package com.a324.mbaaslibrary.manager.impl;

import android.app.Activity;
import android.content.Context;

import com.a324.mbaaslibrary.service.DownloadAsyncTask;
import com.a324.mbaaslibrary.service.DownloadToFolderAsyncTask;
import com.a324.mbaaslibrary.manager.FileDownloadManager;
import com.a324.mbaaslibrary.manager.RegistrationManager;
import com.a324.mbaaslibrary.model.DocumentInfo;
import com.a324.mbaaslibrary.util.AsyncStringResult;
import com.a324.mbaaslibrary.util.DeviceUtility;

public class FileDownloadManagerImpl implements FileDownloadManager {

    /** Download a document to the users phone
    * @param appName The application requesting the document
    * @param di The document information
    * @param activity The activity requesting the document
    */
    public void downloadFile(String appName, DocumentInfo di, Activity activity) {
        Context context = activity.getApplicationContext();
        RegistrationManager rm = new RegistrationManagerImpl(context);
        String email = DeviceUtility.getEmail(context);
        String token = rm.getSessionId();
        int index = di.getFileName().lastIndexOf(".");
        String fileType = di.getFileName().substring(index + 1) ;
        DownloadAsyncTask task = new DownloadAsyncTask(context, fileType.toUpperCase(), activity);
        task.execute(email, token, appName, di.getQualifiedPath(), di.getFileName());
    }

    /**
     * Download a document to the users phone file system
     * @param appName The application requesting the document
     * @param di The document information
     * @param activity The activity requesting the document
     * @param directoryName The name of the directory where the file is located
     * @param openWhenDone Open the document when it's done downloading to the folder
     */
    public void downloadFileToFolder(String appName, DocumentInfo di, Activity activity, String directoryName,
                                     boolean openWhenDone, AsyncStringResult callback) {
        Context context = activity.getApplicationContext();
        RegistrationManager rm = new RegistrationManagerImpl(context);
        String email = DeviceUtility.getEmail(context);
        String token = rm.getSessionId();
        int index = di.getFileName().lastIndexOf(".");
        String fileType = di.getFileName().substring(index + 1) ;
        DownloadToFolderAsyncTask task = new DownloadToFolderAsyncTask(context, fileType.toUpperCase(),
                activity, openWhenDone, callback);
        task.execute(email, token, appName, di.getQualifiedPath(), di.getFileName(), directoryName);
    }
}
