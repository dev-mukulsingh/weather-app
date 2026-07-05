package com.weatherapp.weatherapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Final response object sent back to the client (JSON / view).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    private String city;
    private String country;
    private double currentTemp;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
    private String description;
    private String icon;
    private List<DailyForecast> forecast; // 5-day forecast
}
