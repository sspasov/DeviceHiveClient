package com.devicehive.sspasov.client.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class ClientPreferences {
    private static final String TAG = ClientPreferences.class.getSimpleName();

    private final static String NAMESPACE = "";

    private final Context context;
    private final SharedPreferences preferences;


    public ClientPreferences(final Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(
                context.getPackageName() + "_devicehiveprefs",
                Context.MODE_PRIVATE);
    }

    /**
     * preference for server url
     */
    private final static String KEY_SERVER_URL = NAMESPACE.concat("KEY_SERVER_URL");

    public String getServerUrl() {
        return preferences.getString(KEY_SERVER_URL, null);
    }

    public void setServerUrlSync(String serverUrl) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_SERVER_URL, serverUrl);
        editor.apply();
    }

    /**
     * preference for username and password
     */
    private final static String KEY_USERNAME = NAMESPACE.concat("KEY_USERNAME");
    private final static String KEY_PASSWORD = NAMESPACE.concat("KEY_PASSWORD");

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, DeviceHiveConfig.DEFAULT_USERNAME);
    }

    public String getPassword() {
        return preferences.getString(KEY_PASSWORD, DeviceHiveConfig.DEFAULT_PASSWORD);
    }


    public void setCredentialsSync(String username, String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public void setCredentialsAsync(final String username, final String password) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                setCredentialsSync(username, password);
                return null;
            }

        }.execute();
    }

    /**
     * preference for first time startup configuration
     */
    private final static String KEY_FIRST_STARTUP = NAMESPACE.concat("KEY_FIRST_STARTUP");

    public boolean isFirstStartup() {
        return preferences.getBoolean(KEY_FIRST_STARTUP, DeviceHiveConfig.DEFAULT_FIRST_STARTUP);
    }

    public void setIsFirstStartup(boolean isFirstStartup) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_FIRST_STARTUP, isFirstStartup);
        editor.apply();
    }

    /**
     * preference for receiving notifications async
     */
    private final static String KEY_ASYNC_NOTIFICATIONS = NAMESPACE.concat("KEY_ASYNC_NOTIFICATIONS");

    public boolean getAsyncNotifications() {
        return preferences.getBoolean(KEY_ASYNC_NOTIFICATIONS, DeviceHiveConfig.DEFAULT_ASYNC_NOTIFICATIONS);
    }

    public void setAsyncNotifications(boolean asyncNotifications) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_ASYNC_NOTIFICATIONS, asyncNotifications);
        editor.apply();
    }


    /**
     * preference for remembering user password
     */
    private final static String KEY_REMEMBER_PASSWORD = NAMESPACE.concat("KEY_REMEMBER_PASSWORD");

    public boolean getRememberPassword() {
        return preferences.getBoolean(KEY_REMEMBER_PASSWORD, DeviceHiveConfig.DEFAULT_REMEMBER_PASSWORD);
    }

    public void setRememberPassword(boolean asyncNotifications) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_REMEMBER_PASSWORD, asyncNotifications);
        editor.apply();
    }
}
