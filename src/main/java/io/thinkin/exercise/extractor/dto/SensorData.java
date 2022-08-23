package io.thinkin.exercise.extractor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SensorData {

    @JsonProperty(value = "sensor_id", required = true)
    private Integer sensorId;

    @JsonProperty(value = "value", required = true)
    private Integer value;

    @JsonProperty(value = "timestamp", required = true)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "s")
    private Date timestamp;
}
