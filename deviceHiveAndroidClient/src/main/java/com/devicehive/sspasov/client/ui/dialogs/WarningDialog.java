package com.devicehive.sspasov.client.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by toni on 14.06.15.
 */
public class WarningDialog extends DialogFragment {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    public static final String TAG = WarningDialog.class.getSimpleName();
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private String mTitle;
    private String mMessage;

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------
    public static WarningDialog newInstance(String title, String message) {
        WarningDialog warningDialog = new WarningDialog();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(MESSAGE, message);
        warningDialog.setArguments(bundle);
        return warningDialog;
    }

    // ---------------------------------------------------------------------------------------------
    // Fragment Life Cycle
    // ---------------------------------------------------------------------------------------------
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments().containsKey(TITLE)) {
            mTitle = getArguments().getString(TITLE);
        }
        if (getArguments().containsKey(MESSAGE)) {
            mMessage = getArguments().getString(MESSAGE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        WarningDialog.this.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        WarningDialog.this.dismiss();
                    }
                });
        return builder.create();
    }
}