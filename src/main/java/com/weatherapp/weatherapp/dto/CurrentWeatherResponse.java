package com.weatherapp.weatherapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Maps the JSON response from OpenWeatherMap's
 * "Current Weather Data" API:
 * https://api.openweathermap.org/data/2.5/weather
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeatherResponse {

    private String name;          // City name
    private Main main;
    private List<WeatherInfo> weather;
    private Wind wind;
    private Sys sys;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private double temp;
        private double feels_like;
        private double temp_min;
        private double temp_max;
        private int humidity;
        private int pressure;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherInfo {
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private double speed;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        private String country;
    }
}
