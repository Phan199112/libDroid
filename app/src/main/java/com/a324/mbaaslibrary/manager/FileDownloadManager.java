package com.a324.mbaaslibrary.manager;

import android.app.Activity;

import com.a324.mbaaslibrary.model.DocumentInfo;
import com.a324.mbaaslibrary.util.AsyncStringResult;

public interface FileDownloadManager {

    /**
     * Download a document to the users phone
     * @param appName The application requesting the document
     * @param di The document information
     * @param activity The activity requesting the document
     */
    void downloadFile(String appName, DocumentInfo di, Activity activity) ;

    /**
     * Download a document to the users phone file system
     * @param appName The application requesting the document
     * @param di The document information
     * @param activity The activity requesting the document
     * @param directoryName The name of the directory where the file is located
     * @param openWhenDone Open the document when it's done downloading to the folder
     */
    void downloadFileToFolder(String appName, DocumentInfo di, Activity activity, String directoryName,
                              boolean openWhenDone, AsyncStringResult callback);

}