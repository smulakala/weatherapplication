package com.apple.assignment.handler;

import com.apple.assignment.exception.GeoCodeDataAccessException;
import com.apple.assignment.exception.WeatherDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class WeatherAppExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles the exceptions at the global level.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value
            = { WeatherDataAccessException.class, GeoCodeDataAccessException.class })
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Error retrieving weather data";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
