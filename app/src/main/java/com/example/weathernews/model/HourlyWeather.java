package com.example.weathernews.model;

public class HourlyWeather {

    private final String time;
    private final int temp_c;
    private final String icon;
    private final int windSpeed_mph;

    public HourlyWeather(String time, int temp_c, String icon, int windSpeed_mph) {
        this.time = time;
        this.temp_c = temp_c;
        this.icon = "https:" + icon;
        this.windSpeed_mph = windSpeed_mph;
    }

    public String getTime() {
        return time;
    }

    public String getTemp_c() {
        return String.valueOf(temp_c);
    }

    public String getIcon() {
        return icon;
    }

    public String getWindSpeed_mph() {
        return String.valueOf(windSpeed_mph);
    }
}
