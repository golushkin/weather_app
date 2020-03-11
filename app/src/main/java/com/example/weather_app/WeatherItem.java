package com.example.weather_app;

public class WeatherItem {
    private String date;
    private double temperature;
    private double feels_like;
    private int image_source;
    private double wind;

    public double getWind() {
        return wind;
    }

    public void setWind(double wind) {
        this.wind = wind;
    }

    public double getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(double feels_like) {
        this.feels_like = feels_like;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getImage_source() {
        return image_source;
    }

    public void setImage_source(int image_source) {
        this.image_source = image_source;
    }

    public WeatherItem(WeatherItem w) {
        this.date = w.getDate();
        this.temperature = w.getTemperature();
        this.feels_like = w.getFeels_like();
        this.image_source = w.getImage_source();
        this.wind = w.getWind();
    }

    public WeatherItem() {
        this.date = null;
        this.temperature = 0;
        this.feels_like = 0;
        this.image_source = 0;
        this.wind = 0;
    }


}
