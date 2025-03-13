package com.apple.assignment.service.impl;

import com.apple.assignment.exception.GeoCodeDataAccessException;
import com.apple.assignment.model.GeoCodeResponse;
import com.apple.assignment.service.GeocodeService;
import com.apple.assignment.util.WebclientContextUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Service
@Slf4j
public class GeocodeServiceImpl implements GeocodeService {

    WebClient geoCodeClient;

    @Value("${geocode.path}")
    String path;

    public GeocodeServiceImpl(@Value("${geocode.baseUrl}") String baseUrl) {
        this.geoCodeClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .secure(spec -> spec.sslContext(WebclientContextUtil.getSslContext()))))
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * The method provides latitude and longitude for a given address.
     * @param address
     * @param apiKey
     * @return
     */
    @Override
    @Retry(name="retryGeocodeApi", fallbackMethod = "fallBackGeocodeRetry")
    @CircuitBreaker(name = "geocodeCBService")
    public GeoCodeResponse getGeoCode(String address, String apiKey) {

        return geoCodeClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("q", address)
                        .queryParam("apiKey", apiKey)
                        .queryParam("limit",1)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class).map(GeoCodeDataAccessException::new))
                .bodyToMono(GeoCodeResponse.class)
                .block();
    }
    public GeoCodeResponse fallBackGeocodeRetry(String address, String apiKey,Exception ex) {
        log.info("fallBackGeocodeRetry : {}", ex.getMessage());
        return GeoCodeResponse
                .builder()
                .message("Geocode API Endpoit is not available : "+ex.getMessage())
                .build();
    }
}
