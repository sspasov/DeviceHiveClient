package com.devicehive.sspasov.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.devicehive.sspasov.client.R;
import com.devicehive.sspasov.client.config.ClientConfig;
import com.devicehive.sspasov.client.config.ClientPreferences;
import com.devicehive.sspasov.client.utils.APIValidator;
import com.devicehive.sspasov.client.utils.L;
import com.github.clans.fab.FloatingActionButton;

public class StartupConfigurationActivity extends Activity implements View.OnClickListener {

    private static final String TAG = StartupConfigurationActivity.class.getSimpleName();

    public static final String API = "api";

    private EditText etApiEndpoint;
    private FloatingActionButton btnContinue;

    private ClientPreferences prefs;

    private boolean isEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_configuration);
        L.d(TAG, "onCreate()");

        prefs = new ClientPreferences(this);

        setupToolbar();

        setupViews();

        //TODO: DEBUG ONLY
        if (L.isUsingDebugData()) {
            etApiEndpoint.setText("http://nn8170.pg.devicehive.com/api");
        }
    }

    private void setupViews() {
        etApiEndpoint = (EditText) findViewById(R.id.et_startup_api_endpoint);

        btnContinue = (FloatingActionButton) findViewById(R.id.btn_startup_continue);
        btnContinue.setOnClickListener(this);


        if (getIntent() != null && getIntent().hasExtra(API)) {
            etApiEndpoint.setText(getIntent().getStringExtra(API));
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_startup_activity);
        toolbar.setTitle(getString(R.string.title_activity_startup_configuration));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    @Override
    public void onClick(View v) {
        L.d(TAG, "onClick()");
        etApiEndpoint.setError(null);

        isEmpty = false;

        if (etApiEndpoint.getText()
                .toString()
                .isEmpty()) {
            etApiEndpoint.setError(getString(R.string.empty_api_endpoint));
            isEmpty = true;
        }

        if (!isEmpty) {
            if (APIValidator.validate(etApiEndpoint.getText().toString())) {
                prefs.setServerUrlSync(etApiEndpoint.getText().toString());
                ClientConfig.API_ENDPOINT = prefs.getServerUrl();

                Intent loginActivity = new Intent(this, LoginActivity.class);
                startActivity(loginActivity);
                finish();
            } else {
                etApiEndpoint.setError(getString(R.string.invalid_url));
            }
        }
    }
}
