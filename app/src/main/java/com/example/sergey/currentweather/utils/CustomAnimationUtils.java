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


import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.sergey.currentweather.R;

public class CustomAnimationUtils {

    public static void fromRightToLeftShow(final View view, final Context context) {
        view.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                view.setVisibility(View.VISIBLE);
                Animation showAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right);
                view.startAnimation(showAnimation);
            }
        }, 300);
    }

    public static void fromLeftToRightHide(final View view, final Context context) {
        view.setVisibility(View.VISIBLE);

        Animation showAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_from_left);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(showAnimation);
    }
}
