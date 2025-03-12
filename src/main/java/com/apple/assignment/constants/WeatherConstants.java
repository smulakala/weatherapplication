package com.apple.assignment.constants;

/**
 * Holds all the constants.
 */
public class WeatherConstants {
    public static final String ZIPCODE_PATTERN = "^[0-9]{5}(?:-[0-9]{4})?$";
    public static final String ADDRESS_PATTERN = "^(\\d{1,}) [a-zA-Z0-9\\s]+(\\,)? [a-zA-Z]+(\\,)? [A-Za-z]{2}\\s?(\\d{5}(-\\d{4})?)?$";
    public static final String CITY_STATE_PATTERN = "^[a-zA-Z]+(\\,)?\\s?[A-Za-z]{2}\\s?(\\d{5}(-\\d{4})?)?$";
    public static final String ADDRESS_NOT_FOUND = "No Weather Forecast Found.";
    public static final String ADDRESS_FORMAT_NOT_SUPPORTED = "The provided address is not supported";
    public static final String ADDRESS_NOT_FOUND_MESSAGE = "Address %s Not Found!";
}
