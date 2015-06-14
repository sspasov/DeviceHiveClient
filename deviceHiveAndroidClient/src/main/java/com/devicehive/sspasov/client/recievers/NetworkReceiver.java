package com.devicehive.sspasov.client.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.devicehive.sspasov.client.utils.L;

/**
 * Created by toni on 14.06.15.
 */
public class NetworkReceiver extends BroadcastReceiver {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = NetworkReceiver.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static NetworkReceiver mInstance;
    private static boolean isConnected;
    private static IntentFilter filter;

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public static void startReciever(Context context) {
        if (filter == null || mInstance == null) {
            filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mInstance = new NetworkReceiver();
        }
        context.registerReceiver(mInstance, filter);
    }

    public static void stopReceiver(Context context) {
        if (mInstance != null) {
            context.unregisterReceiver(mInstance);
        }
    }

    public static boolean isConnected() {
        return isConnected;
    }

    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            isConnected = true;
            Toast.makeText(context, "WIFI connected", Toast.LENGTH_LONG).show();
            L.d(TAG, "WIFI connected");
        } else if (networkInfo != null) {
            Toast.makeText(context, "Mobile Data connected", Toast.LENGTH_LONG).show();
            L.d(TAG, "Mobile Data connected");
            isConnected = true;
        } else {
            isConnected = false;
            Toast.makeText(context, "Connection lost", Toast.LENGTH_LONG).show();
            L.d(TAG, "Connection lost");
        }
    }
}