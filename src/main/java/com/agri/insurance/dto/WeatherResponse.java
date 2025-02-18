package com.agri.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class WeatherResponse {
    private List<Result> results;

    @Data
    public static class Result {
        private Location location;
        private List<Daily> daily;
        @JsonProperty("last_update")
        private String lastUpdate;
    }

    @Data
    public static class Location {
        private String id;
        private String name;
        private String country;
        private String path;
        private String timezone;
        @JsonProperty("timezone_offset")
        private String timezoneOffset;
    }

    @Data
    public static class Daily {
        private String date;
        @JsonProperty("text_day")
        private String textDay;
        @JsonProperty("code_day")
        private String codeDay;
        @JsonProperty("text_night")
        private String textNight;
        @JsonProperty("code_night")
        private String codeNight;
        private String high;
        private String low;
        private String rainfall;
        private String precip;
        @JsonProperty("wind_direction")
        private String windDirection;
        @JsonProperty("wind_direction_degree")
        private String windDirectionDegree;
        @JsonProperty("wind_speed")
        private String windSpeed;
        @JsonProperty("wind_scale")
        private String windScale;
        private String humidity;
    }
} 