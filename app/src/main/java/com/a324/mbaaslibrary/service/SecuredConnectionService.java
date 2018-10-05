package com.a324.mbaaslibrary.service;


import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
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

public class SecuredConnectionService {
    private static TrustManager[] getTrustManagers(Context context) throws CertificateException, NoSuchProviderException, IOException, KeyStoreException, NoSuchAlgorithmException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
        InputStream caInput = context.getResources().openRawResource(com.a324.mbaaslibrary.R.raw.cert);
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        ks.load(null, null);
        ks.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(ks);
        TrustManager[] trustManagers = tmf.getTrustManagers();
        final X509TrustManager origTrustManager = (X509TrustManager) trustManagers[0];

        TrustManager[] wrappedTrustManagers = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return origTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            origTrustManager.checkClientTrusted(certs, authType);
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {

                    }
                }
        };
        return wrappedTrustManagers;
    }

    /**
     * Get a secure http client
     * @param context The current context
     * @return
     */
    protected static HttpClient getSecureHttpClient(Context context)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, KeyManagementException, NoSuchProviderException {

        // Create an SSLContext that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, getTrustManagers(context), new SecureRandom());
        SSLSocketFactory factory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HttpClient client = new DefaultHttpClient();
        ClientConnectionManager manager = client.getConnectionManager();
        manager.getSchemeRegistry().register(new Scheme("https", 443, factory));

        return client;
    }
    /**
     * Build the basic authorization string
     * @param username current username
     * @param appName application name
     * @param password password
     * @return basic authorization string
     */
    protected static String buildBasicAuthorizationString(String username, String appName , String phone,
                                                          String imei, String password) throws NullPointerException{
        String credentials = "" ;
        if (!username.isEmpty()) {
            credentials = username+"_"+ appName+"_"+phone+"_"+imei+ ":" + password;
        } else  {
            credentials = appName + ":" + password;
        }
        Log.d("CRED: ", credentials);
        return "Basic " + new String(Base64.encode(credentials.getBytes(), Base64.NO_WRAP));
    }
}
