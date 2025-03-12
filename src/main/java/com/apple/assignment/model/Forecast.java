package com.apple.assignment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Forecast implements Serializable {
    String highTemperature;
    String lowTemperature;
    String skyDescription;
    String iconLink;
    String weekday;
}
