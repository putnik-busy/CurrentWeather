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

package com.example.sergey.currentweather.ui.adapter;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.model.Weather;
import com.example.sergey.currentweather.ui.fragment.DetailFragment;

import java.text.MessageFormat;
import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder> {

    private List<Weather> mData;
    Context context;

    public CityListAdapter(Context context, List<Weather> data) {
        this.context = context;
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Weather weather = mData.get(position);
        holder.name_city.setText(weather.location.getCity());
        holder.name_city.setTag(weather.getCityID());
        holder.temperature_city.setText(MessageFormat.format("{0}℃",
                Math.round(weather.temperature.getTemp())));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView name_city, temperature_city;

        public ViewHolder(View itemView) {
            super(itemView);
            name_city = (TextView) itemView.findViewById(R.id.name_city);
            temperature_city = (TextView) itemView.findViewById(R.id.temperature_city);
            name_city.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            read((long) v.getTag());
        }
    }

    private void read(long pos) {
        Bundle args = new Bundle();
        args.putLong("position", pos);
        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(args);
        ft.replace(R.id.container, detailFragment);
        ft.addToBackStack("detail");
        ft.commit();
    }

    public void addItem(Weather weather) {
        this.mData.add(weather);
        notifyDataSetChanged();
    }

    public void addAll(List<Weather> items) {
        final int size = this.mData.size();
        this.mData.addAll(items);
        notifyItemRangeInserted(size, items.size());
    }

    public Weather removeItem(int position) {
        final int size = this.mData.size();
        Weather weather = mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, size);
        return weather;
    }
}
