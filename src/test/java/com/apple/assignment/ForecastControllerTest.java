package com.apple.assignment;

import lombok.Builder;
import okhttp3.mockwebserver.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(properties = {
        "server.address=localhost",
        "local.server.port=8080",
        "forecast.apiKey=testkey",
        "geocode.baseUrl = http://localhost:44444",
        "geocode.path = /v1/geocode",
        "weather.baseUrl = http://localhost:44444",
        "weather.path = /weather/1.0/report.json"},
        classes = WeatherApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class ForecastControllerTest {

    private static final String GEOCODE_PATH = "/v1/geocode";
    private static final String WEATHER_PATH = "/weather/1.0/report.json";

    @LocalServerPort
    private static int port = 44444;

    @Autowired
    MockMvc mockMvc;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(port);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testWhenZipcodeUsedWithMetric_thenValidResultReturned() throws Exception {

        mockWebServer
                .url("http://localhost:" + port);

        DownstreamResponses responses = DownstreamResponses.builder()
                .geoCodeRespose(getSuccessResponse("geocode200SingleResponse.json"))
                .weatherRespose(getSuccessResponse("weather200Response.json"))
                .forecastRespose(getSuccessResponse("forecast200Response.json"))
                .build();
        mockWebServer.setDispatcher(setupResponses(responses));

        String expoectedResponse = "{\"title\":\"Folsom, California, United States\",\"currentTemperature\":\"8.00\",\"highTemperature\":\"21.30\",\"lowTemperature\":\"4.30\",\"iconDescription\":\"Clear\",\"iconUrl\":\"https://weather.cc.api.here.com/static/weather/icon/16.png\",\"message\":null,\"cached\":\"false\",\"forecasts\":[{\"highTemperature\":\"8.50\",\"lowTemperature\":\"1.60\",\"skyDescription\":\"Overcast\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/17.png\",\"weekday\":\"Thursday\"},{\"highTemperature\":\"8.20\",\"lowTemperature\":\"0.90\",\"skyDescription\":\"Overcast\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/19.png\",\"weekday\":\"Friday\"},{\"highTemperature\":\"7.50\",\"lowTemperature\":\"-1.00\",\"skyDescription\":\"Morning clouds\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/27.png\",\"weekday\":\"Saturday\"},{\"highTemperature\":\"9.10\",\"lowTemperature\":\"-4.00\",\"skyDescription\":\"Sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/1.png\",\"weekday\":\"Sunday\"},{\"highTemperature\":\"7.70\",\"lowTemperature\":\"-1.10\",\"skyDescription\":\"Mostly sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/2.png\",\"weekday\":\"Monday\"},{\"highTemperature\":\"8.40\",\"lowTemperature\":\"-3.80\",\"skyDescription\":\"Sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/1.png\",\"weekday\":\"Tuesday\"},{\"highTemperature\":\"10.50\",\"lowTemperature\":\"-1.20\",\"skyDescription\":\"Sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/1.png\",\"weekday\":\"Wednesday\"}]}";

        mockMvc.perform(MockMvcRequestBuilders
                .get("/forecast/api")
                .param("query", "95630")
                .param("metric", "true"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expoectedResponse));

        RecordedRequest weatherRequest = mockWebServer.takeRequest();
        RecordedRequest forecastRequest = mockWebServer.takeRequest();

        Assertions.assertEquals("/weather/1.0/report.json?product=observation&apiKey=testkey&metric=true&oneobservation=true&zipcode=95630",weatherRequest.getPath());
        Assertions.assertEquals("/weather/1.0/report.json?apiKey=testkey&metric=true&oneobservation=true&product=forecast_7days_simple&name=Folsom,%20California,%20United%20States",forecastRequest.getPath());
    }

    @Test
    public void testWhenCityStateUsedWithMetric_thenValidResultReturned() throws Exception {

        mockWebServer
                .url("http://localhost:" + port);

        DownstreamResponses responses = DownstreamResponses.builder()
                .geoCodeRespose(getSuccessResponse("geocode200SingleResponse.json"))
                .weatherRespose(getSuccessResponse("weather200Response.json"))
                .forecastRespose(getSuccessResponse("forecast200Response.json"))
                .build();
        mockWebServer.setDispatcher(setupResponses(responses));

        String expoectedResponse = "{\"title\":\"Folsom, California, United States\",\"currentTemperature\":\"8.00\",\"highTemperature\":\"21.30\",\"lowTemperature\":\"4.30\",\"iconDescription\":\"Clear\",\"iconUrl\":\"https://weather.cc.api.here.com/static/weather/icon/16.png\",\"message\":null,\"cached\":\"false\",\"forecasts\":[{\"highTemperature\":\"8.50\",\"lowTemperature\":\"1.60\",\"skyDescription\":\"Overcast\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/17.png\",\"weekday\":\"Thursday\"},{\"highTemperature\":\"8.20\",\"lowTemperature\":\"0.90\",\"skyDescription\":\"Overcast\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/19.png\",\"weekday\":\"Friday\"},{\"highTemperature\":\"7.50\",\"lowTemperature\":\"-1.00\",\"skyDescription\":\"Morning clouds\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/27.png\",\"weekday\":\"Saturday\"},{\"highTemperature\":\"9.10\",\"lowTemperature\":\"-4.00\",\"skyDescription\":\"Sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/1.png\",\"weekday\":\"Sunday\"},{\"highTemperature\":\"7.70\",\"lowTemperature\":\"-1.10\",\"skyDescription\":\"Mostly sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/2.png\",\"weekday\":\"Monday\"},{\"highTemperature\":\"8.40\",\"lowTemperature\":\"-3.80\",\"skyDescription\":\"Sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/1.png\",\"weekday\":\"Tuesday\"},{\"highTemperature\":\"10.50\",\"lowTemperature\":\"-1.20\",\"skyDescription\":\"Sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/1.png\",\"weekday\":\"Wednesday\"}]}";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/forecast/api")
                        .param("query", "folsom,ca")
                        .param("metric", "true"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expoectedResponse));

        RecordedRequest weatherRequest = mockWebServer.takeRequest();
        RecordedRequest forecastRequest = mockWebServer.takeRequest();

        Assertions.assertEquals("/weather/1.0/report.json?product=observation&apiKey=testkey&metric=true&oneobservation=true&name=folsom,ca",weatherRequest.getPath());
        Assertions.assertEquals("/weather/1.0/report.json?apiKey=testkey&metric=true&oneobservation=true&product=forecast_7days_simple&name=Folsom,%20California,%20United%20States",forecastRequest.getPath());
    }

    @Test
    public void testWhenAddressUsedWithMetric_thenValidResultReturned() throws Exception {

        mockWebServer
                .url("http://localhost:" + port);

        DownstreamResponses responses = DownstreamResponses.builder()
                .geoCodeRespose(getSuccessResponse("geocode200SingleResponse.json"))
                .weatherRespose(getSuccessResponse("weather200Response.json"))
                .forecastRespose(getSuccessResponse("forecast200Response.json"))
                .build();
        mockWebServer.setDispatcher(setupResponses(responses));

        String expoectedResponse = "{\"title\":\"Folsom, CA, United States\",\"currentTemperature\":\"8.00\",\"highTemperature\":\"21.30\",\"lowTemperature\":\"4.30\",\"iconDescription\":\"Clear\",\"iconUrl\":\"https://weather.cc.api.here.com/static/weather/icon/16.png\",\"message\":null,\"cached\":\"false\",\"forecasts\":[{\"highTemperature\":\"8.50\",\"lowTemperature\":\"1.60\",\"skyDescription\":\"Overcast\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/17.png\",\"weekday\":\"Thursday\"},{\"highTemperature\":\"8.20\",\"lowTemperature\":\"0.90\",\"skyDescription\":\"Overcast\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/19.png\",\"weekday\":\"Friday\"},{\"highTemperature\":\"7.50\",\"lowTemperature\":\"-1.00\",\"skyDescription\":\"Morning clouds\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/27.png\",\"weekday\":\"Saturday\"},{\"highTemperature\":\"9.10\",\"lowTemperature\":\"-4.00\",\"skyDescription\":\"Sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/1.png\",\"weekday\":\"Sunday\"},{\"highTemperature\":\"7.70\",\"lowTemperature\":\"-1.10\",\"skyDescription\":\"Mostly sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/2.png\",\"weekday\":\"Monday\"},{\"highTemperature\":\"8.40\",\"lowTemperature\":\"-3.80\",\"skyDescription\":\"Sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/1.png\",\"weekday\":\"Tuesday\"},{\"highTemperature\":\"10.50\",\"lowTemperature\":\"-1.20\",\"skyDescription\":\"Sunny\",\"iconLink\":\"https://weather.cc.api.here.com/static/weather/icon/1.png\",\"weekday\":\"Wednesday\"}]}";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/forecast/api")
                        .param("query", "214 Colner cir, folsom, ca 95630")
                        .param("metric", "true"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(expoectedResponse));

        RecordedRequest geocodeRequest = mockWebServer.takeRequest();
        RecordedRequest weatherRequest = mockWebServer.takeRequest();
        RecordedRequest forecastRequest = mockWebServer.takeRequest();

        Assertions.assertEquals("/v1/geocode?q=214%20Colner%20cir,%20folsom,%20ca%2095630&apiKey=testkey&limit=1",geocodeRequest.getPath());
        Assertions.assertEquals("/weather/1.0/report.json?product=observation&apiKey=testkey&metric=true&oneobservation=true&latitude=38.68175&longitude=-121.16283",weatherRequest.getPath());
        Assertions.assertEquals("/weather/1.0/report.json?apiKey=testkey&metric=true&oneobservation=true&product=forecast_7days_simple&name=Folsom,%20CA,%20United%20States",forecastRequest.getPath());
    }


    private Dispatcher setupResponses(DownstreamResponses downstreamResponses ) {
        return new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                System.out.println(request.getPath());
                String requestPath = request.getPath().substring(0, request.getPath().lastIndexOf('?'));
                switch (requestPath) {
                    case GEOCODE_PATH:
                        return downstreamResponses.geoCodeRespose;
                    case WEATHER_PATH:
                         return request.getPath().contains("product=observation") ? downstreamResponses.weatherRespose : downstreamResponses.forecastRespose;
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };
    }

    @Builder
    private static class DownstreamResponses {
        MockResponse geoCodeRespose;
        MockResponse weatherRespose;
        MockResponse forecastRespose;
    }

    private MockResponse getSuccessResponse(String fileName) throws IOException {
        String s = Files.readString(Path.of((
                new File("src/test/resources/jsonFiles/" + fileName)).getAbsolutePath()));
        return new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(s);
    }

    private MockResponse getTimeOutResponse(String fileName) throws IOException {
        return new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setSocketPolicy(SocketPolicy.NO_RESPONSE);
    }
}
