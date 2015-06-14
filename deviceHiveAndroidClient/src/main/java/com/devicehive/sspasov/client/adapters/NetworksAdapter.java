package com.devicehive.sspasov.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dataart.android.devicehive.Network;
import com.devicehive.sspasov.client.R;

import java.util.List;

/**
 * Created by toni on 10.06.15.
 */
public class NetworksAdapter extends BaseAdapter {
    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private final LayoutInflater mInflater;
    private final List<Network> mNetworksList;

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public NetworksAdapter(Context context, List<Network> networks) {
        this.mNetworksList = networks;
        this.mInflater = LayoutInflater.from(context);
    }

    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public int getCount() {
        return mNetworksList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNetworksList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_network, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.network_name_text_view);
            holder.description =
                    (TextView) convertView.findViewById(R.id.network_description_text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Network network = mNetworksList.get(position);
        holder.name.setText(network.getName());
        holder.description.setText(network.getDescription());
        return convertView;
    }

    // ---------------------------------------------------------------------------------------------
    // Inner classes
    // ---------------------------------------------------------------------------------------------
    private class ViewHolder {
        TextView name;
        TextView description;
    }
}