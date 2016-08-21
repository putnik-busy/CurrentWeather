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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sergey.currentweather.R;
import com.example.sergey.currentweather.model.Weather;

import java.text.MessageFormat;
import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder> {

    private List<Weather> mData;
    private Context mContext;
    private CityListAdapterClickListener mOnClickListener;

    public CityListAdapter(Context context, CityListAdapterClickListener OnClickListener,
                           List<Weather> data) {
        this.mContext = context;
        this.mData = data;
        mOnClickListener = OnClickListener;
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
        holder.mLayoutView.setTag(weather.getCityID());
        holder.temperature_city.setText(MessageFormat.format("{0}℃",
                Math.round(weather.temperature.getTemp())));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name_city, temperature_city;
        private ViewGroup mLayoutView;

        public ViewHolder(View itemView) {
            super(itemView);
            name_city = (TextView) itemView.findViewById(R.id.name_city);
            temperature_city = (TextView) itemView.findViewById(R.id.temperature_city);
            mLayoutView = (ViewGroup) itemView.findViewById(R.id.ItemLayout);
            mLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.CityListItemClicked(v, (long) v.getTag());
        }
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
