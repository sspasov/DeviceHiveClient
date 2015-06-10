package com.devicehive.sspasov.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.devicehive.sspasov.client.R;
import com.devicehive.sspasov.client.config.ClientConfig;
import com.devicehive.sspasov.client.config.ClientPreferences;
import com.devicehive.sspasov.client.utils.L;
import com.github.clans.fab.FloatingActionButton;

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String USERNAME = "USERNAME";

    private EditText etUsername;
    private EditText etPassword;
    private FloatingActionButton btnContinue;

    private boolean isEmpty;

    private ClientPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        L.d(TAG, "onCreate()");

        prefs = new ClientPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login_activity);
        toolbar.setTitle("Login");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        if (ClientConfig.FIRST_STARTUP && (ClientConfig.API_ENDPOINT == null)) {
            Intent startupActivity = new Intent(this, StartupConfigurationActivity.class);
            startActivity(startupActivity);
            finish();
        }

        if (ClientConfig.FIRST_STARTUP) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_launcher);
        }

        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);

        if (getIntent().hasExtra(USERNAME)) {
            etUsername.setText(getIntent().getStringExtra(USERNAME));
        } else {
            etUsername.setText(ClientConfig.USERNAME);
        }

        btnContinue = (FloatingActionButton) findViewById(R.id.btn_login_continue);
        btnContinue.setOnClickListener(this);


        //TODO: remember password

        if (L.isUsingDebugData()) {
            etUsername.setText("admin");
            etPassword.setText("milena92");
        }
    }

    @Override
    public void onBackPressed() {
        L.d(TAG, "onBackPressed()");
        if (ClientConfig.FIRST_STARTUP && (ClientConfig.API_ENDPOINT != null)) {
            Intent startupConfigurationActivity = new Intent(this, StartupConfigurationActivity.class);
            startupConfigurationActivity.putExtra(StartupConfigurationActivity.API, ClientConfig.API_ENDPOINT);
            startActivity(startupConfigurationActivity);
            finish();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        L.d(TAG, "onClick()");
        etUsername.setError(null);
        etPassword.setError(null);

        isEmpty = false;

        if (TextUtils.isEmpty(etUsername.getText())) {
            etUsername.setError("Required");
            isEmpty = true;
        }
        if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError("Required");
            isEmpty = true;
        }

        if (!isEmpty) {
            prefs.setIsFirstStartup(false);
            ClientConfig.FIRST_STARTUP = prefs.isFirstStartup();

            prefs.setCredentialsSync(
                    etUsername.getText().toString(),
                    etPassword.getText().toString());

            ClientConfig.PASSWORD = etPassword.getText().toString();

            Intent networksActivity = new Intent(this, NetworksActivity.class);
            startActivity(networksActivity);
            finish();
        }
    }
}
