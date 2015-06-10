package com.devicehive.sspasov.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dataart.android.devicehive.Notification;
import com.devicehive.sspasov.client.R;

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

    public void setNotifications(List<Notification> notifications) {
        this.mNotifications = notifications;
        notifyDataSetChanged();
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
        holder.params.setText(notification.getParameters().toString());

        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView params;
    }
}