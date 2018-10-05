package com.a324.mbaaslibrary.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.a324.mbaaslibrary.model.RegisterBean;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import static android.telephony.TelephonyManager.SIM_STATE_READY;


public class DeviceUtility {

    public final static String TAG = DeviceUtility.class.getSimpleName();
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int PRIVATE_MODE = 0;
    private final static String SESSION_PREF = "SessionPref";
    private static final String SESSION_EMAIL = "email";
    private static final String SESSION_KEY = "token";
    private static final String SESSION_GUID = "guid";

    public static String getEmail(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SESSION_PREF, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String domain = PropertiesUtil.getStringProperty("domain");
        String email = null;
        try {
            Account[] accounts = AccountManager.get(context).getAccounts();
            if (accounts.length > 0) {
                for (Account account : accounts) {
                    if (account.name.endsWith(domain)) {
                        String savedEmail = sharedPref.getString(SESSION_EMAIL, "empty");
                        if(savedEmail.equalsIgnoreCase("empty")) {
                            email = account.name;
                            editor.putString(SESSION_EMAIL, email);
                            editor.commit();
                        }else {
                            String name = account.name;
                            if (savedEmail.equalsIgnoreCase(name)) {
                                email = name;
                            } else {
                                Toast.makeText(context, "Email account has been removed", Toast.LENGTH_SHORT).show();
                                editor.remove(SESSION_GUID);
                                editor.remove(SESSION_KEY);
                                editor.remove(SESSION_EMAIL);
                                editor.commit();
                                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(launchIntent);
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return email;
    }

    public static String getGCM() {
        String gcm = "";
        try {
             FirebaseInstanceId.getInstance().getToken();
            Log.i("***GCM**", gcm);
        } catch(Exception ex) {
            Log.e("***GCM**", ex.getMessage());
        }
        return gcm;

    }

    public static String getDevicePhoneNum(Context context) {
        String phNumber = null;
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmp = tMgr.getLine1Number().substring(0);

        if (tmp != null && tmp != "") {
            phNumber = RegisterBean.formatPhoneNum(tmp);
        }
        return phNumber;
    }

    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getDeviceId(Context context) {
        return getImei(context);
    }

    /**
     * Determine is the signal strength on cellular or wifi network is strong enough to send/download information
     * @param activity The activity calling this
     * @param minSignalStrengthAllowable The minimum allowable signal strength
     * @return true is signal strength is good, false if weak
     */
    public static boolean isSignalStrengthStrong(Activity activity, int minSignalStrengthAllowable) {
        //Wifi signal
        int numberOfLevels = 5;
        int wifiLevel = 0;
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiLevel = wifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), numberOfLevels);
        }

        //mobile signal
        int cellLevel = 0;
        try {
            TelephonyManager tm = (TelephonyManager) activity.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            for (final CellInfo info : tm.getAllCellInfo()) {
                if (info instanceof CellInfoGsm) {
                    final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                    cellLevel = gsm.getLevel();
                } else if (info instanceof CellInfoCdma) {
                    final CellSignalStrengthCdma cdma = ((CellInfoCdma)info).getCellSignalStrength();
                    cellLevel = cdma.getLevel();
                } else if (info instanceof CellInfoLte) {
                    final CellSignalStrengthLte lte = ((CellInfoLte)info).getCellSignalStrength();
                    cellLevel = lte.getLevel();
                } else {
                    throw new Exception("Unknown type of cell signal!");
                }
            }
        } catch (Exception e) {
            Log.e("Telephony", "Unable to obtain cell signal information", e);
        }

        if (wifiLevel >= minSignalStrengthAllowable || cellLevel >= minSignalStrengthAllowable) {
            return true;
        }
        return false;
    }

    public static String getLatLong(Context context) {
        if (checkPlayServices(context)) {
            // Building the GoogleApi client
            GoogleApiHelper gAH = GoogleApiHelper.getInstance(context);
            return gAH.getLatLong();
        }
        return "0.0,0.0";
    }

    public static boolean checkPlayServices(Context context) {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog((Activity)context, resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(context,
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                ((Activity)context).finish();
            }
            return false;
        }
        return true;
    }

    public static String getEmailToSMSDomain(Context context){

        // Get System TELEPHONY service reference
        TelephonyManager tManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        // Get carrier name
        if (SIM_STATE_READY == tManager.getSimState()) {
            String simOperatorCode = tManager.getSimOperator();
            ServiceProviders.ServiceProvider provider
                    = ServiceProviders.getServiceProvider(simOperatorCode);

            if (provider != null){
                String domain = ServiceProviders.SERVICE_PROVIDER_EMAIL_TO_SMS_DOMAINS.get(provider);
                Log.i(TAG, "Found domain: " + domain);
                return domain;
            }else{
                Log.e(TAG, "Unable to find provider for operator code: " + simOperatorCode);
            }
        }else{
            Log.e(TAG, "SIM Card was not in the READY state.");
        }

        return null;

    }

}
