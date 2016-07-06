package com.example.sergey.currentweather;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.sergey.currentweather.app.MyApplication;

public class AddListenerOnTextChange implements TextWatcher {
    View view;

    public AddListenerOnTextChange(View view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        switch (view.getId()) {
            case R.id.add_city_edit:
                MyApplication.getInstance().setCityAdd(s.toString());
                break;
        }
    }
}
