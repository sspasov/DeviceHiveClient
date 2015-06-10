package com.devicehive.sspasov.client.fragmetns;

import android.app.Activity;
import android.support.v4.app.ListFragment;

import com.dataart.android.devicehive.Notification;
import com.devicehive.sspasov.client.adapters.NotificationsAdapter;
import com.devicehive.sspasov.client.utils.L;

import java.util.List;

public class DeviceNotificationsFragment extends ListFragment {

    private static final String TAG = DeviceNotificationsFragment.class.getSimpleName();

    private List<Notification> mNotifications;
    private NotificationsAdapter mNotificationsAdapter;

    private static DeviceNotificationsFragment mInstance;

    public static DeviceNotificationsFragment newInstance() {
        L.d(TAG, "newInstance()");
        if (mInstance == null) {
            mInstance = new DeviceNotificationsFragment();
        }
        return mInstance;
    }

    public void setNotifications(List<Notification> notifications) {
        L.d(TAG, "setNotifications()");
        this.mNotifications = notifications;

        //TODO: crash
        mNotificationsAdapter = new NotificationsAdapter(getActivity().getApplicationContext(), mNotifications);
        mNotificationsAdapter.notifyDataSetChanged();
        setListAdapter(mNotificationsAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        L.d(TAG, "onAttach()");
        if (mNotifications != null) {
            mNotificationsAdapter = new NotificationsAdapter(activity, mNotifications);
            setListAdapter(mNotificationsAdapter);
        }
    }
}
