package com.devicehive.sspasov.client.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dataart.android.devicehive.Network;
import com.dataart.android.devicehive.client.commands.DeviceClientCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceClassEquipmentCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceCommand;
import com.dataart.android.devicehive.client.commands.GetDeviceEquipmentStateCommand;
import com.dataart.android.devicehive.client.commands.GetNetworkDevicesCommand;
import com.dataart.android.devicehive.client.commands.GetNetworksCommand;
import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.dataart.android.devicehive.network.NetworkCommand;
import com.dataart.android.devicehive.network.NetworkCommandConfig;
import com.devicehive.sspasov.client.BuildConfig;
import com.devicehive.sspasov.client.R;
import com.devicehive.sspasov.client.config.ClientConfig;
import com.devicehive.sspasov.client.objects.SampleDeviceClient;
import com.devicehive.sspasov.client.recievers.NetworkReceiver;
import com.devicehive.sspasov.client.ui.dialogs.WarningDialog;
import com.devicehive.sspasov.client.utils.L;

public class BaseActivity extends AppCompatActivity {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int SETTINGS_REQUEST_CODE = 0x01;

    protected static final int TAG_GET_NETWORKS = getTagId(GetNetworksCommand.class);
    protected static final int TAG_GET_NETWORK_DEVICES = getTagId(GetNetworkDevicesCommand.class);
    protected static final int TAG_GET_DEVICE = getTagId(GetDeviceCommand.class);
    protected static final int TAG_GET_EQUIPMENT = getTagId(GetDeviceClassEquipmentCommand.class);
    protected static final int TAG_GET_EQUIPMENT_STATE = getTagId(GetDeviceEquipmentStateCommand.class);

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    protected Network network;
    protected SampleDeviceClient deviceClient;

    private DeviceHiveResultReceiver resultReceiver = null;
    private final DeviceHiveResultReceiver.ResultListener resultListener =
            new DeviceHiveResultReceiver.ResultListener() {
                @Override
                public void onReceiveResult(int code, int tag, Bundle data) {
                    BaseActivity.this.onReceiveResult(code, tag, data);
                }
            };

