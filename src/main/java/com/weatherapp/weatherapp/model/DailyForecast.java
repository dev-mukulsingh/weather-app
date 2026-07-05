package com.weatherapp.weatherapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplified, day-wise forecast model used by the frontend.
 * We build this by aggregating OpenWeatherMap's 3-hour interval data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyForecast {
    private String date;        // e.g. "2026-07-06"
    private double minTemp;
    private double maxTemp;
    private int humidity;
    private String description;
    private String icon;
}
