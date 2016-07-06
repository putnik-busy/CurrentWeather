package com.example.sergey.currentweather.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.example.sergey.currentweather.AddListenerOnTextChange;
import com.example.sergey.currentweather.R;

public class MaterialSimpleDialog extends DialogFragment {
    private String pos;
    private String title;
    private DialogInterface.OnClickListener dialogInterfaceListener;

    public MaterialSimpleDialog() {
    }

    public void setListener(DialogInterface.OnClickListener listener) {
        this.dialogInterfaceListener = listener;
    }

    public static void show(String tag, AppCompatActivity activity, int title, int pos,
                            DialogInterface.OnClickListener listener) {
        MaterialSimpleDialog dialog = MaterialSimpleDialog.newInstance(activity, title, pos);
        dialog.setListener(listener);
        dialog.show(activity.getSupportFragmentManager(), tag);
    }

    private static MaterialSimpleDialog newInstance(AppCompatActivity activity, int title, int pos) {
        return newInstance(activity.getResources().getString(title), activity.getResources().getString(pos));
    }

    public static MaterialSimpleDialog newInstance(String title, String pos) {
        MaterialSimpleDialog frag = new MaterialSimpleDialog();
        frag.title = title;
        frag.pos = pos;
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Context dialogViewContext = getActivity();
        View dialogView = LayoutInflater.from(dialogViewContext).inflate(R.layout.simple_dialog, null);
        final TextInputEditText editText = (TextInputEditText) dialogView.findViewById(R.id.add_city_edit);
        editText.addTextChangedListener(new AddListenerOnTextChange(editText));
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setView(dialogView);
        adb.setCancelable(true);
        adb.setTitle(title);
        adb.setPositiveButton(pos, dialogInterfaceListener);
        return adb.create();
    }
}