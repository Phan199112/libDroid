package com.a324.mbaaslibrary.manager;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.a324.mbaaslibrary.model.ValidationMethod;

public interface LauncherManager {

    /**
     * Launch the Registration Manager and get the Users information to see if they can get access
     * to the current application
     *
     * @param launcherActivity        Launcher Activity
     * @param mainActivityClass      Activity that takes over after the user gains access to the application
     * @param twoFactorActivityClass RegistrationActivity
     * @param appName                Application Name requesting authentication to the application
     */
    public void launch(Activity launcherActivity, Class<?> mainActivityClass, Class<?> twoFactorActivityClass, final String appName, final String manufacturer, final boolean isCloudMessageApp);

    public void launch(Activity launcherActivity, Class<?> mainActivityClass, Class<?> twoFactorActivityClass, final String appName, final String manufacturer, final boolean isCloudMessageApp, final Drawable drawable);

}
