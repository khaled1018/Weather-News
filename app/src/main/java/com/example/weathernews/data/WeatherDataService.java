package com.example.weathernews.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.weathernews.R;
import com.example.weathernews.network.MySingleton;

import org.json.JSONObject;

public class WeatherDataService {
    private final Context context;

    public WeatherDataService(Context context) {
        this.context = context;
    }

    public String getApiKey() {
        return context != null? context.getResources().getString(R.string.API_KEY): "";
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(JSONObject response);
    }

    private void fetchWeatherData(String url, VolleyResponseListener volleyResponseListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        volleyResponseListener::onResponse,
                        error -> volleyResponseListener.onError("Something went wrong: " + error.getMessage()));
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void getWeatherInfo(String cityName, VolleyResponseListener volleyResponseListener) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + getApiKey() + "&q=" + cityName + "&days=1&aqi=no&alerts=no";
        fetchWeatherData(url, volleyResponseListener);
    }

    public void getWeatherInfo(double latitude, double longitude, VolleyResponseListener volleyResponseListener) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + getApiKey() + "&q=" + latitude + "," + longitude + "&days=1&aqi=no&alerts=no";
        fetchWeatherData(url, volleyResponseListener);
    }
}