    // ---------------------------------------------------------------------------------------------
    // Activity life cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (resultReceiver != null) {
            resultReceiver.detachResultListener();
            resultReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    protected void onReceiveResult(final int resultCode, final int tagId, final Bundle resultData) {
        L.d(TAG, "onReceiveResult()");
        switch (resultCode) {
            case DeviceHiveResultReceiver.MSG_EXECUTE_REQUEST:
                L.e(TAG, "MSG_EXECUTE_REQUEST");
                L.e(TAG, "resultCode: " + resultCode);
                break;

            case DeviceHiveResultReceiver.MSG_EXCEPTION:
                L.e(TAG, "MSG_EXCEPTION");
                L.e(TAG, "resultCode: " + resultCode);
                final Throwable exception = DeviceClientCommand.getThrowable(resultData);
                L.e(TAG, "Failed to execute network command ", exception);
                //retry command
                if (tagId == TAG_GET_NETWORKS) {
                    L.e(TAG, "TAG_GET_NETWORKS");
                    startCommand(new GetNetworksCommand());
                } else if (tagId == TAG_GET_NETWORK_DEVICES) {
                    L.e(TAG, "TAG_GET_NETWORK_DEVICES");
                    startCommand(new GetNetworkDevicesCommand(network.getId()));
                } else if (tagId == TAG_GET_DEVICE) {
                    L.e(TAG, "TAG_GET_DEVICE");
                    showErrorDialog("Failed to retrieve device data");
                } else if (tagId == TAG_GET_EQUIPMENT) {
                    L.e(TAG, "TAG_GET_EQUIPMENT");
                    startCommand(new GetDeviceClassEquipmentCommand(deviceClient.getDevice().getDeviceClass().getId()));
                } else if (tagId == TAG_GET_EQUIPMENT_STATE) {
                    L.e(TAG, "TAG_GET_EQUIPMENT_STATE");
                    startCommand(new GetDeviceEquipmentStateCommand(deviceClient.getDevice().getId()));
                }
                break;

            //->wrong username, password
            case DeviceHiveResultReceiver.MSG_STATUS_FAILURE:
                L.e(TAG, "MSG_STATUS_FAILURE");
                L.e(TAG, "resultCode: " + resultCode);
                int statusCode = DeviceClientCommand.getStatusCode(resultData);
                L.e(TAG, "Failed to execute network command. Status code: " + statusCode);
                if (statusCode == 404) {
                    WarningDialog warningDialog = WarningDialog.newInstance("404 Not Found", "Failed to connect to the server.");
                    warningDialog.show(getSupportFragmentManager(), WarningDialog.TAG);
                }
                if (statusCode == 401) {
                    WarningDialog warningDialog = WarningDialog.newInstance("401 Unauthorized", "Authentication error. Check username and password.");
                    warningDialog.show(getSupportFragmentManager(), WarningDialog.TAG);
                }
                break;

            case DeviceHiveResultReceiver.MSG_HANDLED_RESPONSE:
                L.e(TAG, "MSG_HANDLED_RESPONSE");
                L.e(TAG, "resultCode: " + resultCode);
                if (tagId == TAG_GET_NETWORKS) {
                    L.e(TAG, "TAG_GET_NETWORKS");
                } else if (tagId == TAG_GET_NETWORK_DEVICES) {
                    L.e(TAG, "TAG_GET_NETWORKS");
                } else if (tagId == TAG_GET_DEVICE) {
                    L.e(TAG, "TAG_GET_DEVICE");
                } else if (tagId == TAG_GET_EQUIPMENT) {
                    L.e(TAG, "TAG_GET_EQUIPMENT");
                } else if (tagId == TAG_GET_EQUIPMENT_STATE) {
                    L.e(TAG, "TAG_GET_EQUIPMENT_STATE");
                }
                getRequestResult(tagId, resultData);
                break;

            case DeviceHiveResultReceiver.MSG_COMPLETE_REQUEST:
                L.e(TAG, "MSG_COMPLETE_REQUEST");
                L.e(TAG, "resultCode: " + resultCode);
                break;
        }

    }

    // ---------------------------------------------------------------------------------------------
    // Protected methods
    // ---------------------------------------------------------------------------------------------

    protected void startRequest() {

    }

    protected final <T extends NetworkCommand> void startCommand(final T command) {
        if (NetworkReceiver.isConnected()) {
            L.d(TAG, "connection available");
            command.start(getApplicationContext(), getNetworkCommandConfig());
        } else {
            L.d(TAG, "connection NOT available");
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG).show();
        }
    }

    protected void getRequestResult(final int tagId, final Bundle resultData) {

    }

    protected DeviceHiveResultReceiver getResultReceiver() {
        if (resultReceiver == null) {
            resultReceiver = new DeviceHiveResultReceiver();
            resultReceiver.setResultListener(resultListener, true);
        }
        return resultReceiver;
    }

    protected NetworkCommandConfig getNetworkCommandConfig() {
        final NetworkCommandConfig config = new NetworkCommandConfig(
                ClientConfig.API_ENDPOINT,
                getResultReceiver(),
                BuildConfig.DEBUG);

        config.setBasicAuthorisation(ClientConfig.USERNAME, ClientConfig.PASSWORD);
        return config;
    }


    protected static int getTagId(final Class<?> tag) {
        return getTagId(tag.getName());
    }

    protected static int getTagId(final String tag) {
        return DeviceHiveResultReceiver.getIdForTag(tag);
    }

    protected void showErrorDialog(String message) {
        showDialog("Error!", message);
    }

    protected void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_REQUEST_CODE);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra(SettingsActivity.API_CHANGED)) {
                    Toast.makeText(this, "API Endpoint was changed. Please login again.", Toast.LENGTH_LONG).show();
                    Intent loginActivity = new Intent(this, LoginActivity.class);
                    loginActivity.putExtra("from", TAG);
                    startActivity(loginActivity);
                    finish();
                }
                L.d(TAG, "Changed settings!");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
