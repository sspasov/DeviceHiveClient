package com.devicehive.sspasov.client.objects;

import android.content.Context;
import android.util.Log;

import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.Notification;
import com.dataart.android.devicehive.client.SingleDeviceClient;
import com.devicehive.sspasov.client.config.ClientConfig;

import java.util.LinkedList;
import java.util.List;

public class SampleDeviceClient extends SingleDeviceClient {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "SampleDeviceClient";

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private final List<NotificationsListener> notificationListeners = new LinkedList<>();
    private final List<CommandListener> commandListeners = new LinkedList<>();
    private final List<DeviceDataListener> deviceDataListeners = new LinkedList<>();

    // ---------------------------------------------------------------------------------------------
    // Interfaces
    // ---------------------------------------------------------------------------------------------
    public interface NotificationsListener {
        void onReceivesNotification(Notification notification);
    }

    public interface DeviceDataListener {
        void onReloadDeviceDataFinished();

        void onReloadDeviceDataFailed();
    }

    public interface CommandListener {
        void onStartSendingCommand(Command command);

        void onFinishSendingCommand(Command command);

        void onFailSendingCommand(Command command);
    }

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public SampleDeviceClient(Context context, DeviceData deviceData) {
        super(context, deviceData);
    }

    public void addNotificationsListener(NotificationsListener listener) {
        notificationListeners.add(listener);
    }

    public void removeNotificationsListener(NotificationsListener listener) {
        notificationListeners.remove(listener);
    }

    public void addDeviceDataListener(DeviceDataListener listener) {
        deviceDataListeners.add(listener);
    }

    public void removeDeviceDataListener(DeviceDataListener listener) {
        deviceDataListeners.remove(listener);
    }

    public void addCommandListener(CommandListener listener) {
        commandListeners.add(listener);
    }

    public void removeCommandListener(CommandListener listener) {
        commandListeners.remove(listener);
    }

    public void clearAllListeners() {
        notificationListeners.clear();
        commandListeners.clear();
        deviceDataListeners.clear();
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private void notifyNotificationListeners(Notification notification) {
        for (NotificationsListener listener : notificationListeners) {
            listener.onReceivesNotification(notification);
        }
    }

    private void notifyCommandListenersStartSending(Command command) {
        for (CommandListener listener : commandListeners) {
            listener.onStartSendingCommand(command);
        }
    }

    private void notifyCommandListenersFinishSending(Command command) {
        for (CommandListener listener : commandListeners) {
            listener.onFinishSendingCommand(command);
        }
    }

    private void notifyCommandListenersFailSending(Command command) {
        for (CommandListener listener : commandListeners) {
            listener.onFailSendingCommand(command);
        }
    }

    private void notifyReloadDeviceDataFinished() {
        for (DeviceDataListener listener : deviceDataListeners) {
            listener.onReloadDeviceDataFinished();
        }
    }

    private void notifyReloadDeviceDataFailed() {
        for (DeviceDataListener listener : deviceDataListeners) {
            listener.onReloadDeviceDataFailed();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
    @Override
    protected boolean shouldReceiveNotificationAsynchronously(Notification notification) {
        return ClientConfig.ASYNC_NOTIFICATIONS;
    }

    @Override
    protected void onStartReceivingNotifications() {
        Log.d(TAG, "onStartReceivingNotifications");
    }

    @Override
    protected void onReceiveNotification(final Notification notification) {
        Log.d(TAG, "onReceiveNotification: " + notification);
        notifyNotificationListeners(notification);
    }

    @Override
    protected void onStopReceivingNotifications() {
        Log.d(TAG, "onStopReceivingNotifications");
    }

    @Override
    protected void onStartSendingCommand(Command command) {
        Log.d(TAG, "onStartSendingCommand: " + command);
        notifyCommandListenersStartSending(command);
    }

    @Override
    protected void onFinishSendingCommand(Command command) {
        Log.d(TAG, "onFinishSendingCommand: " + command);
        notifyCommandListenersFinishSending(command);
    }

    @Override
    protected void onFailSendingCommand(Command command) {
        Log.d(TAG, "onFailSendingCommand: " + command);
        notifyCommandListenersFailSending(command);
    }

    @Override
    protected void onFinishReloadingDeviceData(DeviceData deviceData) {
        notifyReloadDeviceDataFinished();
    }

    @Override
    protected void onFailReloadingDeviceData() {
        notifyReloadDeviceDataFailed();
    }
}
