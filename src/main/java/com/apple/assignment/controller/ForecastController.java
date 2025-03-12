package com.apple.assignment.controller;

import com.apple.assignment.exception.AddressNotFoundException;
import com.apple.assignment.model.ForcastResponse;
import com.apple.assignment.model.ForecastResponseVO;
import com.apple.assignment.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Provides forecast api endpoint.
 */
@RestController
@RequestMapping("/forecast")
public class ForecastController {

    ForecastService forecastService;

    @Autowired
    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    /**
     * Performs forecast search based on the zipcode, city state, address.
     * @param query
     * @param metric
     * @return
     * @throws AddressNotFoundException
     */
    @GetMapping("/api")
    ForecastResponseVO getForecast(@RequestParam String query, @RequestParam String metric) throws AddressNotFoundException {

       String cacheKey = UUID.randomUUID().toString();
        //Get the forecast.
       ForcastResponse forcastResponse = forecastService.getForecast(query, metric, cacheKey);

       /*
        *Check to see if the passed cache key matches with the one in response.
        * if matches means actual query to external api has happened. Flag is disabled.
        */
       forcastResponse.getForecastResponseVO().setCached(cacheKey.equalsIgnoreCase(forcastResponse.getCacheKey()) ? "false" : "true");

        return forcastResponse.getForecastResponseVO();
    }
}
