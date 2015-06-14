package com.devicehive.sspasov.client.utils;

import com.dataart.android.devicehive.Notification;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by toni on 13.06.15.
 */
public class DeviceNotificationManager {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String DEVICE_STATUS = "DeviceStatus";
    private static final String DEVICE_UPDATE = "$device-update";

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private static List<String> devicesList;
    private static List<List<Notification>> devicesNotificationsList;

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public static void init() {
        devicesList = new LinkedList<>();
        devicesNotificationsList = new LinkedList<>();
    }


    public static void putNotification(String deviceId, Notification notification) {
        if (notification.getName().contains(DEVICE_STATUS) || notification.getName().contains(DEVICE_UPDATE)) {
            return;
        }
        if (devicesList.contains(deviceId)) {
            int idx = devicesList.indexOf(deviceId);
            devicesNotificationsList.get(idx).add(notification);
        } else {
            devicesList.add(deviceId);
            List<Notification> newList = new LinkedList<>();
            newList.add(notification);
            devicesNotificationsList.add(newList);
        }
    }

    public static List<Notification> getNotifications(String deviceId) {
        if (devicesList.contains(deviceId)) {
            int idx = devicesList.indexOf(deviceId);
            return devicesNotificationsList.get(idx);
        }
        return null;
    }
}
