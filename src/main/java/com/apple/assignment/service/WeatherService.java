package com.apple.assignment.service;

import com.apple.assignment.model.Item;
import com.apple.assignment.model.Position;
import com.apple.assignment.model.WeatherResponse;
import org.springframework.util.MultiValueMap;

public interface WeatherService {
    public WeatherResponse getWeather(MultiValueMap<String, String> parameters);
}
