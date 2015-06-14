package com.devicehive.sspasov.client.ui.fragmetns;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dataart.android.devicehive.Command;
import com.dataart.android.devicehive.EquipmentData;
import com.devicehive.sspasov.client.R;
import com.devicehive.sspasov.client.adapters.ParametersAdapter;
import com.devicehive.sspasov.client.objects.Parameter;
import com.devicehive.sspasov.client.utils.L;
import com.github.clans.fab.FloatingActionButton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DeviceSendCommandFragment extends Fragment {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = DeviceSendCommandFragment.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private EditText etCommandName;
    private Spinner spEquipment;
    private ListView lvParameters;
    private FloatingActionButton btnSendCommand;

    private CommandSender commandSender;
    private ParameterProvider parameterProvider;
    private ParametersAdapter parametersAdapter;
    private List<EquipmentData> equipment;
    private List<Parameter> parameters = new LinkedList<Parameter>();
    private String mDeviceStatus;

    private static DeviceSendCommandFragment mInstance;

    // ---------------------------------------------------------------------------------------------
    // Interfaces
    // ---------------------------------------------------------------------------------------------
    public interface CommandSender {
        void sendCommand(Command command);
    }

    public interface ParameterProvider {
        void queryParameter();
    }

    // ---------------------------------------------------------------------------------------------
    // Fragment life cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        L.d(TAG, "onAttach()");
        commandSender = (CommandSender) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        L.d(TAG, "onResume()");
        setupEquipmentSpinner(equipment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        L.d(TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_send_command, container, false);

        etCommandName = (EditText) rootView.findViewById(R.id.et_command_name);

        spEquipment = (Spinner) rootView.findViewById(R.id.equipment_spinner);
        spEquipment.setPrompt("Select equipment");
        setupEquipmentSpinner(equipment);

        lvParameters = (ListView) rootView.findViewById(R.id.lv_parameters);
        addParameter("Add extra parameter", "");
        lvParameters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (parameterProvider != null) {
                        parameterProvider.queryParameter();
                    }
                }
            }
        });
        lvParameters.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    parametersAdapter.removeParameter(position);
                    parametersAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        btnSendCommand = (FloatingActionButton) rootView.findViewById(R.id.send_command_button);
        btnSendCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDeviceStatus.contains("Offline")) {
                    sendCommand();
                    clearView();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Warning !!! Device is offline.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        L.d(TAG, "onDestroyView()");
        btnSendCommand = null;
        etCommandName = null;
        spEquipment = null;
        super.onDestroyView();
    }

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public static DeviceSendCommandFragment newInstance() {
        L.d(TAG, "newInstance()");
        mInstance = new DeviceSendCommandFragment();
        return mInstance;
    }

    public static DeviceSendCommandFragment getInstance() {
        L.d(TAG, "getInstance()");
        if (mInstance == null) {
            mInstance = new DeviceSendCommandFragment();
        }
        return mInstance;
    }

    public void setCommandSender(CommandSender commandSender) {
        L.d(TAG, "setCommandSender()");
        this.commandSender = commandSender;
    }

    public void setParameterProvider(ParameterProvider parameterProvider) {
        L.d(TAG, "setParameterProvider()");
        this.parameterProvider = parameterProvider;
    }

    public void setEquipment(List<EquipmentData> equipment) {
        L.d(TAG, "setEquipment()");
        this.equipment = equipment;
        setupEquipmentSpinner(equipment);
    }

    public void setDeviceStatus(String deviceStatus) {
        L.d(TAG, "setDeviceStatus()");
        this.mDeviceStatus = deviceStatus;
    }

    public void addParameter(String name, String value) {
        L.d(TAG, "addParameter()");
        this.parameters.add(new Parameter(name, value));
        setupParameters(this.parameters);
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private void setupEquipmentSpinner(List<EquipmentData> equipment) {
        L.d(TAG, "setupEquipmentSpinner()");
        if (equipment != null && spEquipment != null) {
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item,
                            getEquipmentItems(equipment));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spEquipment.setAdapter(adapter);
        }
    }

    private List<String> getEquipmentItems(List<EquipmentData> equipment) {
        L.d(TAG, "getEquipmentItems()");
        final List<String> equipmentNames = new LinkedList<String>();
        equipmentNames.add("None");
        for (EquipmentData eq : equipment) {
            equipmentNames.add(eq.getName());
        }
        return equipmentNames;
    }

    private void setupParameters(List<Parameter> parameters) {
        L.d(TAG, "setupParameters()");
        parametersAdapter = new ParametersAdapter(getActivity(), parameters);
        parametersAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setupParameters(DeviceSendCommandFragment.this.parameters);
            }
        });
        lvParameters.setAdapter(parametersAdapter);
    }

    private void sendCommand() {
        L.d(TAG, "sendCommand()");
        String command = etCommandName.getText().toString();
        if (TextUtils.isEmpty(command)) {
            command = "TestCommandAndroidFramework";
        }

        HashMap<String, Object> parameters = paramsAsMap(this.parameters);

        int selectedItemPosition = spEquipment.getSelectedItemPosition();
        if (selectedItemPosition != 0) {
            final EquipmentData selectedEquipment = equipment.get(selectedItemPosition - 1);
            parameters.put("equipment", selectedEquipment.getCode());
        }

        if (commandSender != null) {
            commandSender.sendCommand(new Command(command, parameters));
        }
    }

    private static HashMap<String, Object> paramsAsMap(List<Parameter> params) {
        L.d(TAG, "paramsAsMap()");
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        for (int i = 1; i < params.size(); i++) {
            paramsMap.put(params.get(i).name, params.get(i).value);
        }
        return paramsMap;
    }

    private void clearView() {
        etCommandName.setText("");
        spEquipment.refreshDrawableState();
        for (int i = 1; i < parametersAdapter.getCount(); i++) {
            parametersAdapter.removeParameter(i);
        }
        spEquipment.setSelection(0, true);
        lvParameters.setAdapter(parametersAdapter);
    }

    // ---------------------------------------------------------------------------------------------
    // Protected methods
    // ---------------------------------------------------------------------------------------------


    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
}
