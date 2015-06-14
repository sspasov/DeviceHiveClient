package com.devicehive.sspasov.client.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.devicehive.sspasov.client.R;
import com.devicehive.sspasov.client.config.ClientConfig;
import com.devicehive.sspasov.client.config.ClientPreferences;
import com.devicehive.sspasov.client.utils.L;

public class SettingsActivity extends PreferenceActivity {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = SettingsActivity.class.getSimpleName();
    public static final String API_CHANGED = "apiChanged";

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private ClientPreferences prefs;

    // ---------------------------------------------------------------------------------------------
    // Activity Life Cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "onCreate()");
        addPreferencesFromResource(R.xml.preference);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.view_toolbar, root, false);
        toolbar.setTitle(getString(R.string.action_settings));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        root.addView(toolbar, 0); // insert at top

        prefs = new ClientPreferences(this);
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private boolean isApiChanged() {
        if (ClientConfig.API_ENDPOINT.equals(prefs.getServerUrl())) {
            return false;
        }
        return true;
    }

    private void updatePreferences() {
        L.d(TAG, "updatePreferences()");

        ClientConfig.API_ENDPOINT = prefs.getServerUrl();
        L.d(TAG, "DeviceConfig.API_ENDPOINT: " + ClientConfig.API_ENDPOINT);

        ClientConfig.ASYNC_NOTIFICATIONS = prefs.getAsyncNotifications();
        L.d(TAG, "DeviceConfig.DEVICE_ASYNC_COMMAND_EXECUTION: " + ClientConfig.ASYNC_NOTIFICATIONS);

        ClientConfig.FIRST_STARTUP = prefs.isFirstStartup();
        L.d(TAG, "DeviceConfig.FIRST_STARTUP: " + ClientConfig.FIRST_STARTUP);

    }

    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        if (isApiChanged()) {
            updatePreferences();
            setResult(Activity.RESULT_OK, new Intent().putExtra(API_CHANGED, true));
        } else {
            updatePreferences();
            setResult(Activity.RESULT_OK);
        }
        finish();
    }
}