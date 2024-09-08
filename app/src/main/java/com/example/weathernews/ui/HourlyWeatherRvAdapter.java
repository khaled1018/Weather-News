package com.example.weathernews.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weathernews.model.HourlyWeather;
import com.example.weathernews.R;
import java.util.ArrayList;

public class HourlyWeatherRvAdapter extends RecyclerView.Adapter<HourlyWeatherRvAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<HourlyWeather> hourlyWeatherList;

    public HourlyWeatherRvAdapter(Context context, ArrayList<HourlyWeather> hourlyWeatherList) {
        this.context = context;
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @NonNull
    @Override
    public HourlyWeatherRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherRvAdapter.ViewHolder holder, int position) {

        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);
        holder.tv_temp.setText(hourlyWeather.getTemp_c().concat(context.getString(R.string.degree)));
        holder.tv_windSpeed.setText(hourlyWeather.getWindSpeed_mph().concat(" " + context.getString(R.string.milePerHour)));
        Glide.with(context).load(hourlyWeather.getIcon()).into(holder.iv_weatherIcon);
        holder.tv_time.setText(hourlyWeather.getTime());

    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time, tv_temp, tv_windSpeed;
        ImageView iv_weatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_time = itemView.findViewById(R.id.tv_time);
            tv_temp = itemView.findViewById(R.id.tv_temp);
            tv_windSpeed = itemView.findViewById(R.id.tv_windSpeed);
            iv_weatherIcon = itemView.findViewById(R.id.iv_weatherIcon);
        }
    }


}
