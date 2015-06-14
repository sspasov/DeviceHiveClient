package com.devicehive.sspasov.client;

import android.app.Application;

import com.dataart.android.devicehive.DeviceData;
import com.devicehive.sspasov.client.config.ClientConfig;
import com.devicehive.sspasov.client.config.ClientPreferences;
import com.devicehive.sspasov.client.config.DeviceHiveConfig;
import com.devicehive.sspasov.client.objects.SampleDeviceClient;
import com.devicehive.sspasov.client.recievers.NetworkReceiver;
import com.devicehive.sspasov.client.utils.DeviceNotificationManager;
import com.devicehive.sspasov.client.utils.L;

public class SampleClientApplication extends Application {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = SampleClientApplication.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private SampleDeviceClient client;
    private ClientPreferences prefs;

    // ---------------------------------------------------------------------------------------------
    // Activity life cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onCreate() {
        super.onCreate();

        loadPreference();
        NetworkReceiver.startReciever(this);
        DeviceNotificationManager.init();

        L.useDebugMode(true);
        L.useDebugData(false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetworkReceiver.stopReceiver(this);
    }

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public SampleDeviceClient setupClientForDevice(DeviceData device) {
        if (client != null) {
            if (!client.getDevice().getId().equals(device.getId())) {
                resetClient();
                client = getClientForDevice(device);
            }
        } else {
            client = getClientForDevice(device);
        }
        return client;
    }

    public void resetClient() {
        if (client != null) {
            client.stopReceivingNotifications();
            client.clearAllListeners();
            client = null;
        }
    }

    public SampleDeviceClient getClient() {
        return client;
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private SampleDeviceClient getClientForDevice(DeviceData device) {
        SampleDeviceClient client = new SampleDeviceClient(getApplicationContext(), device);
        client.setApiEnpointUrl(ClientConfig.API_ENDPOINT);
        client.setAuthorisation(ClientConfig.USERNAME, ClientConfig.PASSWORD);
        client.setDebugLoggingEnabled(BuildConfig.DEBUG);
        return client;
    }

    private void loadPreference() {
        L.d(TAG, "loadPreferences()");

        prefs = new ClientPreferences(this);

        L.d(TAG, "ClientPref device is first time startup: " + prefs.isFirstStartup());
        ClientConfig.FIRST_STARTUP = prefs.isFirstStartup();


        if (prefs.getServerUrl() == null) {
            prefs.setServerUrlSync(DeviceHiveConfig.DEFAULT_API_ENDPOINT);
        }
        L.d(TAG, "ClientPref apiendpoint: " + prefs.getServerUrl());
        ClientConfig.API_ENDPOINT = prefs.getServerUrl();


        if (prefs.getUsername() == null) {
            prefs.setCredentialsSync(DeviceHiveConfig.DEFAULT_USERNAME,
                    DeviceHiveConfig.DEFAULT_PASSWORD);
        }
        L.d(TAG, "ClientPref username: " + prefs.getUsername());
        ClientConfig.USERNAME = prefs.getUsername();
        L.d(TAG, "ClientPref password: " + prefs.getPassword());
        ClientConfig.PASSWORD = prefs.getPassword();


        L.d(TAG, "ClientPref device async notifications: " + prefs.getAsyncNotifications());
        ClientConfig.ASYNC_NOTIFICATIONS = prefs.getAsyncNotifications();


        L.d(TAG, "ClientPref remember password: " + prefs.getRememberPassword());
        ClientConfig.REMEMBER_PASSWORD = prefs.getRememberPassword();
    }
}
