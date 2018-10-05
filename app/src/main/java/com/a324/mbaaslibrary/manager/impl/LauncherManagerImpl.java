package com.a324.mbaaslibrary.manager.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.a324.mbaaslibrary.R;
import com.a324.mbaaslibrary.manager.LauncherManager;
import com.a324.mbaaslibrary.manager.RegistrationManager;
import com.a324.mbaaslibrary.model.RegisterBean;
import com.a324.mbaaslibrary.model.ValidationMethod;
import com.a324.mbaaslibrary.service.LauncherAsyncTask;
import com.a324.mbaaslibrary.service.PreRegisterAsyncTask;
import com.a324.mbaaslibrary.util.AsyncJsonResult;
import com.a324.mbaaslibrary.util.DeviceUtility;
import com.a324.mbaaslibrary.util.PropertiesUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LauncherManagerImpl implements LauncherManager {

    public static final String EMPTY = "Empty";
    public static final String PROJECT_PROPERTIES = "project.properties";
    public static final String LAT_LONG = "latLong";
    public static final String VALUE = "0.0,0.0";

    /**
     * Launch the Registration Manager and get the Users information to see if they can get access
     * to the current application
     *
     * @param mainActivityClass      Activity that takes over after the user gains access to the application
     * @param twoFactorActivityClass RegistrationActivity
     * @param appName                Application Name requesting authentication to the application
     */
    public void launch(final Activity currentActivity, final Class<?> mainActivityClass, final Class<?> twoFactorActivityClass, final String appName, final String manufacturer, final boolean cloudMessaging) {

        try {
            final Context context = currentActivity.getApplicationContext();
            PropertiesUtil.init(context.getAssets(), PROJECT_PROPERTIES);  //Load the property file
            final RegistrationManagerImpl rm = new RegistrationManagerImpl(context);
            final String sessionId = rm.getSessionId();
            JSONObject jsonParamsObj = rm.getRegistrationParams(appName, cloudMessaging);
            if (jsonParamsObj == null) {
                return;
            }

            if (sessionId.equals(EMPTY)) {
                Log.d("SessionEMpt:<>", sessionId);
                // need to reset the lat-long parameters to get a session token
                // temporary hack, but will come up with long term solution
                jsonParamsObj.remove(LAT_LONG);
                jsonParamsObj.put(LAT_LONG, VALUE);
            } else {

            }

            LauncherAsyncTask task = new LauncherAsyncTask(context, jsonParamsObj, new AsyncJsonResult() {
                @Override
                public void onResult(JSONObject object) {
                    Log.d("Launcher OnResult: ", object.toString());
                    launcherSuccess(rm, context, sessionId, object,
                            currentActivity, mainActivityClass, twoFactorActivityClass);
                }

                @Override
                public void onFailure(int statusCode) {
                    Log.e("Registration Failure", " Status Code: " + statusCode);
                    launcherFailure(rm, statusCode, context, currentActivity);
                }
            });
            task.execute(appName, manufacturer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void launch(final Activity currentActivity, final Class<?> mainActivityClass,
                       final Class<?> twoFactorActivityClass, final String appName,
                       final String manufacturer, final boolean cloudMessaging, final Drawable drawable) {

        try {
            final Context context = currentActivity.getApplicationContext();
            PropertiesUtil.init(context.getAssets(), PROJECT_PROPERTIES);  //Load the property file
            final RegistrationManagerImpl rm = new RegistrationManagerImpl(context);
            final String sessionId = rm.getSessionId();
            JSONObject regBean = rm.getRegistrationParams(appName, cloudMessaging);

            final JSONObject registerBean = regBean;

            PreRegisterAsyncTask task = new PreRegisterAsyncTask(context, regBean, new AsyncJsonResult() {
                @Override
                public void onResult(JSONObject preRegResult) {
                    Log.d("Pre-Register OnResult: ", preRegResult.toString());
                    try {
                        preRegisterSuccess(appName, manufacturer, rm, context, sessionId,
                                preRegResult, currentActivity, mainActivityClass,
                                twoFactorActivityClass, cloudMessaging, drawable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode) {
                    Log.e("Pre-Register Failure", " Status Code: " + statusCode);
                    preRegisterFailure(rm, statusCode, context, currentActivity);
                }
            });
            task.execute(appName, manufacturer);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void preRegisterSuccess(final String appName, final String manufacturer,
                                           final RegistrationManagerImpl rm, final Context context,
                                           final String sessionId,
                                           final JSONObject preRegResult,
                                           final Activity currentActivity,
                                           final Class<?> mainActivityClass,
                                           final Class<?> twoFactorActivityClass,
                                           final boolean cloudMessaging,
                                           final Drawable drawable) throws JSONException {

        final RegisterBean launchRegBean = rm.getRegisterBeanForRequest(appName, cloudMessaging);

        RegisterBean preRegBean = rm.parseJsonResponse(preRegResult);
        Log.d("pre-register response", preRegBean.toString());
        String msg = preRegBean.getEventMessage();

        if (msg.equals(RegistrationManagerImpl.RESPONSE_VALIDATIONMETHODREQUIRED)) {

            // Validation method needs to be selected

            // Set new layout so user can choose email or sms option
            currentActivity.setContentView(R.layout.activity_select_validation_method);
            ImageView appImage = (ImageView) currentActivity.findViewById(R.id.appWelcomeImage);
            appImage.setImageDrawable(drawable);

            // Set up spinner
            final Spinner validationMethodSpinner = (Spinner) currentActivity.findViewById(R.id.validationMethodSpinner);
            List<String> validationOptions = new ArrayList<>();
            validationOptions.add("EMAIL");
            validationOptions.add("SMS");
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (currentActivity, android.R.layout.simple_spinner_item, validationOptions); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            validationMethodSpinner.setAdapter(spinnerArrayAdapter);

            // Set up confirm button
            final Button confirmButton = (Button) currentActivity.findViewById(R.id.confirmButton);
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    confirmButton.setClickable(false);
                    confirmButton.setEnabled(false);

                    // Get selected item from spinner and use
                    String validationMethod = (String) validationMethodSpinner.getSelectedItem();

                    String domain = DeviceUtility.getEmailToSMSDomain(context);

                    if (domain != null) {
                        Toast.makeText(currentActivity, "Selected: "
                                + validationMethod + " " + domain, Toast.LENGTH_SHORT);


                        // Update values in the launch register bean for selected validation and calculated
                        // email-to-sms domain
                        launchRegBean.setValidationMethod(ValidationMethod.valueOf(validationMethod));
                        launchRegBean.setEmailToSMSDomain(domain);
                    } else {
                        Log.i("pre-register", "email-to-sms domain was null");
                    }

                    runLauncherAsyncTask(appName, manufacturer, rm, context, sessionId,
                            launchRegBean, currentActivity, mainActivityClass,
                            twoFactorActivityClass);

                }
            });
        } else {
            // use unmodified launcher register bean
            runLauncherAsyncTask(appName, manufacturer, rm, context, sessionId,
                    launchRegBean, currentActivity, mainActivityClass,
                    twoFactorActivityClass);
        }


    }

    private static void runLauncherAsyncTask(final String appName, final String manufacturer,
                                             final RegistrationManagerImpl rm, final Context context,
                                             final String sessionId,
                                             final RegisterBean launchRegBean,
                                             final Activity currentActivity,
                                             final Class<?> mainActivityClass,
                                             final Class<?> twoFactorActivityClass) {
        // Create json from launch register bean to use for launch async task parameter
        Gson gson = new Gson();
        JSONObject launchRegBeanJson = null;
        String str = gson.toJson(launchRegBean);
        try {
            launchRegBeanJson = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSONException", e.getMessage());
        }
        if (launchRegBeanJson == null) {
            return;
        }

        if (sessionId.equals(EMPTY)) {
            Log.d("SessionEMpt:<>", sessionId);
            // need to reset the lat-long parameters to get a session token
            // temporary hack, but will come up with long term solution
            launchRegBeanJson.remove(LAT_LONG);
            try {
                launchRegBeanJson.put(LAT_LONG, VALUE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Create Launcher Async Task and execute
        LauncherAsyncTask task = new LauncherAsyncTask(context, launchRegBeanJson, new AsyncJsonResult() {
            @Override
            public void onResult(JSONObject object) {
                Log.d("Launcher OnResult: ", object.toString());
                launcherSuccess(rm, context, sessionId, object,
                        currentActivity, mainActivityClass, twoFactorActivityClass);
            }

            @Override
            public void onFailure(int statusCode) {
                Log.e("Registration Failure", " Status Code: " + statusCode);
                launcherFailure(rm, statusCode, context, currentActivity);
            }
        });
        task.execute(appName, manufacturer);
    }


    private static void preRegisterFailure(RegistrationManager rm, int statusCode, Context context,
                                           final Activity currentActivity) {
        String errorString = rm.handleError(statusCode);
        Log.d("error string", errorString);
        ProgressBar pb = (ProgressBar) currentActivity.findViewById(R.id.loading_spinner2);
        pb.setVisibility(View.GONE);
        Toast.makeText(context, errorString, Toast.LENGTH_LONG).show();
    }

    private static void launcherSuccess(RegistrationManager rm, Context context, String sessionId, JSONObject response,
                                        final Activity currentActivity, final Class<?> mainActivityClass, final Class<?> twoFactorActivityClass) {
        RegisterBean rb = rm.parseJsonResponse(response);
        Log.d("register response", rb.toString());
        String msg = rb.getEventMessage();
        if (msg.equals(RegistrationManagerImpl.RESPONSE_REGISTERED)) {
            Intent registeredIntent = new Intent(context, mainActivityClass);
            registeredIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            registeredIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            registeredIntent.putExtra("token", sessionId);

            if (rb.getRoles() != null && rb.getRoles().size() > 0) {
                ArrayList<String> roles = rb.getRoles();
                registeredIntent.putStringArrayListExtra("roles", roles);
                Log.d("RoleAdded:", roles.size() + "");
            }

            context.startActivity(registeredIntent);
        } else {
            Intent intent = new Intent(context, twoFactorActivityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            //important indicator which lets the RegisterActivity know which layout to show: lockedOut or login
            if (msg.equals(RegistrationManagerImpl.RESPONSE_LOCKED)) {
                bundle.putBoolean("isLocked", true);
            } else {
                bundle.putBoolean("isLocked", false);
            }
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
        currentActivity.finish();
    }

    private static void launcherFailure(RegistrationManager rm, int statusCode, Context context, final Activity currentActivity) {
        String errorString = rm.handleError(statusCode);
        Log.d("error string", errorString);
        ProgressBar pb = (ProgressBar) currentActivity.findViewById(R.id.loading_spinner2);
        pb.setVisibility(View.GONE);
        Toast.makeText(context, errorString, Toast.LENGTH_LONG).show();
    }
}
