/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.utils.AddListenerOnTextChange;

/**
 * Simple dialog
 */

public class MaterialSimpleDialog extends DialogFragment {
    private String mPositive;
    private String mNegative;
    private String mTitle;
    private DialogInterface.OnClickListener mDialogInterfaceListener;

    public MaterialSimpleDialog() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("yes", mPositive);
        outState.putString("no", mNegative);
        outState.putString("title", mTitle);
    }

    public void setListener(DialogInterface.OnClickListener listener) {
        this.mDialogInterfaceListener = listener;
    }

    public static void show(String tag, AppCompatActivity activity, int title, int pos, int neg,
                            DialogInterface.OnClickListener listener) {
        MaterialSimpleDialog dialog = MaterialSimpleDialog.newInstance(activity, title, pos, neg);
        dialog.setListener(listener);
        dialog.show(activity.getSupportFragmentManager(), tag);
    }

    private static MaterialSimpleDialog newInstance(AppCompatActivity activity, int title
            , int pos, int neg) {
        return newInstance(activity.getResources().getString(title),
                activity.getResources().getString(pos), activity.getResources().getString(neg));
    }

    public static MaterialSimpleDialog newInstance(String title, String pos, String neg) {
        MaterialSimpleDialog frag = new MaterialSimpleDialog();
        frag.mTitle = title;
        frag.mPositive = pos;
        frag.mNegative = neg;
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        if (savedInstanceState != null) {
            mPositive = savedInstanceState.getString("yes");
            mNegative = savedInstanceState.getString("no");
            mTitle = savedInstanceState.getString("title");
        }
        Context dialogViewContext = getActivity();
        View dialogView = LayoutInflater.from(dialogViewContext)
                .inflate(R.layout.simple_dialog, null);
        final TextInputEditText editText = (TextInputEditText) dialogView
                .findViewById(R.id.add_city_edit);
        editText.addTextChangedListener(new AddListenerOnTextChange(editText));
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity(), R.style.StyledDialog);
        adb.setView(dialogView);
        adb.setCancelable(false);
        adb.setTitle(mTitle);
        adb.setPositiveButton(mPositive, mDialogInterfaceListener);
        adb.setNegativeButton(mNegative, mDialogInterfaceListener);
        return adb.create();
    }
}