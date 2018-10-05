package com.a324.mbaaslibrary.service;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.LabeledIntent;
        import android.content.pm.PackageManager;
        import android.content.pm.ResolveInfo;
        import android.net.Uri;
        import android.net.wifi.WifiManager;
        import android.os.Environment;
        import android.support.v4.content.FileProvider;
        import android.telephony.CellInfo;
        import android.telephony.CellInfoCdma;
        import android.telephony.CellInfoGsm;
        import android.telephony.CellInfoLte;
        import android.telephony.CellSignalStrengthCdma;
        import android.telephony.CellSignalStrengthGsm;
        import android.telephony.CellSignalStrengthLte;
        import android.telephony.TelephonyManager;
        import android.util.Log;
        import android.webkit.MimeTypeMap;
        import android.widget.Toast;

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.net.URL;
        import java.security.KeyManagementException;
        import java.security.KeyStore;
        import java.security.KeyStoreException;
        import java.security.NoSuchAlgorithmException;
        import java.security.NoSuchProviderException;
        import java.security.SecureRandom;
        import java.security.cert.Certificate;
        import java.security.cert.CertificateException;
        import java.security.cert.CertificateFactory;
        import java.security.cert.X509Certificate;
        import java.util.ArrayList;
        import java.util.List;

        import javax.net.ssl.HttpsURLConnection;
        import javax.net.ssl.SSLContext;
        import javax.net.ssl.TrustManager;
        import javax.net.ssl.TrustManagerFactory;
        import javax.net.ssl.X509TrustManager;

        import khandroid.ext.apache.http.androidextra.Base64;
        import khandroid.ext.apache.http.client.HttpClient;
        import khandroid.ext.apache.http.conn.ClientConnectionManager;
        import khandroid.ext.apache.http.conn.scheme.Scheme;
        import khandroid.ext.apache.http.conn.ssl.SSLSocketFactory;
        import khandroid.ext.apache.http.impl.client.DefaultHttpClient;

public class LocalFolderDownloadService {

    public static final String A324 = "A324";

    /**
     * Open a File
     *
     * @param context application context
     * @param path    path to the file
     */
    protected static void openFile(Context context, String path) {

        File f = new File(path);
        Intent i = setUpFileIntent(context, f);
        PackageManager pm = context.getPackageManager();
        if (i.resolveActivity(pm) != null) {
            context.startActivity(i);
        } else {
            Toast.makeText(context, "Please download an application that is compatible with this file.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Open a document from a folder
     *
     * @param context       the current context
     * @param fileName      the file name
     * @param directoryName the directory name of where the file is located
     */
    public static void openDocumentFromAFolder(Context context, String fileName, String directoryName) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Environment.DIRECTORY_DOWNLOADS
                + File.separator + A324
                + File.separator + directoryName;
        File f = new File(fullPath + File.separator + fileName);
        Intent i = setUpFileIntent(context, f);
        PackageManager pm = context.getPackageManager();
        if (i.resolveActivity(pm) != null) {
            context.startActivity(i);
        } else {
            Toast.makeText(context, "Please download an application that is compatible with this file.", Toast.LENGTH_LONG).show();
        }
    }

    /*private static Intent setUpFileIntent(Context context, File f) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        if (Uri.fromFile(f).toString().contains(".doc") || Uri.fromFile(f).toString().contains(".docx")) {
            //Word document
            i.setDataAndType(Uri.fromFile(f), "application/msword");

        } else if (Uri.fromFile(f).toString().contains(".pdf")) {
            //PDF file
            i.setDataAndType(Uri.fromFile(f), "application/pdf");


        } else if (Uri.fromFile(f).toString().contains(".ppt") || Uri.fromFile(f).toString().contains(".pptx")) {
            //Powerpoint document
            i.setDataAndType(Uri.fromFile(f), "application/vnd.ms-powerpoint");
        } else if (Uri.fromFile(f).toString().contains(".xls") || Uri.fromFile(f).toString().contains(".xlsx")) {
            //Excel document
            i.setDataAndType(Uri.fromFile(f), "application/vnd.ms-excel");
        } else if (Uri.fromFile(f).toString().contains(".zip") || Uri.fromFile(f).toString().contains(".rar")) {
            //WAV audio file
            i.setDataAndType(Uri.fromFile(f), "application/x-wav");
        } else if (Uri.fromFile(f).toString().contains(".rft")) {
            //RTF file
            i.setDataAndType(Uri.fromFile(f), "application/rtf");
        } else if (Uri.fromFile(f).toString().contains(".wav") || Uri.fromFile(f).toString().contains(".mp3")) {
            //WAV audio file
            i.setDataAndType(Uri.fromFile(f), "audio/x-wav");
        } else if (Uri.fromFile(f).toString().contains(".wmv")) {
            //WMV video file
            i.setDataAndType(Uri.fromFile(f), "video/x-ms-wmv");

        } else if (Uri.fromFile(f).toString().contains(".gif")) {
            //gif file
            i.setDataAndType(Uri.fromFile(f), "image/gif");
        } else if (Uri.fromFile(f).toString().contains(".jpg") || Uri.fromFile(f).toString().contains(".jpeg") || Uri.fromFile(f).toString().contains(".png")) {
            //image file
            i.setDataAndType(Uri.fromFile(f), "image/jpeg");
        } else if (Uri.fromFile(f).toString().contains(".txt")) {
            //text file
            i.setDataAndType(Uri.fromFile(f), "text/plain");
        } else if (Uri.fromFile(f).toString().contains(".3gp") || Uri.fromFile(f).toString().contains(".mpg") || Uri.fromFile(f).toString().contains(".mpeg")
                || Uri.fromFile(f).toString().contains(".mpe") || Uri.fromFile(f).toString().contains(".mp4")) {
            //video file
            i.setDataAndType(Uri.fromFile(f), "video/*");

        } else {
        */
            //i.setDataAndType(Uri.fromFile(f), "*/*");
        /*}
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return i;
    }*/

    public static Intent setUpFileIntent(Context context, File f) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String fileExtension = fileExt(f.getName());
        if (fileExtension != null) {
            String mimeType = myMime.getMimeTypeFromExtension(fileExtension.substring(1));
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", f);
            newIntent.setDataAndType(uri,mimeType);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return newIntent;
        } else {
            return null;
        }
    }

    /**
     * Tries to start a new activity to view the given file based on it's file extension
     * @param context Current context
     * @param file File to open
     */
    public static void openFileWithIntent(Context context, File file){
        try {
            Intent newIntent = setUpFileIntent(context, file);
            context.startActivity(newIntent);
        } catch (Exception e) {
            Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Return the file extension of the given file name. Ex) "sample.pdf" returns ".pdf"
     * @return
     */
    public static String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    /**
     * Save a file to the users device
     *
     * @param file     File to save
     * @param fileName name of the file
     */
    public static void saveFileToFolder(File file, String fileName, String directoryName) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Environment.DIRECTORY_DOWNLOADS
                + File.separator + A324 + File.separator + directoryName; //MbaasConstants.DOWNLOADED_DOCUMENTS;

        try {
            byte[] bytes = read(file);
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File outFile = new File(fullPath, fileName);
            outFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(outFile, false);

            fOut.write(bytes);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read a file to a byte array
     *
     * @param aFile the file to read
     * @return file in a byte array
     * @throws IOException if one occurs
     */
    private static byte[] read(File aFile) throws IOException {
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(aFile);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }


}
