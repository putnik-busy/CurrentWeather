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

package com.example.sergey.currentweather.utils;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.app.MyApplication;

/**
 * Listener editText field
 */

public class AddListenerOnTextChange implements TextWatcher {
    private View mView;

    public AddListenerOnTextChange(View view) {
        this.mView = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        switch (mView.getId()) {
            case R.id.add_city_edit:
                MyApplication.getInstance().setCityAdd(s.toString());
                break;
        }
    }
}
