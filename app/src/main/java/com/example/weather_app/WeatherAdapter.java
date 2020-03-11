package com.example.weather_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private ArrayList<WeatherItem> mexampleItems;

    public static class WeatherViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageViewl;
        public TextView date;
        public TextView temp;
        public TextView wind;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewl = itemView.findViewById(R.id.img_box);
            date = itemView.findViewById(R.id.date);
            temp = itemView.findViewById(R.id.temp);
            wind = itemView.findViewById(R.id.wind);
        }
    }

    public WeatherAdapter(ArrayList<WeatherItem> exampleItems){
        mexampleItems = exampleItems;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        WeatherViewHolder evh = new WeatherViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherItem currentItem = mexampleItems.get(position);

        String temperature = "Температура: "+currentItem.getTemperature()+"°\nОщущается как: "
                + currentItem.getFeels_like()+"°";

        String[] date_arr = currentItem.getDate().split(" ");
        String[] time_arr = date_arr[1].split(":");
        date_arr = date_arr[0].split("-");

        String date = ""+date_arr[2]+"."+date_arr[1]+ " - "+time_arr[0]+":"+time_arr[1];
        String wind = "Ветер: "+currentItem.getWind()+" м/с";

        holder.mImageViewl.setImageResource(currentItem.getImage_source());
        holder.date.setText(date);
        holder.temp.setText(temperature);
        holder.wind.setText(wind);
    }

    @Override
    public int getItemCount() {
        return mexampleItems.size();
    }
}
