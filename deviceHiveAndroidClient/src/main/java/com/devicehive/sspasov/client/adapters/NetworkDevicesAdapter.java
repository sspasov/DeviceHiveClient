package com.devicehive.sspasov.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dataart.android.devicehive.DeviceData;
import com.devicehive.sspasov.client.R;

import java.util.List;

/**
 * Created by toni on 10.06.15.
 */
public class NetworkDevicesAdapter extends BaseAdapter {
    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private final LayoutInflater mInflater;
    private final List<DeviceData> mDeviceDataList;
    private Context mContext;

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public NetworkDevicesAdapter(Context context, List<DeviceData> devices) {
        this.mDeviceDataList = devices;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public int getCount() {
        return mDeviceDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDeviceDataList.get(position);
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
        final DeviceData device = mDeviceDataList.get(position);
        holder.name.setText(device.getName());
        holder.description.setText(device.getDeviceClass()
                .getName());

        if (device.getStatus().contains("Offline")) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.red_light));
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.green_light));
        }

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
