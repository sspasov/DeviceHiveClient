package com.devicehive.sspasov.client.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dataart.android.devicehive.Network;
import com.dataart.android.devicehive.client.commands.GetNetworksCommand;
import com.devicehive.sspasov.client.R;
import com.devicehive.sspasov.client.adapters.NetworksAdapter;
import com.devicehive.sspasov.client.config.ClientConfig;
import com.devicehive.sspasov.client.utils.L;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetworksActivity extends BaseActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = NetworksActivity.class.getSimpleName();


    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView networksListView;

    private ProgressBar progressBar;

    // ---------------------------------------------------------------------------------------------
    // Activity life cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "onCreate()");
        setContentView(R.layout.activity_networks);

        setupToolbar();

        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.d(TAG, "onResume()");
        L.d(TAG, "Starting Get Networks request");
        progressBar.setVisibility(View.VISIBLE);
        networksListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startRequest();
            }
        }, 10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.d(TAG, "onStop()");
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private void setupViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar_network_activity);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.accent),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_activity_networks);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.pink,
                R.color.violet,
                R.color.violet_dark);
        swipeRefreshLayout.setOnRefreshListener(this);

        networksListView = (ListView) findViewById(R.id.lv_networks);
        networksListView.setOnItemClickListener(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_networks_activity);
        toolbar.setTitle(getString(R.string.title_activity_networks));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
    }

    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void startRequest() {
        L.d(TAG, "startNetworksRequest()");
        startCommand(new GetNetworksCommand());
    }

    @Override
    protected void getRequestResult(int tagId, Bundle resultData) {
        final List<Network> networks = GetNetworksCommand.getNetworks(resultData);
        L.d(TAG, "Fetched networks: " + networks);
        if (networks != null) {
            Collections.sort(networks, new Comparator<Network>() {
                @Override
                public int compare(Network lhs, Network rhs) {
                    return lhs.getName()
                            .compareToIgnoreCase(rhs.getName());
                }
            });
            networksListView.setAdapter(new NetworksAdapter(this, networks));
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onBackPressed() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        loginActivity.putExtra(LoginActivity.USERNAME, ClientConfig.USERNAME);
        startActivity(loginActivity);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NetworksAdapter adapter = (NetworksAdapter) parent.getAdapter();
        Network network = (Network) adapter.getItem(position);
        // start network devices activity
        Intent networkDevicesActivity = new Intent(getApplicationContext(), NetworkDevicesActivity.class);
        networkDevicesActivity.putExtra(NetworkDevicesActivity.EXTRA_NETWORK, network);
        startActivity(networkDevicesActivity);
        finish();
    }

    @Override
    public void onRefresh() {
        networksListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startRequest();
            }
        }, 10);
    }
}
