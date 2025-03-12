package com.apple.assignment.service;

import com.apple.assignment.model.GeoCodeResponse;

public interface GeocodeService {

    public GeoCodeResponse getGeoCode(String address, String apiKey);

}
