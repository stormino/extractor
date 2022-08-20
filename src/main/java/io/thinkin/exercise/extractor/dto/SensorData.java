package io.thinkin.exercise.extractor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class SensorData {

    @JsonProperty(value = "sensor_id", required = true)
    private Integer id;

    @JsonProperty(value = "value", required = true)
    private Integer value;

    @JsonProperty(value = "timestamp", required = true)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "s")
    private Date timestamp;
}
