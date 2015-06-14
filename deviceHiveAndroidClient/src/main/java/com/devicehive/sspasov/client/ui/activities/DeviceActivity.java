package com.devicehive.sspasov.client.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Toast;

import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.EquipmentData;
import com.dataart.android.devicehive.EquipmentState;
import com.dataart.android.devicehive.Notification;
import com.dataart.android.devicehive.client.commands.GetDeviceClassEquipmentCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceEquipmentStateCommand;
import com.devicehive.sspasov.client.R;
import com.devicehive.sspasov.client.SampleClientApplication;
import com.devicehive.sspasov.client.adapters.SimplePagerAdapter;
import com.devicehive.sspasov.client.objects.SampleDeviceClient;
import com.devicehive.sspasov.client.ui.dialogs.ParameterDialog;
import com.devicehive.sspasov.client.ui.fragmetns.DeviceInformationFragment;
import com.devicehive.sspasov.client.ui.fragmetns.DeviceNotificationsFragment;
import com.devicehive.sspasov.client.ui.fragmetns.DeviceSendCommandFragment;
import com.devicehive.sspasov.client.ui.fragmetns.EquipmentListFragment;
import com.devicehive.sspasov.client.utils.DeviceNotificationManager;
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
    public static final String EXTRA_DEVICE = "extra_device";

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private SampleClientApplication app;
    private DeviceData device;
    private String deviceId;

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
        deviceId = device.getId();

        setupFragments();

        setupToolbar();

        setupViewPager();

        setupSlidingTabLayout();
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
    private void setupFragments() {
        deviceInformationFragment = DeviceInformationFragment.newInstance();
        deviceInformationFragment.setDeviceData(device);
        deviceInformationFragment.setContext(this);

        equipmentListFragment = EquipmentListFragment.newInstance();

        deviceNotificationsFragment = DeviceNotificationsFragment.newInstance();
        receivedNotifications = DeviceNotificationManager.getNotifications(deviceId);
        deviceNotificationsFragment.setNotifications(receivedNotifications);

        deviceSendCommandFragment = DeviceSendCommandFragment.newInstance();
        deviceSendCommandFragment.setParameterProvider(this);
        deviceSendCommandFragment.setDeviceStatus(device.getStatus());
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(device.getName());
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
    }

    private void setupViewPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SimplePagerAdapter(this, getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);
    }

    private void setupSlidingTabLayout() {
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
    public void sendCommand(Command command) {
        L.d(TAG, "sendCommand()");
        deviceClient.sendCommand(command);
    }

    @Override
    public void onReceivesNotification(Notification notification) {
        L.d(TAG, "onReceivesNotification()");

        DeviceNotificationManager.putNotification(deviceId, notification);

        receivedNotifications = DeviceNotificationManager.getNotifications(deviceId);
        deviceNotificationsFragment.setNotifications(receivedNotifications);
        Toast.makeText(this, "Received notification from: " + deviceId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void startRequest() {
        L.d(TAG, "startEquipmentRequest()");
        startCommand(
                new GetDeviceClassEquipmentCommand(
                        deviceClient.getDevice().getDeviceClass().getId()));
    }

    @Override
    protected void getRequestResult(int tagId, Bundle resultData) {
        if (tagId == TAG_GET_DEVICE) {
            final DeviceData deviceData = GetDeviceCommand.getDevice(resultData);
            deviceInformationFragment.setDeviceData(deviceData);
        } else if (tagId == TAG_GET_EQUIPMENT) {
            this.equipment = GetDeviceClassEquipmentCommand.getEquipment(resultData);
            deviceSendCommandFragment.setEquipment(equipment);
            startCommand(new GetDeviceEquipmentStateCommand(deviceClient.getDevice().getId()));
        } else if (tagId == TAG_GET_EQUIPMENT_STATE) {
            this.equipmentState = GetDeviceEquipmentStateCommand.getEquipmentState(resultData);
            equipmentListFragment.setEquipment(equipment, equipmentState);
            if (!deviceClient.isReceivingNotifications()) {
                deviceClient.startReceivingNotifications();
            }
        }
    }

    @Override
    public void onStartSendingCommand(Command command) {
        L.d(TAG, "Start sending command: " + command.getCommand());
        Toast.makeText(this, "Sent command \"" + command.getCommand() + "\"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinishSendingCommand(Command command) {
        L.d(TAG, "Finish sending command: " + command.getCommand());
    }

    @Override
    public void onFailSendingCommand(Command command) {
        L.d(TAG, "Fail sending command: " + command.getCommand() + "\n");
        Toast.makeText(this, "Failed sending command \"" + command.getCommand() + "\"", Toast.LENGTH_SHORT).show();
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
