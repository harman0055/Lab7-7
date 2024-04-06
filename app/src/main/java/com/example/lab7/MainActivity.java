package com.example.lab7;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lab7.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    protected String cityName;
    RequestQueue queue = null;
    final protected String API_KEY = "c39aab96c7879a7449a561c08c7f7202";
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Volley request queue
        queue = Volley.newRequestQueue(this);

        // Set OnClickListener for the button
        binding.Forecastbutton.setOnClickListener(click -> {
            // Get the city name from EditText
            cityName = binding.citytext.getText().toString();

            try {
                // Encode city name for URL
                String encodedCityName = URLEncoder.encode(cityName, "UTF-8");
                String stringURL = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=" + API_KEY + "&units=metric";

                Log.d("MainActivity", "Request URL: " + stringURL);

                // Create JsonObjectRequest for weather data
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringURL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    // Parse temperature data from response
                                    JSONObject mainObject = response.getJSONObject("main");
                                    double current = mainObject.getDouble("temp");
                                    double min = mainObject.getDouble("temp_min");
                                    double max = mainObject.getDouble("temp_max");
                                    int humidity = mainObject.getInt("humidity");

                                    // Parse weather icon URL
                                    JSONArray weatherArray = response.getJSONArray("weather");
                                    if (weatherArray.length() > 0) {
                                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                                        String iconCode = weatherObject.getString("icon");
                                        String iconUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";

                                        // Initialize ImageLoader
                                        imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
                                            @Override
                                            public Bitmap getBitmap(String url) {
                                                return null;
                                            }

                                            @Override
                                            public void putBitmap(String url, Bitmap bitmap) {
                                                // No need to implement in this example
                                            }
                                        });

                                        // Load weather icon
                                        ImageLoader.ImageListener listener = ImageLoader.getImageListener(binding.icon, 0, 0);
                                        imageLoader.get(iconUrl, listener);

                                        // Update UI with temperature information
                                        runOnUiThread(() -> {
                                            binding.temp.setText("The current temperature is: " + current);
                                            binding.temp.setVisibility(View.VISIBLE);
                                            binding.mintemp.setText("The min temperature is: " + min);
                                            binding.mintemp.setVisibility(View.VISIBLE);
                                            binding.maxtemp.setText("The max temperature is: " + max);
                                            binding.maxtemp.setVisibility(View.VISIBLE);
                                            binding.humidity.setText("The humidity temperature is: " + humidity);
                                            binding.humidity.setVisibility(View.VISIBLE);
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MainActivity", "Error: " + error.toString());
                    }
                });

                // Add request to the queue
                queue.add(request);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }
}
