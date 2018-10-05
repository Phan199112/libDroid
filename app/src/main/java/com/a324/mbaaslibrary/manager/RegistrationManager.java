package com.a324.mbaaslibrary.manager;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.a324.mbaaslibrary.model.RegisterBean;
import com.a324.mbaaslibrary.model.ValidationMethod;

import org.json.JSONObject;

public interface RegistrationManager {

    void launchToMainActivityFromValidateGuid(final Activity validateGuidActivity, final Class<?> mainActivity,
                                              final String appName, final String manufacturer,
                                              final boolean isCloudMessageApp, Drawable drawable);

    /**
     * Handle the errors and Log the error message
     *
     * @param statusCode error status code
     * @return error message
     */
    String handleError(int statusCode);
    //String handleVolleyError(VolleyError error);

    /**
     * Constructs the REST request input parameters
     *
     * @param appName application name
     * @return JSONObject
     */
    JSONObject getRegistrationParams(String appName, boolean cloudMessaging);

    /**
     * Constructs the REST request input parameters
     *
     * @param appName application name
     * @param cloudMessaging true if using cloud messaging
     * @param validationMethod the method used to deliver registration one-time-password
     * @param emailToSMSDomain the domain used for sending email-to-sms (ex. vtext.com for Verizon)
     * @return JSONObject
     */
    JSONObject getRegistrationParams(String appName, boolean cloudMessaging,
                                     ValidationMethod validationMethod, String emailToSMSDomain);

    /**
     * Get the session ID from the shared preferences. If there there is no session token,
     * then set the token to Empty
     *
     * @return session id
     */
    String getSessionId();

    /**
     * Parse the JSONObject response to a RegisterBean object
     *
     * @param response JSONObject response
     * @return RegisterBean object
     */
    RegisterBean parseJsonResponse(JSONObject response);

    /**
     * Get the JSON parameters from the RegisterBean object
     *
     * @param rb RegisterBean object
     * @return JSONObject
     */
    JSONObject getJsonParams(RegisterBean rb);

    /**
     * Retrieves the guid from shared preferences
     *
     * @return
     */
    public String getGuid();

    /**
     *
     * Stores the guid in  shared preferences
     *
     * @return
     */
    public void setGuid(String guid);

    /**
     * Set the Users Session token in the SharedPreferences
     * @param sessionToken users session token
     */
    public void setUserSession(String sessionToken);
}

