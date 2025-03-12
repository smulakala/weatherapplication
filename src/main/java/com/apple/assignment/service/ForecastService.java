package com.apple.assignment.service;

import com.apple.assignment.exception.AddressNotFoundException;
import com.apple.assignment.model.ForcastResponse;
import com.apple.assignment.model.ForecastResponseVO;

import java.util.List;

public interface ForecastService {

    ForcastResponse getForecast(String address, String metric, String cacheKey) throws AddressNotFoundException;
}
