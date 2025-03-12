package com.apple.assignment.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class ForcastResponse implements Serializable {
    ForecastResponseVO forecastResponseVO;
    String cacheKey;
}
