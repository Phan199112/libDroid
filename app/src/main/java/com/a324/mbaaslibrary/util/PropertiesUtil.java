package com.a324.mbaaslibrary.util;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesUtil {

    private static Properties props = null;

    /**
     * Initialize the PropertiesUtil
     * @param assets AssetManager
     * @param fileName 1 to many property file names
     */
    public static void init(AssetManager assets, final String... fileName) {
        String[] fileList = fileName;
        props = new Properties();
        for (int i = fileList.length - 1; i >= 0; i--) {
            String file = fileList[i];
            InputStream fileStream = null;
            try {
                fileStream = assets.open(file);
                props.load(fileStream);
                fileStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getStringProperty(final String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (final SecurityException e) {
            // Ignore
        }
        return (prop == null) ? props.getProperty(name) : prop;
    }

    public static String getStringProperty(final String name, final String defaultValue) {
        final String prop = getStringProperty(name);
        return (prop == null) ? defaultValue : prop;
    }

    public static int getIntegerProperty(final String name) {
        return getIntegerProperty(name, 0);
    }

    public static int getIntegerProperty(final String name, final int defaultValue) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (final SecurityException e) {
            // Ignore
        }
        if (prop == null) {
            prop = props.getProperty(name);
        }
        if (prop != null) {
            try {
                return Integer.parseInt(prop);
            } catch (final Exception ex) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static long getLongProperty(final String name) {
        return getLongProperty(name, 0);
    }

    public static long getLongProperty(final String name, final long defaultValue) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (final SecurityException e) {
            // Ignore
        }
        if (prop == null) {
            prop = props.getProperty(name);
        }
        if (prop != null) {
            try {
                return Long.parseLong(prop);
            } catch (final Exception ex) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static boolean getBooleanProperty(final String name) {
        return getBooleanProperty(name, false);
    }

    public static boolean getBooleanProperty(final String name, final boolean defaultValue) {
        final String prop = getStringProperty(name);
        return (prop == null) ? defaultValue : "true".equalsIgnoreCase(prop);
    }

}