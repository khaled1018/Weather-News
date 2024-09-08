package com.example.weathernews.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.BuildConfig;
import com.bumptech.glide.Glide;
import com.example.weathernews.R;
import com.example.weathernews.data.WeatherDataService;
import com.example.weathernews.model.CurrentWeather;
import com.example.weathernews.model.HourlyWeather;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextInputEditText et_cityInput;
    private TextView tv_cityName, tv_degree, tv_condition, tv_humidity, tv_uvIndex, tv_wind;
    private ImageView weatherIcon;
    private ArrayList<HourlyWeather> hourlyWeatherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.weather), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
    }

    private void setupUI() {
        et_cityInput = findViewById(R.id.et_cityInput);
        TextInputLayout textInputLayout = findViewById(R.id.cityInput);
        tv_humidity = findViewById(R.id.tv_humidityValue);
        tv_cityName = findViewById(R.id.cityName);
        tv_degree = findViewById(R.id.tv_degree);
        tv_condition = findViewById(R.id.tv_weatherCondition);
        tv_uvIndex = findViewById(R.id.tv_uvIndexValue);
        tv_wind = findViewById(R.id.tv_windValue);
        weatherIcon = findViewById(R.id.weatherIcon);

        textInputLayout.setEndIconOnClickListener(v -> {
            if (isInternetUnAvailable(WeatherActivity.this)) {
                Toast.makeText(WeatherActivity.this, "Not Connected\nCheck your internet connection", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(et_cityInput.getText())) {
                Toast.makeText(WeatherActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
            } else {
                fetchCityWeatherInfo(et_cityInput.getText().toString());
            }
        });
    }

    private void updateHourlyWeatherRv(ArrayList<HourlyWeather> hourlyWeatherList) {

        HourlyWeatherRvAdapter hourlyWeatherRvAdapter = new HourlyWeatherRvAdapter(this, hourlyWeatherList);
        RecyclerView rv_hourlyWeather = findViewById(R.id.rv_hourlyWeather);
        rv_hourlyWeather.setAdapter(hourlyWeatherRvAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_hourlyWeather.setLayoutManager(layoutManager);
        rv_hourlyWeather.setHasFixedSize(true);
    }

    private boolean isLocationUnEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showLocationServiceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Location Service Required")
                .setMessage("Turn on the Location service to obtain weather information for your current location.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    // Open the Location Settings
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the case where the user doesn't want to enable location services
                    Toast.makeText(WeatherActivity.this, "Location services are required to get weather information.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    // Check if the user connect to the internet
    public boolean isInternetUnAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities == null || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return true;
    }


    private void fetchCityWeatherInfo(String cityName) {
        WeatherDataService weatherDataService = new WeatherDataService(this);
        weatherDataService.getWeatherInfo(cityName, new WeatherDataService.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(getBaseContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                handleWeatherResponse(response);
            }
        });
    }

    private void fetchCityWeatherInfo(double latitude, double longitude) {
        WeatherDataService weatherDataService = new WeatherDataService(this);
        weatherDataService.getWeatherInfo(latitude, longitude, new WeatherDataService.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                handleWeatherResponse(response);
            }
        });
    }

    private void handleWeatherResponse(JSONObject response) {
        hourlyWeatherList.clear();
        try {
            CurrentWeather currentWeather = parseCurrentWeather(response);
            setCurrentWeatherDataToViews(currentWeather);

            hourlyWeatherList = parseHourlyWeather(response);
            updateHourlyWeatherRv(hourlyWeatherList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private CurrentWeather parseCurrentWeather(JSONObject response) throws JSONException {
        JSONObject locationObject = response.getJSONObject("location");
        String cityName = locationObject.getString("name");

        JSONObject currentObject = response.getJSONObject("current");
        int temperature = currentObject.getInt("temp_c");
        int isDay = currentObject.getInt("is_day");
        JSONObject conditionObject = currentObject.getJSONObject("condition");
        String condition = conditionObject.getString("text");
        String icon = conditionObject.getString("icon");

        int humidity = currentObject.getInt("humidity");
        int uv = currentObject.getInt("uv");
        int windSpeed = currentObject.getInt("wind_mph");
        setBackgroundWeather(isDay);

        return new CurrentWeather(cityName, temperature, condition, windSpeed, uv, humidity, icon);
    }

    private ArrayList<HourlyWeather> parseHourlyWeather(JSONObject response) throws JSONException {

        JSONObject forecastObject = response.getJSONObject("forecast");
        JSONArray hourArray = forecastObject.getJSONArray("forecastday")
                .getJSONObject(0).getJSONArray("hour");
        for (int i = 0; i < hourArray.length(); i++) {
            JSONObject hourObject = hourArray.getJSONObject(i);
            String time = hourObject.getString("time");
            String weatherTimeForecast = getTimeFromDateTime(time);
            if (isTimeBeforeCurrent(weatherTimeForecast, getTimeFromDateTime(response.getJSONObject("location").getString("localtime")))) {
                continue;
            }
            int hourlyTemp = hourObject.getInt("temp_c");
            String hourlyIcon = hourObject.getJSONObject("condition").getString("icon");
            int hourlyWindSpeed = hourObject.getInt("wind_mph");

            HourlyWeather hourlyWeather = new HourlyWeather(weatherTimeForecast, hourlyTemp, hourlyIcon, hourlyWindSpeed);
            hourlyWeatherList.add(hourlyWeather);
        }

        return hourlyWeatherList;
    }

    private void setBackgroundWeather(int isDay) {
        ConstraintLayout weatherLayout = findViewById(R.id.weather);
        if (isDay == 1) {
            weatherLayout.setBackgroundResource(R.drawable.daylight_bg);
        } else {
            weatherLayout.setBackgroundResource(R.drawable.night_bg);
        }
    }

    private String getTimeFromDateTime(String datetime) {
        if (datetime == null || datetime.isEmpty()) {
            return "";
        }
        try {
            String inputPattern = "yyyy-MM-dd HH:mm";
            String outputPattern = "HH:mm";

            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

            Date date = inputFormat.parse(datetime);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean isTimeBeforeCurrent(String weatherTimeForecast, String cityLocalTime) {
        if (weatherTimeForecast == null || cityLocalTime == null ||
                weatherTimeForecast.isEmpty() || cityLocalTime.isEmpty()) {
            return true;
        }
        try {
            String timePattern = "HH:mm";
            SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern, Locale.getDefault());

            Date providedTime = timeFormat.parse(weatherTimeForecast);
            Date currentTimeParsed = timeFormat.parse(cityLocalTime);

            return providedTime == null || !providedTime.after(currentTimeParsed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void setCurrentWeatherDataToViews(CurrentWeather currentWeather) {

        if (currentWeather == null) {
            return;
        }
        tv_cityName.setText(currentWeather.getCityName());
        tv_degree.setText(currentWeather.getTemp_c());
        tv_condition.setText(currentWeather.getTextCondition());
        Glide.with(WeatherActivity.this).load(currentWeather.getIcon()).into(weatherIcon);
        tv_humidity.setText(currentWeather.getHumidity());
        tv_wind.setText(currentWeather.getWindSpeed_mph());
        tv_uvIndex.setText(currentWeather.getUv());
    }

    private void requestLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    // Handling location update
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    fetchCityWeatherInfo(latitude, longitude);
                }
                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    showLocationServiceDialog();
                }
            });
        }
    }

    private void getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                fetchCityWeatherInfo(latitude, longitude);
            } else {
                // Request location updates
                requestLocationUpdates();
            }
        }
    }

    private boolean isFirstTimePermissionGranted() {
        SharedPreferences sharedPreferences = getSharedPreferences("WeatherAppPreferences", Context.MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTimePermissionGranted", true);

        if (isFirstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstTimePermissionGranted", false);
            editor.apply();
        }

        return isFirstTime;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                if (isFirstTimePermissionGranted()) {
                    // Check if location services are enabled
                    if (isLocationUnEnabled()) {
                        showLocationServiceDialog();
                    } else {
                        // Get the last known location
                        getLastKnownLocation();
                    }
                }
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission is required to get weather information.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check and request location permissions if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return; // Exit if permissions are not granted yet
        }

        // Check if location services are enabled
        if (isLocationUnEnabled()) {
            showLocationServiceDialog();
        } else {
            // Get the last known location
            getLastKnownLocation();
        }
    }
}