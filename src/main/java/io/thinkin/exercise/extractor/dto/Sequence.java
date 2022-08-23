package io.thinkin.exercise.extractor.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder(setterPrefix = "with")
@Getter
@Setter
public class Sequence {

    private Integer sensorId;

    private List<SensorValue> values;
}
