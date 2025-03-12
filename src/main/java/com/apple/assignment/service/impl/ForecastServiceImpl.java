package com.apple.assignment.service.impl;

import com.apple.assignment.constants.WeatherConstants;
import com.apple.assignment.exception.AddressNotFoundException;
import com.apple.assignment.model.*;
import com.apple.assignment.service.ForecastService;
import com.apple.assignment.service.GeocodeService;
import com.apple.assignment.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service provides forecast for a given location. Interfaces with weather api and geocode api.
 */
@Service
@ConfigurationProperties(prefix = "forecast")
@Slf4j
public class ForecastServiceImpl implements ForecastService {

    GeocodeService geocodeService;
    WeatherService weatherService;

    @Value("${forecast.apiKey}")
    String apiKey;

    @Autowired
    public ForecastServiceImpl(GeocodeService geocodeService, WeatherService weatherService) {
        this.geocodeService = geocodeService;
        this.weatherService = weatherService;
    }

    /**
     * Main Service methos that pattern match the type and make calls to geocode and weather api to create
     * response.
     * @param address
     * @param metric
     * @param cacheKey
     * @return
     * @throws AddressNotFoundException
     */
    @Override
    @Cacheable(
            value = "forecastResponseCache",
            key = "#address")
    public ForcastResponse getForecast(String address, String metric, String cacheKey) throws AddressNotFoundException{

        List<WeatherResponse> weatherResponseList = new ArrayList<>();

        MultiValueMap<String, String> parameters = createBaseParameters(metric);

        //Block checks if the query is zipcode.
        if(doesMatch(address, WeatherConstants.ZIPCODE_PATTERN)){
            parameters.add("zipcode",address);
            weatherResponseList.add( weatherService.getWeather(parameters));
            parameters.remove("product");
            parameters.remove("zipcode");
        }
        //Block checks if the query is address. First gets lat and lng and then makes weather call.
        else if(doesMatch(address,WeatherConstants.ADDRESS_PATTERN)){
            List<Item> codeResponse = getGeoLocation(address, apiKey);
            weatherResponseList = codeResponse.stream()
                    .map(item -> {
                        parameters.add("latitude",Float.toString(item.getPosition().getLat()));
                        parameters.add("longitude",Float.toString(item.getPosition().getLng()));
                        WeatherResponse weatherResponse = weatherService.getWeather(parameters);
                        parameters.remove("latitude");
                        parameters.remove("longitude");
                        parameters.remove("product");
                        weatherResponse.setItem(item);
                        return weatherResponse;
                    })
                    .toList();
        }
        //Block checks if the query is City State format.
        else if(doesMatch(address, WeatherConstants.CITY_STATE_PATTERN))
        {
            parameters.add("name",address);
            weatherResponseList.add( weatherService.getWeather(parameters));
            parameters.remove("name");
            parameters.remove("product");
        }
        //Block to report that the address is not supported.
        else {
            weatherResponseList.add(WeatherResponse
                    .builder()
                    .message(WeatherConstants.ADDRESS_FORMAT_NOT_SUPPORTED)
                    .build());
        }
        //created the response.
        return ForcastResponse.builder()
                .cacheKey(cacheKey)
                .forecastResponseVO(createForecastResponse(weatherResponseList,parameters,cacheKey).get(0))
                .build();
    }

    private List<Item> getGeoLocation(String address, String apiKey) throws AddressNotFoundException {
        GeoCodeResponse geoCodeResponse = geocodeService.getGeoCode(address, apiKey);
        if(geoCodeResponse.getItems() == null || geoCodeResponse.getItems().isEmpty()) {
            throw new AddressNotFoundException(String.format(WeatherConstants.ADDRESS_NOT_FOUND_MESSAGE,address));
        }
        return geoCodeResponse.getItems();
    }

    /**
     * This method consolidates the data from weather, geocode and get the 7 day forecast and creates response.
     * @param weatherResponseList
     * @param parameters
     * @param cacheKey
     * @return
     */
    private List<ForecastResponseVO> createForecastResponse(List<WeatherResponse> weatherResponseList, MultiValueMap<String, String> parameters, String cacheKey) {
        final List<ForecastResponseVO> forecastResponseList = new ArrayList<>();
        weatherResponseList.forEach(weatherResponse -> {
            if(weatherResponse.getObservations() != null
                    && weatherResponse.getObservations().getLocation() != null
                    && !weatherResponse.getObservations().getLocation().isEmpty()) {

                parameters.add("product", "forecast_7days_simple");
                parameters.add("name", getTitle(weatherResponse));
                WeatherResponse forecastResponse = weatherService.getWeather(parameters);
                forecastResponseList.add( ForecastResponseVO.builder()
                        .highTemperature(weatherResponse.getObservations().getLocation().get(0).getObservation().get(0).getHighTemperature())
                        .lowTemperature(weatherResponse.getObservations().getLocation().get(0).getObservation().get(0).getLowTemperature())
                        .title(getTitle(weatherResponse))
                        .currentTemperature(weatherResponse.getObservations().getLocation().get(0).getObservation().get(0).getTemperature())
                        .iconDescription(weatherResponse.getObservations().getLocation().get(0).getObservation().get(0).getSkyDescription())
                        .iconUrl(weatherResponse.getObservations().getLocation().get(0).getObservation().get(0).getIconLink())
                        .forecasts(forecastResponse.getDailyForecasts().getForecastLocation().getForecast())
                        .build());
            }
        });
        if(forecastResponseList.isEmpty()){
            //If no weather data is found gracefully reports the message to the user.
            forecastResponseList.add(ForecastResponseVO.builder().message(WeatherConstants.ADDRESS_NOT_FOUND).build());
        }
        return forecastResponseList;
    }

    /**
     * Creates the common properties for the endpoint.
     * @param metric
     * @return
     */
    private MultiValueMap<String, String> createBaseParameters(String metric) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("product", "observation");
        parameters.add("apiKey", apiKey);
        parameters.add("metric", metric);
        parameters.add("oneobservation","true");
        return parameters;
    }

    /**
     * Provides display title based on diffetent types of observations.
     * @param weatherResponse
     * @return
     */
    private String getTitle(WeatherResponse weatherResponse)
    {
        StringBuilder titleBuilder = new StringBuilder();
        //In case of zipcode and city state we get the title from the weather api
        if(weatherResponse.getItem() == null || weatherResponse.getItem().getTitle() == null || weatherResponse.getItem().getTitle().isEmpty()) {
            titleBuilder.append( weatherResponse.getObservations().getLocation().get(0).getCity());
            titleBuilder.append(", ");
            titleBuilder.append( weatherResponse.getObservations().getLocation().get(0).getState());
            titleBuilder.append(", ");
            titleBuilder.append( weatherResponse.getObservations().getLocation().get(0).getCountry());
        }
        //In case of address search we get title from geocode item details.
        else {
            titleBuilder.append( weatherResponse.getItem().getTitle());
        }
        return titleBuilder.toString();
    }

    /**
     * provides functionality to identify if the supplied query is a zipcode, city state or address.
     * This method uses pattern supplied to make the determination.
     * @param query
     * @param pattern
     * @return
     */
    public static boolean doesMatch(String query, String pattern){
        return Pattern
                .compile(pattern)
                .matcher(query)
                .matches();
    }
}
