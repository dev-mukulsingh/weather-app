package com.weatherapp.weatherapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.time.Duration;

/**
 * Configures the WebClient used to call OpenWeatherMap.
 *
 * Many corporate / college networks require an HTTP(S) proxy for any
 * outbound internet access. Tools like `curl` pick this up automatically
 * from the http_proxy / https_proxy environment variables, but Java's
 * reactor-netty based WebClient does NOT do this automatically — so we
 * detect and apply it manually here.
 *
 * You can also set it explicitly in application.properties:
 *   app.proxy.host=192.0.0.4
 *   app.proxy.port=8080
 */
@Configuration
public class WebClientConfig {

    @Value("${app.proxy.host:}")
    private String configuredProxyHost;

    @Value("${app.proxy.port:0}")
    private int configuredProxyPort;

    @Bean
    public WebClient.Builder webClientBuilder() {

        String host = configuredProxyHost;
        int port = configuredProxyPort;

        // Fall back to system environment variables if not set explicitly
        if (host == null || host.isBlank()) {
            String envProxy = firstNonBlank(
                    System.getenv("https_proxy"),
                    System.getenv("HTTPS_PROXY"),
                    System.getenv("http_proxy"),
                    System.getenv("HTTP_PROXY")
            );

            if (envProxy != null) {
                try {
                    URI proxyUri = URI.create(envProxy);
                    host = proxyUri.getHost();
                    port = proxyUri.getPort() != -1 ? proxyUri.getPort() : 80;
                } catch (Exception ignored) {
                    // If parsing fails, just proceed without a proxy
                }
            }
        }

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(15));

        if (host != null && !host.isBlank() && port > 0) {
            final String finalHost = host;
            final int finalPort = port;
            System.out.println("[WeatherApp] Routing outbound API calls through proxy "
                    + finalHost + ":" + finalPort);

            httpClient = httpClient.proxy(proxySpec -> proxySpec
                    .type(ProxyProvider.Proxy.HTTP)
                    .host(finalHost)
                    .port(finalPort));
        }

        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
