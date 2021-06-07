package com.huawei.kritify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class SiteMapDialog extends AppCompatDialogFragment {

    private TextInputEditText siteName;
    private Spinner siteType;
    private TextView location;
    private SiteDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_site, null);
        builder.setView(view)
        .setTitle("Add a Site")
        .setNegativeButton("Cancel", (dialog, which) -> {

        })
        .setPositiveButton("Save", (dialog, which) -> {
            String name = siteName.getText().toString();
            listener.applyTexts(name);
        });

        //siteName = view.findViewById(R.id.textInputEditText);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (SiteDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SiteDialogListener");
        }
    }

    public interface SiteDialogListener {
        void applyTexts(String name);
    }
}