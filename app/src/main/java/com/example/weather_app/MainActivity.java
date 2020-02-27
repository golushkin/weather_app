package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
