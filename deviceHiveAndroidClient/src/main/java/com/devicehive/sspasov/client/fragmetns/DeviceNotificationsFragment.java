package com.devicehive.sspasov.client.fragmetns;

import android.app.Activity;
import android.support.v4.app.ListFragment;

import com.dataart.android.devicehive.Notification;
import com.devicehive.sspasov.client.adapters.NotificationsAdapter;
import com.devicehive.sspasov.client.utils.L;

import java.util.List;

public class DeviceNotificationsFragment extends ListFragment {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = DeviceNotificationsFragment.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private List<Notification> mNotifications;
    private NotificationsAdapter mNotificationsAdapter;

    private static DeviceNotificationsFragment mInstance;

    // ---------------------------------------------------------------------------------------------
    // Fragment life cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        L.d(TAG, "onAttach()");
        if (mNotifications != null) {
            mNotificationsAdapter = new NotificationsAdapter(activity, mNotifications);
            setListAdapter(mNotificationsAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        L.d(TAG, "onDestroyView()");
        mNotifications = null;
        mNotificationsAdapter = null;
    }

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public static DeviceNotificationsFragment newInstance() {
        L.d(TAG, "newInstance()");
        mInstance = new DeviceNotificationsFragment();
        return mInstance;
    }

    public static DeviceNotificationsFragment getInstance() {
        L.d(TAG, "getInstance()");
        if (mInstance == null) {
            mInstance = new DeviceNotificationsFragment();
        }
        return mInstance;
    }

    public void setNotifications(List<Notification> notifications) {
        L.d(TAG, "setNotifications()");
        this.mNotifications = notifications;
        if (getActivity() != null && mNotifications != null) {
            mNotificationsAdapter = new NotificationsAdapter(getActivity(), mNotifications);
            mNotificationsAdapter.notifyDataSetChanged();
            setListAdapter(mNotificationsAdapter);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Protected methods
    // ---------------------------------------------------------------------------------------------


    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------


}
