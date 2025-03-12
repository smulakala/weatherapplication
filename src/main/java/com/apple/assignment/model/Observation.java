package com.apple.assignment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Getter
@Setter
@Builder
public class Observation {

    String highTemperature;
    String lowTemperature;
    String temperature;
    String skyDescription;
    String iconLink;

}
