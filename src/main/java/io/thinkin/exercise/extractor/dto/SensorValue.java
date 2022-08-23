package io.thinkin.exercise.extractor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder(setterPrefix = "with")
@Getter
@Setter
public class SensorValue {

    Integer value;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "s")
    Date timestamp;
}
