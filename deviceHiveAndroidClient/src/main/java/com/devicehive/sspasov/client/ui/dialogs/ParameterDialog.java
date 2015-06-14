package com.devicehive.sspasov.client.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.devicehive.sspasov.client.R;

public class ParameterDialog extends DialogFragment {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    public static final String TAG = ParameterDialog.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private EditText etName;
    private EditText etValue;

    // ---------------------------------------------------------------------------------------------
    // Interfaces
    // ---------------------------------------------------------------------------------------------
    public interface ParameterDialogListener {
        void onFinishEditingParameter(String name, String value);
    }

    // ---------------------------------------------------------------------------------------------
    // Fragment Life Cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_parameter, null);
        etName = (EditText) view.findViewById(R.id.et_name);
        etValue = (EditText) view.findViewById(R.id.et_value);

        builder
                .setView(view)
                .setTitle("Add parameter")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ParameterDialogListener activity = (ParameterDialogListener) getActivity();
                        activity.onFinishEditingParameter(
                                etName.getText().toString(), etValue.getText()
                                        .toString());
                        ParameterDialog.this.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ParameterDialog.this.dismiss();
                    }
                });
        return builder.create();
    }
}