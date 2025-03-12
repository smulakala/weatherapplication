package com.apple.assignment.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Setter
public class ForecastResponseVO implements Serializable {
    String title;
    String currentTemperature;
    String highTemperature;
    String lowTemperature;
    String iconDescription;
    String iconUrl;
    String message;
    String cached;
    List<Forecast> forecasts;
}
