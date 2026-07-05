package com.weatherapp.weatherapp.controller;

import com.weatherapp.weatherapp.model.WeatherData;
import com.weatherapp.weatherapp.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherRestController {

    private final WeatherService weatherService;

    public WeatherRestController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * GET /api/weather?city=Delhi
     * Returns current weather + 5-day forecast as JSON.
     */
    @GetMapping
    public ResponseEntity<?> getWeather(@RequestParam String city) {
        try {
            WeatherData data = weatherService.getWeatherByCity(city);
            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Simple error wrapper so the frontend gets clean JSON on failure too
    record ErrorResponse(String error) {}
}
