# Weather App

A simple weather forecast web application built with Java Spring Boot. It fetches real-time weather data from the OpenWeatherMap API and displays the current weather along with a 5-day forecast for any city.

This project was built during my internship to practice REST API integration, JSON parsing, and building a full-stack Java web application.

## Features

- Search weather by city name
- Displays current temperature, humidity, wind speed, and conditions
- 5-day forecast
- REST API endpoint that returns weather data as JSON
- Simple web interface built with HTML, CSS, and JavaScript

## Tech Stack

- Java 17
- Spring Boot 3
- Spring WebFlux (WebClient) for calling the external API
- Jackson for JSON parsing
- Thymeleaf for serving the HTML page
- OpenWeatherMap API
- HTML, CSS, JavaScript (frontend)

## Project Structure

```
weather-app/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/weatherapp/weatherapp/
    │   ├── WeatherappApplication.java     - main class
    │   ├── config/
    │   │   └── WebClientConfig.java       - configures the HTTP client (with proxy support)
    │   ├── controller/
    │   │   ├── WeatherRestController.java - REST endpoint (/api/weather)
    │   │   └── PageController.java        - serves the home page
    │   ├── service/
    │   │   └── WeatherService.java        - calls the OpenWeatherMap API and processes the response
    │   ├── dto/
    │   │   ├── CurrentWeatherResponse.java - maps the current weather JSON response
    │   │   └── ForecastResponse.java       - maps the 5-day forecast JSON response
    │   └── model/
    │       ├── WeatherData.java           - final response sent to the frontend
    │       └── DailyForecast.java         - one day of forecast data
    └── resources/
        ├── application.properties         - configuration (API key, port, etc.)
        ├── templates/index.html
        └── static/
            ├── css/style.css
            └── js/app.js
```

## Setup Instructions

### 1. Requirements

- Java 17 or higher
- Maven

### 2. Get an API key

This project uses the OpenWeatherMap API to fetch weather data.

1. Create a free account at https://openweathermap.org/
2. Go to "My API Keys" and copy your key
3. Note: a new API key can take up to a couple of hours to activate

### 3. Configure the API key

Open `src/main/resources/application.properties` and set your key:

```properties
openweathermap.api.key=YOUR_API_KEY_HERE
```

### 4. Run the application

```bash
mvn clean install
mvn spring-boot:run
```

The app will start on:

```
http://localhost:8080
```

### 5. Using the app

Type a city name in the search box and click Search. The current weather and a 5-day forecast will be displayed.

You can also call the API directly:

```
GET http://localhost:8080/api/weather?city=London
```

This returns the weather data as JSON.

## Notes

- If you're on a network that requires a proxy for internet access, the app will try to auto-detect it from the `http_proxy` / `https_proxy` environment variables. You can also set it manually in `application.properties` using `app.proxy.host` and `app.proxy.port`.
- If you get an "Invalid API key" error right after generating a new key, wait a while and try again — new keys take some time to activate.

## Possible Improvements

- Add unit tests
- Add caching so repeated searches for the same city don't call the API every time
- Add support for switching between Celsius and Fahrenheit
- Store recent searches
