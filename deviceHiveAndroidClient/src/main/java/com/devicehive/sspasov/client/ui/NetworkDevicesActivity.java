package com.devicehive.sspasov.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dataart.android.devicehive.DeviceData;
import com.dataart.android.devicehive.Network;
import com.dataart.android.devicehive.client.commands.DeviceClientCommand;
import com.dataart.android.devicehive.client.commands.GetNetworkDevicesCommand;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.devicehive.sspasov.client.R;
import com.devicehive.sspasov.client.adapters.NetworkDevicesAdapter;
import com.devicehive.sspasov.client.utils.L;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetworkDevicesActivity extends BaseActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = NetworkDevicesActivity.class.getSimpleName();

    private static final int TAG_GET_NETWORK_DEVICES = getTagId(GetNetworkDevicesCommand.class);
    public static final String EXTRA_NETWORK = "extra_network";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView networkDevicesListView;
    private Network network;

    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_devices);
        L.d(TAG, "onCreate()");

        if (getIntent().hasExtra(EXTRA_NETWORK)) {
            network = getIntent().getExtras().getParcelable(EXTRA_NETWORK);
        } else {
            throw new IllegalArgumentException("Network extra should be provided");
        }

        setupToolbar();

        setupViews();
    }

    private void setupViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar_network_devices_activity);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.accent),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_activity_network_devices);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.pink,
                R.color.violet,
                R.color.violet_dark);
        swipeRefreshLayout.setOnRefreshListener(this);

        networkDevicesListView = (ListView) findViewById(R.id.lv_network_devices);
        networkDevicesListView.setOnItemClickListener(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_network_devices_activity);
        toolbar.setTitle(network.getName());
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent networksActivity = new Intent(this, NetworksActivity.class);
        startActivity(networksActivity);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.d(TAG, "onResume()");
        Log.d(TAG, "Starting Fetch Network devices request");
        progressBar.setVisibility(View.VISIBLE);
        networkDevicesListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startNetworkDevicesRequest();
            }
        }, 10);
    }

    private void startNetworkDevicesRequest() {
        L.d(TAG, "startNetworkDevicesRequest()");
        startCommand(new GetNetworkDevicesCommand(network.getId()));
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
                break;
            case DeviceHiveResultReceiver.MSG_STATUS_FAILURE:
                int statusCode = DeviceClientCommand.getStatusCode(resultData);
                L.e(TAG, "Failed to execute network command. Status code: " + statusCode);
                break;
            case DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE:
                if (tagId == TAG_GET_NETWORK_DEVICES) {
                    final List<DeviceData> devices =
                            GetNetworkDevicesCommand.getNetworkDevices(resultData);
                    L.d(TAG, "Fetched devices: " + devices);
                    if (devices != null) {
                        Collections.sort(devices, new Comparator<DeviceData>() {
                            @Override
                            public int compare(DeviceData lhs, DeviceData rhs) {
                                return lhs.getName()
                                        .compareToIgnoreCase(rhs.getName());
                            }
                        });
                        networkDevicesListView.setAdapter(new NetworkDevicesAdapter(this, devices));
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NetworkDevicesAdapter adapter = (NetworkDevicesAdapter) parent.getAdapter();
        final DeviceData device = (DeviceData) adapter.getItem(position);

        Intent deviceActivity = new Intent(getApplicationContext(), DeviceActivity.class);
        deviceActivity.putExtra(DeviceActivity.EXTRA_DEVICE, device);
        startActivity(deviceActivity);
        finish();
    }

    @Override
    public void onRefresh() {
        //networkDevicesListView.setAdapter(null);
        networkDevicesListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startNetworkDevicesRequest();
            }
        }, 10);

    }
}
