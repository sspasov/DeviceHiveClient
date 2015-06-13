package com.devicehive.sspasov.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dataart.android.devicehive.Notification;
import com.devicehive.sspasov.client.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by toni on 10.06.15.
 */
public class NotificationsAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<Notification> mNotifications;

    public NotificationsAdapter(Context context, List<Notification> notifications) {
        this.mNotifications = notifications;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mNotifications.size();
    }

    @Override
    public Object getItem(int position) {
        return mNotifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.notification_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_command_name);
            holder.params = (TextView) convertView.findViewById(R.id.tv_command_params);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Notification notification = mNotifications.get(position);
        holder.name.setText(notification.getId() + ". " + notification.getName());

        HashMap<String, String> params = (HashMap<String, String>) notification.getParameters();
        String[] values = new String[3];
        if (params.isEmpty()) {
            holder.params.setText("(no parameters)");
        } else if (params.containsKey("value")) {
            values[0] = params.get("value");
            holder.params.setText("value: " + values[0]);
        } else {
            values[0] = params.get("x");
            values[1] = params.get("y");
            values[2] = params.get("z");
            holder.params.setText("x: " + values[0] + "\ny: " + values[1] + "\nz: " + values[2]);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView params;
    }
}