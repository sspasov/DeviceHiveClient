package com.devicehive.sspasov.client.fragmetns;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dataart.android.devicehive.DeviceData;
import com.devicehive.sspasov.client.R;
import com.devicehive.sspasov.client.utils.L;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class DeviceInformationFragment extends Fragment {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = DeviceInformationFragment.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private DeviceData mDeviceData;

    private TextView tvDeviceName;
    private TextView tvDeviceBoard;
    private TextView tvDeviceId;
    private TextView tvDeviceStatus;
    private TextView tvDeviceTimeOn;
    private TextView tvDeviceBattery;

    private TextView tvDeviceClassName;
    private TextView tvDeviceClassVersion;
    private TextView tvDeviceClassIsPermanent;

    private TextView tvDeviceNetworkName;
    private TextView tvDeviceNetworkDescription;

    private static DeviceInformationFragment mInstance;

    private Context mContext;

    // ---------------------------------------------------------------------------------------------
    // Fragment life cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onResume() {
        super.onResume();
        L.d(TAG, "onResume()");
        setupDeviceData(mDeviceData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        L.d(TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_device_info, container, false);

        tvDeviceName = (TextView) rootView.findViewById(R.id.tv_device_name);
        //tvDeviceBoard = (TextView) rootView.findViewById(R.id.tv_device_board);
        tvDeviceId = (TextView) rootView.findViewById(R.id.tv_device_id);
        tvDeviceStatus = (TextView) rootView.findViewById(R.id.tv_device_status);
        //tvDeviceTimeOn = (TextView) rootView.findViewById(R.id.tv_device_time_on);
        //tvDeviceBattery = (TextView) rootView.findViewById(R.id.tv_device_battery);

        tvDeviceClassName = (TextView) rootView.findViewById(R.id.tv_device_class_name);
        tvDeviceClassVersion = (TextView) rootView.findViewById(R.id.tv_device_class_version);
        tvDeviceClassIsPermanent =
                (TextView) rootView.findViewById(R.id.tv_device_class_is_permanent);

        tvDeviceNetworkName = (TextView) rootView.findViewById(R.id.tv_device_network_name);
        tvDeviceNetworkDescription =
                (TextView) rootView.findViewById(R.id.tv_device_network_description);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDeviceData = null;

        tvDeviceName = null;
        tvDeviceBoard = null;
        tvDeviceId = null;
        tvDeviceStatus = null;
        tvDeviceTimeOn = null;
        tvDeviceBattery = null;

        tvDeviceClassName = null;
        tvDeviceClassVersion = null;
        tvDeviceClassIsPermanent = null;

        tvDeviceNetworkName = null;
        tvDeviceNetworkDescription = null;
    }

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public static DeviceInformationFragment newInstance() {
        L.d(TAG, "newInstance()");
        mInstance = new DeviceInformationFragment();
        return mInstance;
    }

    public static DeviceInformationFragment getInstance() {
        L.d(TAG, "getInstance()");
        if (mInstance == null) {
            mInstance = new DeviceInformationFragment();
        }
        return mInstance;
    }

    public void setContext(Context context) {
        L.d(TAG, "setContext()");
        mContext = context;
    }

    public void setDeviceData(DeviceData deviceData) {
        L.d(TAG, "setDeviceData()");
        this.mDeviceData = deviceData;
        if (isAdded()) {
            setupDeviceData(deviceData);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private void setupDeviceData(DeviceData deviceData) {
        L.d(TAG, "setupDeviceData()");
        if (deviceData != null) {
            tvDeviceName.setText(deviceData.getName());
            //tvDeviceBoard.setText(Build.BOARD);
            tvDeviceId.setText(deviceData.getId());
            tvDeviceStatus.setText(deviceData.getStatus());
            //timeThread();
            //batteryThread();

            tvDeviceClassName.setText(deviceData.getDeviceClass()
                    .getName());
            tvDeviceClassVersion.setText(deviceData.getDeviceClass()
                    .getVersion());
            tvDeviceClassIsPermanent.setText("" + deviceData.getDeviceClass()
                    .isPermanent());

            tvDeviceNetworkName.setText(deviceData.getNetwork()
                    .getName());
            tvDeviceNetworkDescription.setText(deviceData.getNetwork()
                    .getDescription()
                    .isEmpty() ? "--" : deviceData.getNetwork()
                    .getDescription());
        }
    }

    private void timeThread() {
        L.d(TAG, "timeThread()");
        final Handler handler = new Handler();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long millis;
                millis = SystemClock.elapsedRealtime();
                final String hms =
                        String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                handler.post(new Runnable() {
                    public void run() {
                        tvDeviceTimeOn.setText(hms);
                    }
                });
            }
        }, 0, 1_000);
    }

    private void batteryThread() {
        L.d(TAG, "batteryThread()");
        final Handler handler = new Handler();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = mContext.registerReceiver(null,
                        new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int level = intent != null ? intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) : 0;
                int scale =
                        intent != null ? intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100) : 0;
                final int percent = (level * 100) / scale;
                handler.post(new Runnable() {
                    public void run() {
                        tvDeviceBattery.setText(String.valueOf(percent) + "%");
                    }
                });
            }
        }, 0, 60_000);
    }
    // ---------------------------------------------------------------------------------------------
    // Protected methods
    // ---------------------------------------------------------------------------------------------


    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------


}
