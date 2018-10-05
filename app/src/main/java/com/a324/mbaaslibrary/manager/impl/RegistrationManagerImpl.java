package com.a324.mbaaslibrary.manager.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a324.mbaaslibrary.R;
import com.a324.mbaaslibrary.model.ValidationMethod;
import com.a324.mbaaslibrary.service.RegistrationAsyncTask;
import com.a324.mbaaslibrary.manager.RegistrationManager;
import com.a324.mbaaslibrary.model.RegisterBean;
import com.a324.mbaaslibrary.util.AsyncJsonResult;
import com.a324.mbaaslibrary.util.DeviceUtility;
import com.a324.mbaaslibrary.util.PropertiesUtil;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class RegistrationManagerImpl implements RegistrationManager {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Context context;

    //Static parameters
    private int PRIVATE_MODE = 0;
    private static final String PROJECT_PROPERTIES = "project.properties";
    private static final String SESSION_PREF = "SessionPref";
    private static final String SESSION_KEY = "token";
    private static final String SESSION_GUID = "guid";
    public static final String RESPONSE_REGISTERED = "REGISTERED";
    public static final String RESPONSE_LOCKED = "LOCKED";
    public static final String RESPONSE_VALIDATIONMETHODREQUIRED = "VALIDATIONMETHODREQUIRED";


    /**
     * Constructor
     * @param context application context
     */
    public RegistrationManagerImpl(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(SESSION_PREF, PRIVATE_MODE);
        editor = sharedPref.edit();
    }

    /**
     * Set the Users Session token in the SharedPreferences
     * @param sessionToken users session token
     */
    public void setUserSession(String sessionToken) {
        editor.remove(SESSION_KEY);
        editor.remove(SESSION_GUID);
        editor.putString(SESSION_KEY, sessionToken);
        editor.commit();
    }

    /**
     * Get the JSON parameters from the RegisterBean object
     * @param rb RegisterBean object
     * @return JSONObject
     */
    public JSONObject getJsonParams(RegisterBean rb) {
        Gson gson = new Gson();
        JSONObject jsonObject = null;
        String str = gson.toJson(rb);
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", e.getMessage());
        }
        return jsonObject;
    }

    /**
     * Get the session ID from the shared preferences. If there there is no session token,
     * then set the token to Empty
     * @return session id
     */
    public String getSessionId() {
        String sessionToken = sharedPref.getString(SESSION_KEY, "Empty");
        return sessionToken;
    }

    /**
     * Get the session ID from the shared preferences. If there there is no session token,
     * then set the token to Empty
     * @return session id
     */
    public String getGuid() {
        String guid = sharedPref.getString(SESSION_GUID, "empty");
        return guid;
    }

    @Override
    public void setGuid(String guid) {
        editor.remove(SESSION_KEY);
        editor.remove(SESSION_GUID);
        editor.putString(SESSION_GUID, guid);
        editor.commit();
    }

    /**
     * Parse the JSONObject response to a RegisterBean object
     * @param response JSONObject response
     * @return RegisterBean object
     */
    public RegisterBean parseJsonResponse(JSONObject response) {
        if (response == null || response.length() == 0) {
            return null;
        }

        String jsonStr = response.toString();
        RegisterBean rb = new Gson().fromJson(jsonStr, RegisterBean.class);

        return rb;
    }

    /**
     * Get the RegisterBean object based on the Application Name
     * @param appName application name
     * @return RegisterBean object
     */
    public RegisterBean getRegisterBeanForRequest(String appName , boolean cloudMessaging){
        return getRegisterBeanForRequest(appName, cloudMessaging, ValidationMethod.EMAIL, "");
    }

    public RegisterBean getRegisterBeanForRequest(String appName , boolean cloudMessaging,
                                                  ValidationMethod validationMethod, String emailToSMSDomain) {
        String email = DeviceUtility.getEmail(context);
        String phoneNumber = DeviceUtility.getDevicePhoneNum(context);
        double latitude = 0.1;
        double longitude = 0.1;
        String deviceId = DeviceUtility.getDeviceId(context);
        String gcm = "";
        if (cloudMessaging == true ) {
            gcm = DeviceUtility.getGCM();
        }
        RegisterBean rb = new RegisterBean(email, phoneNumber, latitude, longitude, appName, deviceId, gcm);
        rb.setValidationMethod(validationMethod);
        rb.setEmailToSMSDomain(emailToSMSDomain);

        return rb;
    }

    /**
     * Constructs the REST request input parameters
     * @param appName application name
     * @return JSONObject
     */
    public JSONObject getRegistrationParams(String appName, boolean cloudMessaging ) {
        return getRegistrationParams(appName, cloudMessaging,
                ValidationMethod.EMAIL, "");
    }

    public JSONObject getRegistrationParams(String appName, boolean cloudMessaging,
                                            ValidationMethod validationMethod, String emailToSMSDomain) {
        RegisterBean rb = getRegisterBeanForRequest(appName , cloudMessaging, validationMethod,
                emailToSMSDomain);
        Gson gson = new Gson();
        JSONObject jsonObject = null;
        String str = gson.toJson(rb);
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSONException", e.getMessage());
        }
        return jsonObject;
    }

    /**
     * Handle the errors and Log the error message
     * @param statusCode connection status code
     * @return error message
     */
    public String handleError(int statusCode) {
        String errorStr = "";
        if (statusCode == 305) {
            errorStr = "The requested resource MUST be accessed through the proxy";
        } else if (statusCode == 401) {
            errorStr = "HTTP Error 401 - Unauthorized: Access is denied due to invalid credentials.";
        } else if (statusCode == 400) {
            errorStr = "Registration was not found for user. Check email was send in request";
        } else if (statusCode == 403) {
            errorStr = "You do not have permission to access this server";
        } else {
            errorStr = context.getString(R.string.error_server);
        }
        return errorStr;
    }

    private void validateGuid(final Activity act, final Class<?> mainActivityClass, final String appName, final String manufacturer, final boolean isCloudMessageApp) {
        PropertiesUtil.init(act.getAssets(), PROJECT_PROPERTIES);  //Load the property file
        //final RegistrationManager rm = new RegistrationManagerImpl(act.getApplicationContext());
        JSONObject jsonParamsObj = getRegistrationParams(appName, isCloudMessageApp);
        try {
            EditText txtField = (EditText) act.findViewById(R.id.mbaas_field_guid);
            jsonParamsObj.put("guid", txtField.getText().toString());
            Log.d("validate service", jsonParamsObj.toString());

            RegistrationAsyncTask task = new RegistrationAsyncTask(context, jsonParamsObj, new AsyncJsonResult() {
                @Override
                public void onResult(JSONObject object) {
                    Log.d("Registration OnResult: ", object.toString());
                    registrationSuccess(object, act, mainActivityClass);
                }

                @Override
                public void onFailure(int responseCode) {
                    Log.e("Registration Failure ", "responseCode: " + responseCode);
                    registrationFailure(responseCode, act);
                }
            });
            task.execute(appName, manufacturer);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void launchToMainActivityFromValidateGuid(final Activity validateGuidActivity, final Class<?> mainActivity,
                                                     final String appName, final String manufacturer,
                                                     final boolean isCloudMessageApp, Drawable drawable) {
        final Context context = validateGuidActivity;
        validateGuidActivity.setContentView(R.layout.activity_register);
        ImageView icon = (ImageView)validateGuidActivity.findViewById(R.id.imageView);
        icon.setImageDrawable(drawable);

        //get isLocked value from Intent to determine which layout to show
        Bundle bundle = validateGuidActivity.getIntent().getExtras();
        boolean isLocked;
        if (bundle == null) {
            isLocked = false;
        } else {
            isLocked = bundle.getBoolean("isLocked");
        }
        EditText email = (EditText) validateGuidActivity.findViewById(R.id.mbaas_field_email);
        email.setText(DeviceUtility.getEmail(context));
        LinearLayout lockedLayout = (LinearLayout) validateGuidActivity.findViewById(R.id.mbaas_lockout_layout);
        LinearLayout loginLayout = (LinearLayout) validateGuidActivity.findViewById(R.id.mbaas_login_layout);
        LinearLayout loginTitle = (LinearLayout) validateGuidActivity.findViewById(R.id.mbaas_login_title);
        LinearLayout verifyLayout = (LinearLayout) validateGuidActivity.findViewById(R.id.mbaas_validating_layout);

        if (isLocked) {
            lockedLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
            loginTitle.setVisibility(View.GONE);
            verifyLayout.setVisibility(View.GONE);
        } else {
            lockedLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
            loginTitle.setVisibility(View.VISIBLE);
            verifyLayout.setVisibility(View.GONE);
        }

        String guid = "";
        EditText txtField = (EditText) validateGuidActivity.findViewById(R.id.mbaas_field_guid);
        if(!this.getGuid().trim().equals("empty")) {
             guid = this.getGuid();
            txtField.setText(this.getGuid());
            Log.d("zzGUID", guid);
        }
//        else {
//            guid = txtField.getText().toString();
//        }


        final Button loginButton = (Button) validateGuidActivity.findViewById(R.id.mbaas_login_btn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    //hide login layout/guid entry components & show loading spinner
                    LinearLayout validateLayout = (LinearLayout) validateGuidActivity.findViewById(R.id.mbaas_validating_layout);
                    validateLayout.setVisibility(View.VISIBLE);
                    LinearLayout layout = (LinearLayout) validateGuidActivity.findViewById(R.id.mbaas_login_layout);
                    LinearLayout loginTitle = (LinearLayout) validateGuidActivity.findViewById(R.id.mbaas_login_title);
                    layout.setVisibility(View.GONE);
                    loginTitle.setVisibility(View.GONE);

                    loginButton.setEnabled(false); //disable button until a response has been received from server
                    validateGuid(validateGuidActivity, mainActivity, appName, manufacturer, isCloudMessageApp);

            }
        });
    }

    private void registrationSuccess(JSONObject response, final Activity act, final Class<?> mainActivityClass) {
        RegisterBean rb = parseJsonResponse(response);
        Button loginBtn = (Button) act.findViewById(R.id.mbaas_login_btn);
        loginBtn.setEnabled(true);
        String sessionToken = rb.getSessionToken();
        if (sessionToken != null) {
            setUserSession(sessionToken);
            Intent registeredIntent = new Intent(act.getApplicationContext(), mainActivityClass);
            registeredIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            registeredIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (rb.getRoles() != null && rb.getRoles().size() > 0) {
                ArrayList<String> roles = rb.getRoles();
                registeredIntent.putStringArrayListExtra("roles", roles);
                Log.d("RoleAdded:", roles.size() + "");
            }

            act.getApplicationContext().startActivity(registeredIntent);
            act.finish();
        } else {
            Log.d("response", "error response");
            TextView invalidView = (TextView) act.findViewById(R.id.mbaas_invalid_login_msg);
            invalidView.setVisibility(View.VISIBLE);
            TextView guidView = (TextView) act.findViewById(R.id.mbaas_login_btn);
            guidView.setVisibility(View.GONE);
        }
    }

    private void registrationFailure(int statusCode, final Activity act) {
        Button loginBtn = (Button) act.findViewById(R.id.mbaas_login_btn);
                loginBtn.setEnabled(true);

                //Hide Validating text and progress bar
                LinearLayout validatingLayout = (LinearLayout) act.findViewById(R.id.mbaas_validating_layout);
                validatingLayout.setVisibility(View.GONE);

                LinearLayout loginLayout = (LinearLayout) act.findViewById(R.id.mbaas_login_layout);
                LinearLayout loginTitle = (LinearLayout) act.findViewById(R.id.mbaas_login_title);
                TextView guidView = (TextView) act.findViewById(R.id.mbaas_login_btn);
                TextView invalidView = (TextView) act.findViewById(R.id.mbaas_invalid_login_msg);

                if (statusCode == 305) { //invalid entry - show invalid message
                    loginLayout.setVisibility(View.VISIBLE);
                    loginTitle.setVisibility(View.VISIBLE);
                    invalidView.setVisibility(View.VISIBLE);
                    guidView.setVisibility(View.GONE);
                } else if (statusCode == 403) {  //exceeded # of attempts: account locked, show locked screen
                    LinearLayout lockedLayout = (LinearLayout) act.findViewById(R.id.mbaas_lockout_layout);
                    lockedLayout.setVisibility(View.VISIBLE);
                    loginLayout.setVisibility(View.GONE);
                    loginTitle.setVisibility(View.GONE);
                    validatingLayout.setVisibility(View.GONE);
                } else { //Server, Network, etc. errors
                    loginLayout.setVisibility(View.VISIBLE);
                    loginTitle.setVisibility(View.VISIBLE);
                    invalidView.setVisibility(View.GONE);
                    guidView.setVisibility(View.VISIBLE);
                    String errorString = handleError(statusCode);
                    Toast.makeText(act.getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
                }
    }

}
