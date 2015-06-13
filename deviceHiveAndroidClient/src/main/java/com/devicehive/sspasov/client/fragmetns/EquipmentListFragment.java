package com.devicehive.sspasov.client.fragmetns;

import android.app.Activity;
import android.support.v4.app.ListFragment;

import com.dataart.android.devicehive.EquipmentData;
import com.dataart.android.devicehive.EquipmentState;
import com.devicehive.sspasov.client.adapters.EquipmentAdapter;
import com.devicehive.sspasov.client.utils.L;

import java.util.List;

public class EquipmentListFragment extends ListFragment {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static String TAG = EquipmentListFragment.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private List<EquipmentData> equipment;
    private List<EquipmentState> equipmentState;
    private EquipmentAdapter equipmentAdapter;

    private static EquipmentListFragment mInstance;

    // ---------------------------------------------------------------------------------------------
    // Fragment life cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        L.d(TAG, "onAttach()");
        if (equipment != null) {
            equipmentAdapter = new EquipmentAdapter(activity, equipment, equipmentState);
            setListAdapter(equipmentAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        L.d(TAG, "onDestroyView()");
        equipment = null;
        equipmentState = null;
        equipmentAdapter = null;
    }


    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public static EquipmentListFragment newInstance() {
        L.d(TAG, "newInstance()");
        mInstance = new EquipmentListFragment();
        return mInstance;
    }

    public static EquipmentListFragment getInstance() {
        L.d(TAG, "getInstance()");
        if (mInstance == null) {
            mInstance = new EquipmentListFragment();
        }
        return mInstance;
    }

    public void setEquipment(List<EquipmentData> equipment, List<EquipmentState> equipmentState) {
        L.d(TAG, "setEquipment()");
        this.equipment = equipment;
        this.equipmentState = equipmentState;
        equipmentAdapter = new EquipmentAdapter(getActivity(), this.equipment, this.equipmentState);
        setListAdapter(equipmentAdapter);
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