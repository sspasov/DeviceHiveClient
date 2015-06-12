package com.devicehive.sspasov.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.EquipmentData;
import com.dataart.android.devicehive.EquipmentState;
import com.dataart.android.devicehive.Notification;
import com.dataart.android.devicehive.client.commands.DeviceClientCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceClassEquipmentCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceEquipmentStateCommand;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.devicehive.sspasov.client.R;
import com.devicehive.sspasov.client.SampleClientApplication;
import com.devicehive.sspasov.client.adapters.SimplePagerAdapter;
import com.devicehive.sspasov.client.dialogs.ParameterDialog;
import com.devicehive.sspasov.client.fragmetns.DeviceInformationFragment;
import com.devicehive.sspasov.client.fragmetns.DeviceNotificationsFragment;
import com.devicehive.sspasov.client.fragmetns.DeviceSendCommandFragment;
import com.devicehive.sspasov.client.fragmetns.EquipmentListFragment;
import com.devicehive.sspasov.client.objects.SampleDeviceClient;
import com.devicehive.sspasov.client.utils.L;
import com.devicehive.sspasov.client.views.SlidingTabLayout;

import java.util.LinkedList;
import java.util.List;

public class DeviceActivity extends BaseActivity implements
        SampleDeviceClient.NotificationsListener, SampleDeviceClient.CommandListener,
        DeviceSendCommandFragment.CommandSender, DeviceSendCommandFragment.ParameterProvider,
        ParameterDialog.ParameterDialogListener {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = DeviceActivity.class.getSimpleName();
    private final static int TAG_GET_DEVICE = getTagId(GetDeviceCommand.class);
    private final static int TAG_GET_EQUIPMENT = getTagId(GetDeviceClassEquipmentCommand.class);
    private final static int TAG_GET_EQUIPMENT_STATE = getTagId(GetDeviceEquipmentStateCommand.class);

    public static final String EXTRA_DEVICE = "extra_device";

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private SampleClientApplication app;
    private DeviceData device;
    private SampleDeviceClient deviceClient;

    private ViewPager viewPager;

    private DeviceInformationFragment deviceInformationFragment;
    private EquipmentListFragment equipmentListFragment;
    private DeviceNotificationsFragment deviceNotificationsFragment;
    private DeviceSendCommandFragment deviceSendCommandFragment;

    private List<Notification> receivedNotifications = new LinkedList<>();
    private List<EquipmentData> equipment = new LinkedList<>();
    private List<EquipmentState> equipmentState = new LinkedList<>();

    // ---------------------------------------------------------------------------------------------
    // Activity life cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        L.d(TAG, "onCreate()");

        if (getIntent().hasExtra(EXTRA_DEVICE)) {
            device = getIntent().getExtras().getParcelable(EXTRA_DEVICE);
        } else {
            throw new IllegalArgumentException("Device extra should be provided");
        }

        app = (SampleClientApplication) getApplication();
        deviceClient = app.setupClientForDevice(device);

        deviceInformationFragment = DeviceInformationFragment.newInstance();
        deviceInformationFragment.setDeviceData(device);
        deviceInformationFragment.setContext(this);

        equipmentListFragment = EquipmentListFragment.newInstance();

        deviceNotificationsFragment = DeviceNotificationsFragment.newInstance();

        deviceSendCommandFragment = DeviceSendCommandFragment.newInstance();
        deviceSendCommandFragment.setParameterProvider(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(device.getName());
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SimplePagerAdapter(this, getSupportFragmentManager()));

        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(viewPager);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getColorBasedForPosition(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.d(TAG, "onResume()");
        deviceClient.addNotificationsListener(this);
        deviceClient.addCommandListener(this);
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                startRequest();
            }
        }, 10);
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.d(TAG, "onPause()");
        deviceClient.stopReceivingNotifications();
        deviceClient.removeCommandListener(this);
        deviceClient.removeNotificationsListener(this);
        if (isFinishing()) {
            app.resetClient();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.d(TAG, "onStop()");
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private int getColorBasedForPosition(int pos) {
        int color;
        switch (pos) {
            case 0:
                color = getResources().getColor(R.color.red);
                break;
            case 1:
                color = getResources().getColor(R.color.pink);
                break;
            case 2:
                color = getResources().getColor(R.color.violet);
                break;
            default:
                color = getResources().getColor(R.color.violet_dark);
                break;
        }
        return color;
    }

    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onReceivesNotification(Notification notification) {
        L.d(TAG, "onReceivesNotification()");
        receivedNotifications.add(notification);
        deviceNotificationsFragment.setNotifications(receivedNotifications);
    }

    @Override
    public void sendCommand(Command command) {
        L.d(TAG, "sendCommand()");
        deviceClient.sendCommand(command);
    }

    @Override
    protected void startRequest() {
        L.d(TAG, "startEquipmentRequest()");
        startCommand(new GetDeviceClassEquipmentCommand(deviceClient.getDevice()
                .getDeviceClass()
                .getId()));
    }

    @Override
    protected void onReceiveResult(final int resultCode, final int tagId, final Bundle resultData) {
        L.d(TAG, "onReceiveResult()");
        switch (resultCode) {
            case DeviceHiveResultReceiver.MSG_COMPLETE_REQUEST:
                break;
            case DeviceHiveResultReceiver.MSG_EXCEPTION:
                final Throwable exception = DeviceClientCommand.getThrowable(resultData);
                L.e(TAG, "Failed to execute network command", exception);
                if (tagId == TAG_GET_DEVICE) {
                    showErrorDialog("Failed to retrieve device data");
                } else if (tagId == TAG_GET_EQUIPMENT) {
                    // retry
                    startRequest();
                } else if (tagId == TAG_GET_EQUIPMENT_STATE) {
                    // retry
                    startCommand(new GetDeviceEquipmentStateCommand(deviceClient.getDevice()
                            .getId()));
                }
                break;
            case DeviceHiveResultReceiver.MSG_STATUS_FAILURE:
                int statusCode = DeviceClientCommand.getStatusCode(resultData);
                showErrorDialog("Server returned status code: " + statusCode);
                break;
            case DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE:
                if (tagId == TAG_GET_DEVICE) {
                    final DeviceData deviceData = GetDeviceCommand.getDevice(resultData);
                    deviceInformationFragment.setDeviceData(deviceData);
                } else if (tagId == TAG_GET_EQUIPMENT) {
                    this.equipment = GetDeviceClassEquipmentCommand.getEquipment(resultData);
                    deviceSendCommandFragment.setEquipment(equipment);
                    startCommand(new GetDeviceEquipmentStateCommand(deviceClient.getDevice()
                            .getId()));
                } else if (tagId == TAG_GET_EQUIPMENT_STATE) {
                    this.equipmentState =
                            GetDeviceEquipmentStateCommand.getEquipmentState(resultData);
                    equipmentListFragment.setEquipment(equipment, equipmentState);
                    if (!deviceClient.isReceivingNotifications()) {
                        deviceClient.startReceivingNotifications();
                    }
                }
                break;
        }
    }

    @Override
    public void onStartSendingCommand(Command command) {
        L.d(TAG, "Start sending command: " + command.getCommand());
    }

    @Override
    public void onFinishSendingCommand(Command command) {
        L.d(TAG, "Finish sending command: " + command.getCommand());
        showDialog("Success!", "Command \"" + command.getCommand() + "\" has been sent.");
    }

    @Override
    public void onFailSendingCommand(Command command) {
        L.d(TAG, "Fail sending command: " + command.getCommand() + "\n");
        showErrorDialog("Failed to send command: " + command.getCommand());
    }

    @Override
    public void queryParameter() {
        L.d(TAG, "queryParameter()");
        final ParameterDialog parameterDialog = new ParameterDialog();
        parameterDialog.show(getSupportFragmentManager(), ParameterDialog.TAG);
    }

    @Override
    public void onFinishEditingParameter(String name, String value) {
        L.d(TAG, "onFinishEditingParameter()");
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
            deviceSendCommandFragment.addParameter(name, value);
        }
    }

    @Override
    public void onBackPressed() {
        Intent networkDevicesActivity = new Intent(this, NetworkDevicesActivity.class);
        networkDevicesActivity.putExtra(NetworkDevicesActivity.EXTRA_NETWORK, device.getNetwork());
        startActivity(networkDevicesActivity);
        finish();
    }
}
