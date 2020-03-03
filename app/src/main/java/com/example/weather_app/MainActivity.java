package com.example.weather_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView http_output;
    private ProgressBar pb;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 0;
    private double longitude = 0;

    void show_err(String err){
        Toast.makeText(this, err, Toast.LENGTH_LONG).show();
    }

    void request_to_api(){
        http_output = findViewById(R.id.test_output);

        OkHttpClient client = new OkHttpClient();

        String url = "https://reqres.in/api/users?page=2";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String my_response = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            http_output.setText(my_response);
                            JSONObject Jobject = null;
                            JSONArray Jarray = null;
                            try {
                                Jobject = new JSONObject(my_response);
                                System.out.println(Jobject.get("page"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }
                    });
                }
            }
        });
    }

    void get_permission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    void get_current_location(){
        final TextView output = (TextView) findViewById(R.id.test_output);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            output.setText("Latitude = " + latitude+ ", Longitude = "+ longitude);
                        }
                        else{
                            output.setText("Location is null");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        show_err(e.getMessage());
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        get_permission();
        get_current_location();
    }
}
