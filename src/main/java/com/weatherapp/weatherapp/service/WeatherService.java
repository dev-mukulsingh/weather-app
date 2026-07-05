package com.weatherapp.weatherapp.service;

import com.weatherapp.weatherapp.dto.CurrentWeatherResponse;
import com.weatherapp.weatherapp.dto.ForecastResponse;
import com.weatherapp.weatherapp.model.DailyForecast;
import com.weatherapp.weatherapp.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private final WebClient webClient;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.baseurl}")
    private String baseUrl;

    public WeatherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Fetches current weather + 5-day forecast for a given city
     * and combines them into a single WeatherData object.
     */
    public WeatherData getWeatherByCity(String city) {

        // 1) Current weather
        String currentUrl = String.format(
                "%s/weather?q=%s&appid=%s&units=metric",
                baseUrl, city, apiKey);

        CurrentWeatherResponse current;
        try {
            current = webClient.get()
                    .uri(currentUrl)
                    .retrieve()
                    .bodyToMono(CurrentWeatherResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException("City not found: " + city);
        } catch (WebClientResponseException.Unauthorized e) {
            throw new RuntimeException("Invalid API key. Please check application.properties");
        }

        // 2) 5-day / 3-hour forecast
        String forecastUrl = String.format(
                "%s/forecast?q=%s&appid=%s&units=metric",
                baseUrl, city, apiKey);

        ForecastResponse forecastResponse = webClient.get()
                .uri(forecastUrl)
                .retrieve()
                .bodyToMono(ForecastResponse.class)
                .block();

        List<DailyForecast> dailyForecasts = buildDailyForecast(forecastResponse);

        // 3) Combine into final response object
        WeatherData data = new WeatherData();
        data.setCity(current.getName());
        data.setCountry(current.getSys() != null ? current.getSys().getCountry() : "");
        data.setCurrentTemp(current.getMain().getTemp());
        data.setFeelsLike(current.getMain().getFeels_like());
        data.setHumidity(current.getMain().getHumidity());
        data.setWindSpeed(current.getWind() != null ? current.getWind().getSpeed() : 0);

        if (current.getWeather() != null && !current.getWeather().isEmpty()) {
            data.setDescription(current.getWeather().get(0).getDescription());
            data.setIcon(current.getWeather().get(0).getIcon());
        }

        data.setForecast(dailyForecasts);

        return data;
    }

    /**
     * OpenWeatherMap's free "forecast" endpoint returns data in 3-hour
     * intervals (40 entries for 5 days). This groups them by date and
     * picks min/max temp + a representative description per day.
     */
    private List<DailyForecast> buildDailyForecast(ForecastResponse response) {
        if (response == null || response.getList() == null) {
            return Collections.emptyList();
        }

        // Group entries by date (yyyy-MM-dd)
        Map<String, List<ForecastResponse.ForecastItem>> groupedByDay = response.getList().stream()
                .collect(Collectors.groupingBy(item -> item.getDt_txt().split(" ")[0], LinkedHashMap::new, Collectors.toList()));

        List<DailyForecast> result = new ArrayList<>();

        for (Map.Entry<String, List<ForecastResponse.ForecastItem>> entry : groupedByDay.entrySet()) {
            String date = entry.getKey();
            List<ForecastResponse.ForecastItem> items = entry.getValue();

            double minTemp = items.stream().mapToDouble(i -> i.getMain().getTemp_min()).min().orElse(0);
            double maxTemp = items.stream().mapToDouble(i -> i.getMain().getTemp_max()).max().orElse(0);
            int avgHumidity = (int) items.stream().mapToInt(i -> i.getMain().getHumidity()).average().orElse(0);

            // Prefer the entry closest to midday (12:00) as the "representative" icon/description
            ForecastResponse.ForecastItem midday = items.stream()
                    .filter(i -> i.getDt_txt().contains("12:00:00"))
                    .findFirst()
                    .orElse(items.get(items.size() / 2));

            String description = "";
            String icon = "";
            if (midday.getWeather() != null && !midday.getWeather().isEmpty()) {
                description = midday.getWeather().get(0).getDescription();
                icon = midday.getWeather().get(0).getIcon();
            }

            result.add(new DailyForecast(date, minTemp, maxTemp, avgHumidity, description, icon));
        }

        // Return only 5 days
        return result.stream().limit(5).collect(Collectors.toList());
    }
}
