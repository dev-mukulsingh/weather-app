package com.weatherapp.weatherapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Maps the JSON response from OpenWeatherMap's
 * "5 Day / 3 Hour Forecast" API:
 * https://api.openweathermap.org/data/2.5/forecast
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponse {

    private String cod;
    private City city;
    private List<ForecastItem> list;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        private String name;
        private String country;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastItem {
        private String dt_txt;          // e.g. "2026-07-06 12:00:00"
        private Main main;
        private List<WeatherInfo> weather;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private double temp;
        private double temp_min;
        private double temp_max;
        private int humidity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherInfo {
        private String main;
        private String description;
        private String icon;
    }
}
