package com.devicehive.sspasov.client.utils;

import android.util.Log;

/**
 * Created by toni on 01.06.15.
 */
public class L {
    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private static boolean debugMode = false;
    private static boolean debugData = false;

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public static void useDebugMode(boolean mode) {
        debugMode = mode;
    }

    public static boolean isUsingDebugMode() {
        return debugMode;
    }

    public static void useDebugData(boolean mode) {
        debugData = mode;
    }

    public static boolean isUsingDebugData() {
        return debugData;
    }

    public static void d(String tag, String message) {
        if (debugMode) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (debugMode) {
            Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (debugMode) {
            Log.e(tag, message, throwable);
        }
    }

    public static void w(String tag, String message) {
        if (debugMode) {
            Log.w(tag, message);
        }
    }
}
