package com.example.weather_app;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Switch temp_switch;
    private LocationManager locationManager;
    private ProgressBar pb;
    private String longitude, latitude;
    private JSONObject Jobject;
    private JSONObject Jobject_current;
    private RequestQueue mqueue;
    private ArrayList<WeatherItem> weatherItems;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter mAdapter;


    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private void pb_visible(){
        if (pb.isEnabled()){
            pb.setVisibility(ProgressBar.GONE);
        }
        else{
            pb.setVisibility(ProgressBar.VISIBLE);
        }
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    public void get_updates() {
        if(!checkLocation())
            return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
        pb_visible();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 2 * 60 * 1000, 10, locationListenerGPS);
    }


    void request_to_api_five_day(){
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat="+latitude+"&lon="+longitude+"&units=metric&appid="+getString(R.string.API);
        pb_visible();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pb_visible();
                JSONArray list = null;
                JSONObject object = null;

                try {
                    list = response.getJSONArray("list");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i< list.length(); i++){
                    WeatherItem item = new WeatherItem();

                    try {
                        object = list.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    get_weather(item, object);
                    get_main(item, object);
                    get_wind(item, object);
                    get_date(item, object);
                    weatherItems.add(item);
                }
                update_weather_items();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mqueue.add(request);
    }

    void update_weather_items(){
        mAdapter = new WeatherAdapter(weatherItems);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    void get_date(WeatherItem item, JSONObject response)
    {
        try {
            String date = "";
            if (response.isNull("dt_txt")){
                date = "now-now-now now:now:now";
            }
            else {
                date = response.getString("dt_txt");
            }
            item.setDate(date);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void get_weather(WeatherItem item, JSONObject response)
    {
        try {
            double weather_code = response.getJSONArray("weather").getJSONObject(0).getDouble("id");
            String first_ch = String.valueOf(weather_code);
            switch (first_ch.charAt(0)){
                case '2':
                    item.setImage_source(R.drawable.lightning_rain);
                    break;

                case '3':
                    item.setImage_source(R.drawable.drizzle);
                    break;

                case '5':
                    item.setImage_source(R.drawable.rain);
                    break;

                case '6':
                    item.setImage_source(R.drawable.snow);
                    break;

                case '7':
                    item.setImage_source(R.drawable.fog);
                    break;

                case '8':
                    if (weather_code == 800)
                    {
                        item.setImage_source(R.drawable.sun);
                    }
                    else{
                        item.setImage_source(R.drawable.cloud_sun);
                    }
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void get_main(WeatherItem item, JSONObject response)
    {
        try {
            double temperature = response.getJSONObject("main").getDouble("temp");
            double feels_like = response.getJSONObject("main").getDouble("feels_like");
            item.setTemperature(temperature);
            item.setFeels_like(feels_like);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void get_wind(WeatherItem item, JSONObject response)
    {
        try {
            double wind = response.getJSONObject("wind").getDouble("speed");
            item.setWind(wind);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void request_current_temp(){
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&units=metric&appid="+getString(R.string.API);
        pb_visible();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pb_visible();
                WeatherItem item = new WeatherItem();
                get_weather(item, response);
                get_main(item, response);
                get_wind(item, response);
                get_date(item, response);
                weatherItems.add(item);
                update_weather_items();
                request_to_api_five_day();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mqueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar)findViewById(R.id.progressBar2);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mqueue = Volley.newRequestQueue(this);

        weatherItems = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        temp_switch = (Switch)findViewById(R.id.temp_switch);
        temp_switch.setOnCheckedChangeListener(this);

        get_updates();
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = String.valueOf(location.getLongitude());
            latitude = String.valueOf(location.getLatitude());

            pb_visible();
            request_current_temp();
            locationManager.removeUpdates(locationListenerGPS);
            Toast.makeText(MainActivity.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            temp_switch.setText("°F");

            ArrayList<WeatherItem> items = new ArrayList<>();
            
            for(WeatherItem w : weatherItems) {
                items.add(new WeatherItem(w));
            }

            for(WeatherItem w : items) {
                w.setTemperature(Math.round(w.getTemperature()*1.8)+32);
                w.setFeels_like(Math.round(w.getTemperature()*1.8)+32);
            }

            mAdapter = new WeatherAdapter(items);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mAdapter);

        }
        else {
            temp_switch.setText("°C");
            update_weather_items();
        }
    }
}
