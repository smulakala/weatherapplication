package com.apple.assignment.service.impl;

import com.apple.assignment.exception.WeatherDataAccessException;
import com.apple.assignment.model.WeatherResponse;
import com.apple.assignment.service.WeatherService;
import com.apple.assignment.util.WebclientContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * Weather Endpoint method that server the weather search functionality.
 */
@Service
public class WeatherServiceImpl implements WeatherService {

    WebClient weatherClient;

    @Value("${weather.path}")
    String path;

    @Autowired
    public WeatherServiceImpl(@Value("${weather.baseUrl}") String baseUrl) {
        this.weatherClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient
                                .create()
                                .secure(spec -> spec.sslContext(WebclientContextUtil.getSslContext()))))
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * The method retrives the forecast based on the parameters provided.
     * @param parameters
     * @return
     */
    @Override
    public WeatherResponse getWeather(MultiValueMap<String, String> parameters) {

        WeatherResponse weatherResponse = weatherClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParams(parameters)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class).map(WeatherDataAccessException::new))
                .bodyToMono(WeatherResponse.class)
                .block();
        return weatherResponse;
    }

}
