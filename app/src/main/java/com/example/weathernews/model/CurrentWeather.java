package com.example.weathernews.model;

public class CurrentWeather {
    private final String cityName;
    private final int temp_c;
    private final String textCondition;
    private final int windSpeed_mph;
    private final int uv;
    private final int humidity;
    private final String icon;

    public CurrentWeather(String cityName, int temp_c, String textCondition, int windSpeed_mph, int uv, int humidity, String icon) {
        this.cityName = cityName;
        this.temp_c = temp_c;
        this.textCondition = textCondition;
        this.windSpeed_mph = windSpeed_mph;
        this.uv = uv;
        this.humidity = humidity;
        this.icon = "https:" + icon;
    }


    public String getCityName() {
        return cityName;
    }

    public String getTemp_c() {
        return String.valueOf(temp_c);
    }

    public String getTextCondition() {
        return textCondition;
    }

    public String getWindSpeed_mph() {
        return String.valueOf(windSpeed_mph);
    }

    public String getUv() {
        return String.valueOf(uv);
    }

    public String getHumidity() {
        return String.valueOf(humidity);
    }

    public String getIcon() {
        return icon;
    }
}
