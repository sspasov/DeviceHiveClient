package com.devicehive.sspasov.client.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dataart.android.devicehive.network.DeviceHiveResultReceiver;
import com.dataart.android.devicehive.network.NetworkCommand;
import com.dataart.android.devicehive.network.NetworkCommandConfig;
import com.devicehive.sspasov.client.BuildConfig;
import com.devicehive.sspasov.client.config.ClientConfig;

public class BaseActivity extends AppCompatActivity {

    /*private final static String NAMESPACE = BaseActivity.class.getName();

    private final static String EXTRA_PARENT_ACTIVITY = NAMESPACE.concat(".EXTRA_PARENT_ACTIVITY");

    private final static String EXTRA_PARENT_ACTIVITY_EXTRAS =
        NAMESPACE.concat(".EXTRA_PARENT_ACTIVITY_EXTRAS");*/

    private DeviceHiveResultReceiver resultReceiver = null;

    private final DeviceHiveResultReceiver.ResultListener resultListener =
            new DeviceHiveResultReceiver.ResultListener() {
                @Override
                public void onReceiveResult(int code, int tag, Bundle data) {
                    BaseActivity.this.onReceiveResult(code, tag, data);
                }
            };

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

    protected final <T extends NetworkCommand> void startCommand(final T command) {
        command.start(getApplicationContext(), getNetworkCommandConfig());
    }

    protected DeviceHiveResultReceiver getResultReceiver() {
        if (resultReceiver == null) {
            resultReceiver = new DeviceHiveResultReceiver();
            resultReceiver.setResultListener(resultListener, true);
        }
        return resultReceiver;
    }

    protected NetworkCommandConfig getNetworkCommandConfig() {

        /*final ClientPreferences prefs = new ClientPreferences(this);
        String serverUrl = prefs.getServerUrl();
        if (serverUrl == null) {
            serverUrl = DeviceHiveConfig.API_ENDPOINT;
            prefs.setServerUrlSync(serverUrl);
        }*/
        final NetworkCommandConfig config =
                new NetworkCommandConfig(ClientConfig.API_ENDPOINT, getResultReceiver(), BuildConfig.DEBUG);

        config.setBasicAuthorisation(ClientConfig.USERNAME, ClientConfig.PASSWORD);
        return config;
    }

    protected void onReceiveResult(final int resultCode, final int tagId, final Bundle resultData) {

    }

    protected static final int getTagId(final Class<?> tag) {
        return getTagId(tag.getName());
    }

    protected static final int getTagId(final String tag) {
        return DeviceHiveResultReceiver.getIdForTag(tag);
    }

    /*protected final static <T extends Activity> Intent setParentActivity(final Intent intent,
        final Class<T> parentActivityClass) {
        return setParentActivity(intent, parentActivityClass, null);
    }

    protected final static <T extends Activity> Intent setParentActivity(final Intent intent,
        final Class<T> parentActivityClass, Bundle parentActivityExtras) {
        intent.putExtra(EXTRA_PARENT_ACTIVITY, parentActivityClass.getName());
        if (parentActivityExtras != null) {
            intent.putExtra(EXTRA_PARENT_ACTIVITY_EXTRAS, parentActivityExtras);
        }
        return intent;
    }*/

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

}
